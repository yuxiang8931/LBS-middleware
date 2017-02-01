package com.aut.yuxiang.lbs_middleware;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.aut.yuxiang.lbs_middleware.databinding.TestModeLayoutBinding;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.Accuracy;

/**
 * Created by yuxiang on 2/02/17.
 */

public class TestModeActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x01;
    private LocationBindingData locationData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_mode_layout);
        bindLayoutData();
        checkPermissionAndStartLBS();
    }

    private void bindLayoutData()
    {
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

    private void startLBS() {
        LBS.getInstance().startDetect(this, new PolicyReferenceValues(Accuracy.HIGH_LEVEL_ACCURACY, 1000 * 20)).getContinuouslyLocation(new LBSLocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                locationData.setAccuracy(String.valueOf(location.getAccuracy()));
                locationData.setLongitude(String.valueOf(location.getLongitude()));
                locationData.setLatitude(String.valueOf(location.getLatitude()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LBS.getInstance().stopDetect();
    }
}
