package com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager;

import android.location.Location;
import android.location.LocationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by lingliang on 17/12/16.
 */
public class GPSMechanismTest {
    private static final double EARTH_RADIUS = 6376.5*1000;

//    @Mock
//    GPSMechanism gpsMech;
//    Location loc;
//    @Mock
//    Location first;
//    @Mock
//    Location sec;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getMechanismName() throws Exception {

    }

    @Test
    public void startMechanism() throws Exception {

    }

    @Test
    public void startMechanismOneTime() throws Exception {

    }

    @Test
    public void stopMechanism() throws Exception {
//        double long1, long2, lati1,lati2;
//        long1 = 0d;
//        long2 = 0d;
//        lati1 = Math.PI/2;
//        lati2 = 0;
//        double temp;
//        temp = Math.cos(lati1)*Math.cos(lati2)* Math.cos(long1-long2)+Math.sin(lati1)*Math.sin(lati2);
//        double distance = EARTH_RADIUS * Math.acos(temp);
//        System.out.println("my distance" + distance);

        float[] result = new float[3];
        Location first = Mockito.mock(Location.class);
        Location sec = Mockito.mock(Location.class);
        Mockito.when(first.setLatitude(0)).then();
        Mockito.when(sec.setLatitude(0)).thenReturn(0);
//        first = new Location(LocationManager.GPS_PROVIDER);
//        sec = new Location(LocationManager.GPS_PROVIDER);
        GPSMechanism gpsMech = new GPSMechanism();

        gpsMech.getDistance(first,sec);
        System.out.println("system distance:" + result[0]);

    }

}