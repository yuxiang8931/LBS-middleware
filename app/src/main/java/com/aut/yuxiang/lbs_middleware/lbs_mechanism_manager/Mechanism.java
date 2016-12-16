package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;

/**
 * Created by yuxiang on 13/12/16.
 */

public abstract class Mechanism {

    protected final Context context;
    protected PolicyReferenceValues values;
    protected LBSLocationListener listener;
    protected AdapterProviderUsabilityListener usabilityListener;


    public Mechanism(Context context, final LBSLocationListener listener, final AdapterProviderUsabilityListener usabilityListener, PolicyReferenceValues values) {
        this.context = context;
        this.values = values;
        this.listener = listener;
        this.usabilityListener = usabilityListener;
    }

    public abstract String getMechanismName();


    public abstract void startMechanismOneTime();

    public abstract void stopMechanism();

    public void startMechanism() {
        LBS.getInstance().stopDetect();
    }


}
