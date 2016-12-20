package com.aut.yuxiang.lbs_middleware.lbs_policy;

import android.content.Context;
import android.location.Location;

import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

/**
 * Created by yuxiang on 8/12/16.
 */

public class LBS {
    private static final String TAG = "LBS";
    private static LBS instance;
    private LBSPolicy policy;

    public PolicyReferenceValues getValues() {
        return values;
    }

    public void setValues(PolicyReferenceValues values) {
        this.values = values;
    }

    private Context context;
    private PolicyReferenceValues values;

    private LBS() {
    }

    public static LBS getInstance() {
        if (instance == null) {
            instance = new LBS();
        }
        return instance;
    }


    public LBS startDetect(Context context, PolicyReferenceValues values) {
        this.context = context;
        this.values = values;
        if (policy == null) {
            policy = new LBSPolicy(context, values);
        } else {
            policy.setPolicyReferenceValues(values);
        }
        policy.startDetectingMotion();
        return instance;
    }


    public void getCurrentLocation(LBSLocationListener listener) {
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        policy.getCurrentLocation(listener);
    }

    public void getCurrentMechanismLocation(LBSLocationListener listener) {
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        final Location currentLocation;
        policy.getMechanismLocation(listener);
    }

    public void getContinuouslyLocation(LBSLocationListener listener) {
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        policy.getContinuouslyLocation(listener);
    }

    public void stopContinuousLocation() {
        if (policy != null) {
            policy.stopContinuousLocation();
        }
    }

    public void stopDetect() {
        LogHelper.showLog(TAG, "Stop Detection");
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        policy.stopDetectingMotion();
    }

    public interface LBSLocationListener {
        public void onLocationUpdated(Location location);
    }


}
