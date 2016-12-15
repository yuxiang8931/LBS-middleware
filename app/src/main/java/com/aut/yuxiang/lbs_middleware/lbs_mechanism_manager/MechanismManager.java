package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.Mechanism;

/**
 * Created by yuxiang on 13/12/16.
 */

public class MechanismManager {
    public static final String GPS_MECHANISM = "gps_mechanism";
    public static final String CELL_TOWER_MECHANISM = "cell_tower_mechanism";
    private GPSMechanism gpsMechanism;
    private static MechanismManager instance;

    private MechanismManager() {
    }

    public static MechanismManager getInstance() {
        if (instance == null) {
            instance = new MechanismManager();
        }
        return instance;
    }

    public Mechanism getMechanism(Context context, LBSLocationListener listener, AdapterProviderUsabilityListener usabilityListener, String mechanismName) {
        if (mechanismName == GPS_MECHANISM) {
            if (gpsMechanism==null)
            {
                gpsMechanism = new GPSMechanism(context, listener, usabilityListener);
            }
            return gpsMechanism;
        } else if (mechanismName == CELL_TOWER_MECHANISM) {
            return null;
        } else {
            return null;
        }
    }

}
