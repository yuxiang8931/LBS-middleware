package com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

/**
 * Created by yuxiang on 8/12/16.
 */

public class ProviderUsabilityDetector {
    private static final String TAG = "ProviderUsabilityDetector";
    private static Context context;

    protected ProviderUsabilityDetector(Context context) {
        this.context = context;

    }

    static public boolean getGPSUsability() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    static public boolean getWifiUsability() {
        ConnectivityManager connMgr = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    static public boolean getAccelerometerUsability() {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return mSensor != null;
    }

    static public boolean getCellTowerUsability() {
        TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
        return location != null&&isOnline();
    }

   static public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    protected PolicyReferenceValues detectProviderUsability(PolicyReferenceValues policyReferenceValues) {
        boolean isGPSAvailable = getGPSUsability();
        boolean isWIFIAvailable = getWifiUsability();
        boolean isAccelerometerAvailable = getAccelerometerUsability();
        boolean isCellTowerAvailable = getCellTowerUsability();
        LogHelper.showLog(TAG, "GPS:  " + isGPSAvailable + ", WIFI:  " + isWIFIAvailable + ", Accelerometer:  " + isAccelerometerAvailable + ", Cell Tower:  " + isCellTowerAvailable);
        policyReferenceValues.isGPSAvailable = isGPSAvailable;
        policyReferenceValues.isWIFIAvailable = isWIFIAvailable;
        policyReferenceValues.isAccelerometerAvailable = isAccelerometerAvailable;
        policyReferenceValues.isCellTowerAvailable = isCellTowerAvailable;
        return policyReferenceValues;
    }

}
