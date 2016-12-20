package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.aut.yuxiang.lbs_middleware.lbs_db.DBHelper;
import com.aut.yuxiang.lbs_middleware.lbs_db.SQL.GPSReadingsTable;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

import java.util.ArrayList;

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
    private static final int LATEST_LOCATIONS_NUMBER = 5;
    private static final long ACCEPTED_LOCATION_TIME = 20 * 1000;
    private static final double EARTH_RADIUS = 6376.5*1000;
    private Context context;
    private PolicyReferenceValues values;

    public GPSMechanism() {
    }



    public GPSMechanism(Context context, final LBSLocationListener listener, final AdapterProviderUsabilityListener usabilityListener, PolicyReferenceValues values) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        this.values = values;
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) throws SecurityException {
                LogHelper.showLog(TAG, "onLocationChanged");
                saveCache(location);
                listener.onLocationUpdated(location);
                resetRequestLocationUpdatesListener();
                if (!running) {
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
                if (running) {
                    usabilityListener.onProviderAble(false);
                } else {
                    usabilityListener.onProviderAble(true);
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                LogHelper.showLog(TAG, "onProviderDisabled: " + s);
                if (running) {
                    usabilityListener.onProviderDisabled(false);
                } else {
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

    public void startMechanismOneTime() throws SecurityException {
        LogHelper.showLog(TAG, "Start GPS Mechanism One Time");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void stopMechanism() throws SecurityException {
        LogHelper.showLog(TAG, "Stop GPS Mechanism");
        locationManager.removeUpdates(locationListener);
        closeDB();
        if (running)
        {
            LBS.getInstance().startDetect(context, values);
        }
        running = false;
    }


    private void resetRequestLocationUpdatesListener() throws SecurityException {
        LogHelper.showLog(TAG, "Reset Location Listener");
        double currentAverageSpeed = getAverageSpeed();
        if (currentAverageSpeed == 0) {
            stopMechanism();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getMinUpdateInterval(currentAverageSpeed), getMinUpdateDistance(), locationListener);
        }

    }

    private long getMinUpdateInterval(double currentAverageSpeed) {
        if (currentAverageSpeed > 10) {
            return 0;
        } else {
            return 5;
        }
    }

    private float getMinUpdateDistance() {

        return 10f;
    }

    private double getAverageSpeed() {
        double sumOfSpeed = 0;
        ArrayList<Location> latestLocationsList = getLatestGPSLocationsFromDB(LATEST_LOCATIONS_NUMBER);
        if (latestLocationsList != null && latestLocationsList.size() == LATEST_LOCATIONS_NUMBER) {
            for (int i = 0; i < latestLocationsList.size() - 1; i += 2) {
                Location fistLoc = latestLocationsList.get(i);
                Location secLoc = latestLocationsList.get(i + 1);
                double distance = getDistance(fistLoc, secLoc);
                long time = secLoc.getTime() - fistLoc.getTime();
                sumOfSpeed += distance / time;
            }
            LogHelper.showLog(TAG, "Speed: " + sumOfSpeed);
            return sumOfSpeed / latestLocationsList.size();
        } else {
            return Double.MAX_VALUE;
        }

    }

    private ArrayList<Location> getLatestGPSLocationsFromDB(int limitNum) {
        String table = GPSReadingsTable.GPS_READINGS_TABLE_NAME;
        String[] columns = {GPSReadingsTable.LATITUDE, GPSReadingsTable.LONGITUDE, GPSReadingsTable.TIME_STAMP};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = GPSReadingsTable.TIME_STAMP + " DESC";
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
                Location location = new Location(LocationManager.GPS_PROVIDER);
                double latitude = cursor.getDouble(cursor.getColumnIndex(GPSReadingsTable.LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(GPSReadingsTable.LONGITUDE));
                long time = cursor.getLong(cursor.getColumnIndex(GPSReadingsTable.TIME_STAMP));
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

    public double getDistance(Location firstLocation, Location secondLocation) {
        if (firstLocation == null || secondLocation == null){
            System.out.println("invalid location message");
            return 0;
        }
        double distance = 0;
        //我们的算法：假设地球是一个球体
        double long1, long2, lati1,lati2;
        long1  = firstLocation.getLongitude()*Math.PI/180;
        lati1 = firstLocation.getLatitude()*Math.PI/180;
        long2 = secondLocation.getLongitude()*Math.PI/180;
        lati2 = secondLocation.getAltitude()*Math.PI/180;
        double temp;
        temp = Math.cos(lati1)*Math.cos(lati2)* Math.cos(long1-long2)+Math.sin(lati1)*Math.sin(lati2);
        distance = EARTH_RADIUS * Math.acos(temp);
        System.out.println("my distance" + distance);
        float[] result = new float[3];

        //系统算法：distanceTo. 这个算法语句最少
//        distance = (double)firstLocation.distanceTo(secondLocation);

        //系统算法：distanceBetween
//        long1 = 0d;
//        long2 = 0d;
//        lati1 = Math.PI/2;
//        lati2 = 0;
//        Location.distanceBetween(lati1,long1,lati2,long2,result);
//        System.out.println("system distance:" + result[0]);
//        distance = (double)result[0];

        return distance;
    }


    private long saveCache(Location location) {
        long execution = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(GPSReadingsTable.ALTITUDE, location.getAltitude());
        contentValues.put(GPSReadingsTable.LATITUDE, location.getLatitude());
        contentValues.put(GPSReadingsTable.LONGITUDE, location.getLongitude());
        contentValues.put(GPSReadingsTable.ACCURACY, location.getAccuracy());
        contentValues.put(GPSReadingsTable.TIME_STAMP, location.getTime());
        execution = sqLiteDatabase.
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
