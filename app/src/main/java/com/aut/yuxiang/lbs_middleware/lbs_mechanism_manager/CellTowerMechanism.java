package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.AdapterProviderUsabilityListener;

import java.util.List;

import static com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.MechanismManager.CELL_TOWER_MECHANISM;

/**
 * Created by yuxiang on 13/12/16.
 */

public class CellTowerMechanism extends Mechanism {
    private int mcc;
    private int mnc;
    private int lac;
    private int cid;
    private int sid;
    private TelephonyManager telephonyManager;

    private List<CellInfo> allCellInfo;

    public CellTowerMechanism(Context context, LBSLocationListener listener, AdapterProviderUsabilityListener usabilityListener, PolicyReferenceValues values) {
        super(context, listener, usabilityListener, values);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CELL_LOCATION);
        getOperatorInfo();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCellLocationChanged(CellLocation location) {
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        }

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
        }
    }


    @Override
    public String getMechanismName() {
        return CELL_TOWER_MECHANISM;
    }

    @Override
    public void startMechanismOneTime() {

    }

    @Override
    public void stopMechanism() {

    }

    @Override
    public void startMechanism() {
        super.startMechanism();
    }

    private void getOperatorInfo() {
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
            cid = cdmaCellLocation.getBaseStationId();
            lac = cdmaCellLocation.getNetworkId();
            sid = cdmaCellLocation.getSystemId();
        } else {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            cid = gsmCellLocation.getCid();
            lac = gsmCellLocation.getLac();
        }
        allCellInfo = telephonyManager.getAllCellInfo();
    }


}
