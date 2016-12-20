package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;

/**
 * Created by yuxiang on 13/12/16.
 */

public class MechanismFactory {
    public static final String GPS_MECHANISM = "gps_mechanism";
    public static final String CELL_TOWER_MECHANISM = "cell_tower_mechanism";
    private GPSMechanism gpsMechanism;
    private CellTowerMechanism cellTowerMechanism;
    private static MechanismFactory instance;

    private MechanismFactory() {
    }

    public static MechanismFactory getInstance() {
        if (instance == null) {
            instance = new MechanismFactory();
        }
        return instance;
    }

    public Mechanism getMechanism(Context context, LBSLocationListener listener, AdapterProviderUsabilityListener usabilityListener, String mechanismName, PolicyReferenceValues values) {
        if (mechanismName == GPS_MECHANISM) {
            if (gpsMechanism==null)
            {
                gpsMechanism = new GPSMechanism(context, listener, usabilityListener, values);
            }
            return gpsMechanism;
        } else if (mechanismName == CELL_TOWER_MECHANISM) {
            if (cellTowerMechanism==null)
            {
                cellTowerMechanism = new CellTowerMechanism(context, listener, usabilityListener, values);
            }
            return cellTowerMechanism;
        } else {
            return null;
        }
    }

}
