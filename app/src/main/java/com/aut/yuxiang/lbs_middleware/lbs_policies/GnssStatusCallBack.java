package com.aut.yuxiang.lbs_middleware.lbs_policies;

import android.location.GnssStatus;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;

import com.aut.yuxiang.lbs_middleware.Utils.LogHelper;


/**
 * Created by yuxiang on 8/12/16.
 */

@RequiresApi(api = VERSION_CODES.N)
public class GnssStatusCallBack extends GnssStatus.Callback {
    private static final String TAG = "GnssStatusCallBack";
    public GnssStatusCallBack() {
    }

    @Override
    public void onStarted() {
        super.onStarted();
        LogHelper.showLog(TAG, "onStarted");
    }

    @Override
    public void onStopped() {
        super.onStopped();
        LogHelper.showLog(TAG, "onStopped");
    }

    @Override
    public void onFirstFix(int ttffMillis) {
        super.onFirstFix(ttffMillis);
        LogHelper.showLog(TAG, "onFirstFix");
    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        super.onSatelliteStatusChanged(status);
        LogHelper.showLog(TAG, "onSatelliteStatusChanged");
    }
}
