package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.android.volley.VolleyError;
import com.aut.yuxiang.lbs_middleware.lbs_db.DBHelper;
import com.aut.yuxiang.lbs_middleware.lbs_db.SQL.CellTowerReadingsTable;
import com.aut.yuxiang.lbs_middleware.lbs_db.SQL.GPSReadingsTable;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity.CellTower;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationResponseEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.net_api.GeoLocationAPI;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.Accuracy;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.ProviderUsabilityDetector;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.MechanismFactory.CELL_TOWER_MECHANISM;

/**
 * Created by yuxiang on 13/12/16.
 */

public class CellTowerMechanism extends Mechanism {
    private static final String TAG = "CellTowerMechanism";
    private static final int GSM = 0;
    private static final int LTE = 1;
    private static final int CDMA = 2;
    private static final int WCDMA = 3;
    private static final int LATEST_LOCATIONS_NUMBER = 2;

    private Context context;
    private int mcc;
    private int mnc;
    private int lac;
    private int cid;
    private int sid;
    private TelephonyManager telephonyManager;
    private NetRequestInterface netRequestInterface;
    private Timer timer;
    private List<CellInfo> allCellInfo;
    private boolean running = false;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private MyPhoneStateListener cellLocationListener;

    public CellTowerMechanism(Context context, LBSLocationListener listener, AdapterProviderUsabilityListener usabilityListener, PolicyReferenceValues values) {
        super(context, listener, usabilityListener, values);
        this.context = context;
        cellLocationListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(cellLocationListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        netRequestInterface = new GeoLocationInterface();
        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        getOperatorInfo();

    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCellLocationChanged(CellLocation location) {
            requestLocationToNetAPI();
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            requestLocationToNetAPI();
        }

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            requestLocationToNetAPI();
        }
    }


    @Override
    public String getMechanismName() {

        return CELL_TOWER_MECHANISM;
    }

    @Override
    public void startMechanismOneTime() {
        LogHelper.showLog(TAG, "Start Cell Tower Mechanism Onc Time");
        requestLocationToNetAPI();
    }

    @Override
    public void stopMechanism() {
        LogHelper.showLog(TAG, "Stop Cell Tower Mechanism.");
        if (running) {
            if (timer != null) timer.cancel();
            closeDB();
            telephonyManager.listen(cellLocationListener, PhoneStateListener.LISTEN_NONE);
            LBS.getInstance().startDetect(context, values);
        }
        running = false;
    }

    @Override
    public void startMechanism() {
        super.startMechanism();
        if (!running) {
            LogHelper.showLog(TAG, "Start Cell Tower Mechanism.");
            clearCache();
            requestLocationToNetAPI();
        }
        running = true;
    }


    private void sendRequestDelay() {
        if (!checkOtherProviders()) {
            double averageSpeed = getAverageSpeed();
            LogHelper.showLog(TAG, "Average Speed: " + averageSpeed);
            if (averageSpeed < 1) {
                stopMechanism();
            } else {
                scheduleTimerForSendingRequest(getInterval(averageSpeed));
            }
        }
    }

    private void scheduleTimerForSendingRequest(long delayTime) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                requestLocationToNetAPI();
                timer.cancel();
            }
        };
        timer.schedule(task, delayTime);
    }

    private long getInterval(double speed) {
        return 1000 * 10;
    }

    private void getOperatorInfo() {
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
            cid = cdmaCellLocation.getBaseStationId();
            lac = cdmaCellLocation.getNetworkId();
            sid = cdmaCellLocation.getSystemId();
        } else {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            cid = gsmCellLocation.getCid();
            lac = gsmCellLocation.getLac();
        }
        String operator = telephonyManager.getNetworkOperator();
        mcc = Integer.valueOf(operator.substring(0, 3));
        mnc = Integer.valueOf(operator.substring(3));
        allCellInfo = telephonyManager.getAllCellInfo();
    }

    private String getRadioType(int phoneType) {
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                return "gsm";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "cdma";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "sip";
            default:
                return "none";
        }
    }

    private int getCellInfoType(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return GSM;
        } else if (cellInfo instanceof CellInfoCdma) {
            return CDMA;

        } else if (cellInfo instanceof CellInfoLte) {
            return LTE;
        } else if (cellInfo instanceof CellInfoWcdma) {
            return WCDMA;
        } else {
            return -1;
        }
    }


    private GeolocationRequestEntity createRequestEntity() {
        String operator = telephonyManager.getNetworkOperator();
        GeolocationRequestEntity entity = new GeolocationRequestEntity();
        entity.setHomeMobileCountryCode(Integer.valueOf(mcc));
        entity.setHomeMobileNetworkCode(Integer.valueOf(mnc));
        entity.setRadioType(getRadioType(telephonyManager.getPhoneType()));
        entity.setCarrier(telephonyManager.getNetworkOperatorName());
        entity.setConsiderIp("true");
        if (allCellInfo != null && allCellInfo.size() > 0) {
            ArrayList<CellTower> cellTowers = new ArrayList<>();
            for (CellInfo cellInfo : allCellInfo) {
                int cellId = 0;
                int lac = 0;
                int mcc = 0;
                int mnc = 0;
                int dbm = 0;
                if (getCellInfoType(cellInfo) == GSM) {
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                    cellId = cellInfoGsm.getCellIdentity().getCid();
                    lac = cellInfoGsm.getCellIdentity().getLac();
                    mcc = cellInfoGsm.getCellIdentity().getMcc();
                    mnc = cellInfoGsm.getCellIdentity().getMnc();
                    dbm = cellInfoGsm.getCellSignalStrength().getDbm();
                } else if (getCellInfoType(cellInfo) == CDMA) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                    cellId = cellInfoCdma.getCellIdentity().getBasestationId();
                    lac = cellInfoCdma.getCellIdentity().getNetworkId();
                    mcc = Integer.valueOf(operator.substring(0, 3));
                    mnc = cellInfoCdma.getCellIdentity().getSystemId();
                } else if (getCellInfoType(cellInfo) == LTE) {
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    cellId = cellInfoLte.getCellIdentity().getPci();
                    mcc = cellInfoLte.getCellIdentity().getMcc();
                    mnc = cellInfoLte.getCellIdentity().getMnc();
                    dbm = cellInfoLte.getCellSignalStrength().getDbm();
//                    lac = this.lac;
                    continue;
                } else if (getCellInfoType(cellInfo) == WCDMA) {
                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                    cellId = cellInfoWcdma.getCellIdentity().getCid();
                    lac = cellInfoWcdma.getCellIdentity().getLac();
                    mcc = cellInfoWcdma.getCellIdentity().getMcc();
                    mnc = cellInfoWcdma.getCellIdentity().getMnc();
                    dbm = cellInfoWcdma.getCellSignalStrength().getDbm();
                }
                CellTower cellTower = new CellTower();
                cellTower.setCellId(cellId);
                cellTower.setLocationAreaCode(lac);
                cellTower.setMobileCountryCode(mcc);
                cellTower.setMobileNetworkCode(mnc);
                cellTower.setAge(0);
                cellTower.setSignalStrength(dbm);
//                cellTower.setTimingAdvance(15);

                cellTowers.add(cellTower);
            }
            entity.setCellTowers(cellTowers);
        }
        return entity;

    }

    private void requestLocationToNetAPI() {
        GeoLocationAPI api = new GeoLocationAPI(context, netRequestInterface);
        api.sendAPI(createRequestEntity());
    }

    class GeoLocationInterface implements NetRequestInterface {

        @Override
        public void onResponse(Object response) {
            if (response instanceof GeolocationResponseEntity) {
                GeolocationResponseEntity entity = (GeolocationResponseEntity) response;
                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                location.setTime(System.currentTimeMillis());
                location.setLongitude(entity.getLocation().getLng());
                location.setLatitude(entity.getLocation().getLat());
                location.setAccuracy(entity.getAccuracy());
                saveCache(location);
                listener.onLocationUpdated(location);
                if (running) {
                    sendRequestDelay();
                }
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            if (running) {
//                stopMechanism();
                listener.onLocationUpdated(null);
                sendRequestDelay();
            }
        }
    }

    private boolean checkOtherProviders() {
        boolean result;
        if (result = (ProviderUsabilityDetector.getGPSUsability()&&values.accuracy== Accuracy.HIGH_LEVEL_ACCURACY)) {
                usabilityListener.onProviderAble(false);
        }
        return result;
    }

    private ArrayList<Location> getLatestCellTowerLocationsFromDB(int limitNum) {
        String table = CellTowerReadingsTable.CELL_TOWER_READINGS_TABLE_NAME;
        String[] columns = {CellTowerReadingsTable.LATITUDE, CellTowerReadingsTable.LONGITUDE, CellTowerReadingsTable.TIME_STAMP};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = CellTowerReadingsTable.TIME_STAMP + " DESC";
        String limit = String.valueOf(limitNum);
        Cursor cursor = sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        ArrayList<Location> result = null;
        if (cursor != null) {
            if (cursor.getCount() < limitNum) {
                return null;
            }
            result = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                double latitude = cursor.getDouble(cursor.getColumnIndex(CellTowerReadingsTable.LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(CellTowerReadingsTable.LONGITUDE));
                long time = cursor.getLong(cursor.getColumnIndex(CellTowerReadingsTable.TIME_STAMP));
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setTime(time);
//                if (System.currentTimeMillis() - location.getTime() < ACCEPTED_LOCATION_TIME)
                {
                    result.add(location);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    private long saveCache(Location location) {
        long execution;
        if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(GPSReadingsTable.ALTITUDE, location.getAltitude());
        contentValues.put(GPSReadingsTable.LATITUDE, location.getLatitude());
        contentValues.put(GPSReadingsTable.LONGITUDE, location.getLongitude());
        contentValues.put(GPSReadingsTable.ACCURACY, location.getAccuracy());
        contentValues.put(GPSReadingsTable.TIME_STAMP, location.getTime());
        execution = sqLiteDatabase.
                insert(CellTowerReadingsTable.CELL_TOWER_READINGS_TABLE_NAME, null, contentValues);
        return execution;
    }

    private int clearCache() {
        if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        int execution = sqLiteDatabase.delete(GPSReadingsTable.GPS_READINGS_TABLE_NAME, null, null);
        LogHelper.showLog(TAG, "Clear cached items: " + execution);
        return execution;
    }

    private void closeDB() {
        sqLiteDatabase.close();
        dbHelper.close();
    }


    private double getAverageSpeed() {
        double sumOfSpeed = 0;
        ArrayList<Location> latestLocationsList = getLatestCellTowerLocationsFromDB(LATEST_LOCATIONS_NUMBER);
        if (latestLocationsList != null && latestLocationsList.size() == LATEST_LOCATIONS_NUMBER) {
            for (int i = 0; i < latestLocationsList.size() - 1; i += 1) {
                Location fistLoc = latestLocationsList.get(i);
                Location secLoc = latestLocationsList.get(i + 1);
                double distance = getDistance(fistLoc, secLoc);
                double time = (fistLoc.getTime() - secLoc.getTime()) / 1000;
                sumOfSpeed += distance / time;
            }
            return sumOfSpeed / (double) latestLocationsList.size();
        } else {
            return Double.MAX_VALUE;
        }
    }

    private double getDistance(Location firstLocation, Location secondLocation) {
        return firstLocation.distanceTo(secondLocation);
    }


}
