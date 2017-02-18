package com.aut.yuxiang.lbs_middleware;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.aut.yuxiang.lbs_middleware.databinding.TestModeLayoutBinding;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.Accuracy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by yuxiang on 2/02/17.
 */

public class TestModeActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x01;
    private LocationBindingData locationData;
    private static final String LOG_FILE_NAME = "LBS_LOCATION_LOG.TXT";
    private static final String LOG_BATTERY_NAME = "LBS_BATTERY_LOG.TXT";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_mode_layout);
        bindLayoutData();
        checkPermissionAndStartLBS();
    }

    private void bindLayoutData() {
        TestModeLayoutBinding binding = DataBindingUtil.setContentView(this, R.layout.test_mode_layout);
        locationData = new LocationBindingData();
        binding.setLocation(locationData);
    }


    private void checkPermissionAndStartLBS() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            startLBS();
        }

        startLBS();
    }

    private String getFileRoot() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return getExternalFilesDir(null).getAbsolutePath();
        } else {
            return this.getFilesDir().getAbsolutePath();
        }
    }

    private FileOutputStream fos = null;
    private FileOutputStream batteryFOS = null;
    Intent batteryStatus = null;
    BroadcastReceiver receiver;
    private Calendar calendar;
    private SimpleDateFormat format;

    private void startLBS() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver = new PowerConnectionReceiver();
        batteryStatus = this.registerReceiver(receiver, intentFilter);

        calendar = Calendar.getInstance();
        format = new SimpleDateFormat("ss-mm-HH dd-MM-yyyy");
        String locationFilePath = getFileRoot() + File.separator + LOG_FILE_NAME;
        String batteryFilePath = getFileRoot() + File.separator + LOG_BATTERY_NAME;
        final File locationLogFile = new File(locationFilePath);
        try {
            fos = new FileOutputStream(locationLogFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            batteryFOS = new FileOutputStream(batteryFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LBS.getInstance().startDetect(this, new PolicyReferenceValues(Accuracy.HIGH_LEVEL_ACCURACY, 1000 * 20)).getContinuouslyLocation(new LBSLocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                locationData.setAccuracy(String.valueOf(location.getAccuracy()));
                locationData.setLongitude(String.valueOf(location.getLongitude()));
                locationData.setLatitude(String.valueOf(location.getLatitude()));
                calendar.setTimeInMillis(System.currentTimeMillis());
                String content = "Longitude: " + String.valueOf(location.getAccuracy()) + "  Latitude:  " + String.valueOf(location.getLatitude()) +
                        "  Accuracy:  " + String.valueOf(location.getAccuracy()) + "  Time:  " + format.format(calendar.getTime()) + "\n";
                try {
                    fos.write(content.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStop() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (batteryFOS!=null)
        {
            try {
                batteryFOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(receiver);
        composeEmail(new String[]{"yuxiang8931@gmail.com"}, "LOCATION_LOG", Uri.fromFile(new File(getFileRoot() + File.separator + LOG_FILE_NAME)));
        super.onStop();
        LBS.getInstance().stopDetect();

    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale;

            String content = "Level: " + level + "  Scale: " + scale + "  BatteryPct:  " + batteryPct + " Time:  " + format.format(calendar.getTime()) + "\n";
            try {
                batteryFOS.write(content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
