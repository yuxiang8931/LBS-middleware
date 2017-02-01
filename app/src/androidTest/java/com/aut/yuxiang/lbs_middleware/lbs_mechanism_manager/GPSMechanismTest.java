package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by yuxiang on 25/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class GPSMechanismTest {
    @Test
    public void getMechanismName() throws Exception {
        GPSMechanism gpsMechanism = new GPSMechanism(getTargetContext(), null, null, null);
        String name = gpsMechanism.getMechanismName();
        Assert.assertEquals(name, "gps_mechanism");
    }

}