package com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper;

/**
 * Created by yuxiang on 13/12/16.
 */

public interface AdapterProviderUsabilityListener {
    public void onProviderDisabled(boolean oneTime);
    public void onProviderAble(boolean oneTime);
}
