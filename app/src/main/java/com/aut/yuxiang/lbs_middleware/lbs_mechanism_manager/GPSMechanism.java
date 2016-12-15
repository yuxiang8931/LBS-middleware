package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.aut.yuxiang.lbs_middleware.lbs_db.DBHelper;
import com.aut.yuxiang.lbs_middleware.lbs_db.SQL.GPSReadingsTable;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.Mechanism;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

/**
 * Created by yuxiang on 13/12/16.
 */

public class GPSMechanism extends Mechanism {
    private static final String TAG = "GPSMechanism";
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private boolean running = false;

    public GPSMechanism(Context context, final LBSLocationListener listener, final AdapterProviderUsabilityListener usabilityListener) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) throws SecurityException{
                LogHelper.showLog(TAG, "onLocationChanged");
                saveCache(location);
                listener.onLocationUpdated(location);
                if (!running)
                {
                    LogHelper.showLog(TAG, "Stop Mechanism One Time");
                    locationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                LogHelper.showLog(TAG, "onStatusChanged: " + s + "  i: " + i);
            }

            @Override
            public void onProviderEnabled(String s) {
                LogHelper.showLog(TAG, "onProviderEnabled: " + s);
                if (running)
                {
                    usabilityListener.onProviderAble(false);
                }
                else
                {
                    usabilityListener.onProviderAble(true);
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                LogHelper.showLog(TAG, "onProviderDisabled: " + s);
                if (running)
                {
                    usabilityListener.onProviderDisabled(false);
                }
                else
                {
                    usabilityListener.onProviderDisabled(true);
                }

            }
        };
        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }


    @Override
    public String getMechanismName() {
        return MechanismManager.GPS_MECHANISM;
    }

    @Override
    public void startMechanism() throws SecurityException {
        super.startMechanism();
        LogHelper.showLog(TAG, "Start GPS Mechanism");
        clearCache();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        running = true;
    }

    public void startMechanismOneTime() throws SecurityException
    {
        LogHelper.showLog(TAG, "Start GPS Mechanism One Time");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void stopMechanism() throws SecurityException {
        LogHelper.showLog(TAG, "Stop GPS Mechanism");
        locationManager.removeUpdates(locationListener);
        closeDB();
        running = false;
    }

    private long getMinUpdateInterval() {
        return 0;
    }

    private float getMinUpdateDistance() {
        return 0f;
    }

    private float getSpeed() {

        return 0;
    }


    private long saveCache(Location location) {
        long execution = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(GPSReadingsTable.ALTITUDE, location.getAltitude());
        contentValues.put(GPSReadingsTable.LATITUDE, location.getLatitude());
        contentValues.put(GPSReadingsTable.LONGITUDE, location.getLongitude());
        contentValues.put(GPSReadingsTable.ACCURACY, location.getAccuracy());
        contentValues.put(GPSReadingsTable.TIME_STAMP, location.getTime());
        execution =  sqLiteDatabase.
                insert(GPSReadingsTable.GPS_READINGS_TABLE_NAME, null, contentValues);
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
}
