package com.aut.yuxiang.lbs_middleware.lbs_policies;

import android.content.Context;
import android.location.Location;

/**
 * Created by yuxiang on 8/12/16.
 */

public class LBS {
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
        }
        policy.startDetectingMotion();
        return instance;
    }



    public Location getCurrentLocation() {
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        return policy.getCurrentLocation();
    }

    public Location requireRealTimeCurrentLocation() {
        return null;
    }

    public void stopDetect() {
        if (policy == null) {
            throw new NullPointerException("Have not started detecting motion!");
        }
        policy.stopDetectingMotion();
    }


}
