package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.Context;
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
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity.CellTower;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationResponseEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.net_api.GeoLocationAPI;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;

import java.util.ArrayList;
import java.util.List;

import static com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.MechanismManager.CELL_TOWER_MECHANISM;

/**
 * Created by yuxiang on 13/12/16.
 */

public class CellTowerMechanism extends Mechanism {
    private static final String TAG = "CellTowerMechanism";
    private static final int GSM = 0;
    private static final int LTE = 1;
    private static final int CDMA = 2;
    private static final int WCDMA = 3;

    private Context context;
    private int mcc;
    private int mnc;
    private int lac;
    private int cid;
    private int sid;
    private TelephonyManager telephonyManager;
    private NetRequestInterface netRequestInterface;

    private List<CellInfo> allCellInfo;

    public CellTowerMechanism(Context context, LBSLocationListener listener, AdapterProviderUsabilityListener usabilityListener, PolicyReferenceValues values) {
        super(context, listener, usabilityListener, values);
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CELL_LOCATION);
        netRequestInterface = new GeoLocationInterface();
        getOperatorInfo();
        requestLocationToNetAPI();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCellLocationChanged(CellLocation location) {
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        }

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
        }
    }


    @Override
    public String getMechanismName() {
        return CELL_TOWER_MECHANISM;
    }

    @Override
    public void startMechanismOneTime() {

    }

    @Override
    public void stopMechanism() {

    }

    @Override
    public void startMechanism() {
        super.startMechanism();
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
                listener.onLocationUpdated(location);
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }


}
