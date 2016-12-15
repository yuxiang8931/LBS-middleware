package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;

/**
 * Created by yuxiang on 13/12/16.
 */

public abstract class Mechanism {

    public abstract String getMechanismName();

    public abstract void startMechanismOneTime();

    public abstract void stopMechanism();

    public void startMechanism() {
        LBS.getInstance().stopDetect();
    }
}
