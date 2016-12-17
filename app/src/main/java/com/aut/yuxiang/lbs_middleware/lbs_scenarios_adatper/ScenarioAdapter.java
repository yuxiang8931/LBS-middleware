package com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.Mechanism;
import com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.MechanismManager;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;


/**
 * Created by yuxiang on 13/12/16.
 */

public class ScenarioAdapter {
    private static final String TAG = "ScenarioAdapter";
    private Context context;
    private ProviderUsabilityDetector providerUsabilityDetector;
    private Mechanism currentMechanism;
    private AdapterProviderUsabilityListener usabilityListener;
    private PolicyReferenceValues values;
    private LBSLocationListener locationListener;

    public ScenarioAdapter(final Context context) {
        this.context = context;
        providerUsabilityDetector = new ProviderUsabilityDetector(context);
        usabilityListener = new AdapterProviderUsabilityListener() {
            @Override
            public void onProviderDisabled(boolean oneTime) {
                reAdapt(oneTime);
            }

            @Override
            public void onProviderAble(boolean oneTime) {
                reAdapt(oneTime);
            }

        };
    }

    private void reAdapt(boolean oneTime) {
//        Mechanism mechanism = adapt(locationListener, values);
//        if (mechanism.getMechanismName() != currentMechanism.getMechanismName()) {
            stopMechanism();
//            currentMechanism = mechanism;
            runMechanism(oneTime, locationListener, values);
//        }
    }

    private Mechanism adapt(LBSLocationListener listener, PolicyReferenceValues values) {
        currentMechanism = null;
        LogHelper.showLog(TAG, "Adapt scenario");
        providerUsabilityDetector.detectProviderUsability(values);
        switch (values.accuracy) {
            case HIGH_LEVEL_ACCURACY:
                if (values.isGPSAvailable) {
                    currentMechanism = MechanismManager.getInstance().getMechanism(context, listener, usabilityListener, MechanismManager.GPS_MECHANISM, values);
                } else if (values.isCellTowerAvailable) {
                    currentMechanism = MechanismManager.getInstance().getMechanism(context, listener,usabilityListener,MechanismManager.CELL_TOWER_MECHANISM, values);
                }
                else
                {
                    return null;
                }
                break;
            case LOW_LEVEL_ACCURACY:
                if (values.isCellTowerAvailable) {
                    currentMechanism = MechanismManager.getInstance().getMechanism(context, listener,usabilityListener,MechanismManager.CELL_TOWER_MECHANISM, values);
                }
                else
                {
                    return null;
                }
                break;
            case SHUTTER_PRIORITY:
                break;
        }

        return currentMechanism;
    }

    public Mechanism runMechanism(boolean oneTime, LBSLocationListener listener, PolicyReferenceValues values) {
        this.values = values;
        this.locationListener = listener;
        currentMechanism = adapt(listener, values);
        if (currentMechanism != null) {
            if (oneTime) {
                currentMechanism.startMechanismOneTime();
            } else {
                currentMechanism.startMechanism();
            }
        }
        return currentMechanism;
    }

    public void stopMechanism() {
        if (currentMechanism != null) {
            currentMechanism.stopMechanism();
        }
    }
}
