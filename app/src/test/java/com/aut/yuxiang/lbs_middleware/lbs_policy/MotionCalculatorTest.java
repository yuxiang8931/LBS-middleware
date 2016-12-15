package com.aut.yuxiang.lbs_middleware.lbs_policy;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.*;
import java.util.*;
//import java.util.Queue;
import static org.junit.Assert.*;

/**
 * Created by lingliang on 15/12/16.
 */
public class MotionCalculatorTest {

    MotionCalculator motCal = null;
    Queue<float[]> buffer = null;
    PrivateMethodTest priMeth = null;

    public MotionCalculatorTest(){
//        MotionCalculator motCal = new MotionCalculator();

        if (motCal == null){
            motCal = new MotionCalculator();
        }
        if (priMeth == null){
            priMeth = new PrivateMethodTest();
        }
        buffer = new LinkedList<float[]>();
        float [] tmpBuf = {1f,1f,1f};
        buffer.offer(tmpBuf);
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void calculateMotion() throws Exception {

    }

    @Test
    public void calculateMagnitudeTest() throws Exception {
//        float tmpFloat = (float)Math.sqrt(3d);
//
//        Method [] ma = MotionCalculator.class.getDeclaredMethods();
//        for(Method m :ma) {
//            if (m.getName().equals("calculateMagnitude")) {
//                m.setAccessible(true);
//                System.out.println("calculateMagnitude is accessible now");
//                m.invoke(motCal,buffer);
//                Field [] fa = MotionCalculator.class.getDeclaredFields();
//                for(Field f :fa) {
//                    if (f.getName().equals("magnitudeArray")) {
//                        f.setAccessible(true);
//                        ArrayList<Float> tmpArray = (ArrayList<Float>)f.get(motCal);
//                        System.out.println(tmpArray.get(0));
//                        assertEquals((float)tmpArray.get(0), tmpFloat, 0.01f);
//                    }
//                }
//
//            }
//        }

//        assertArrayEquals(motCal.magnitudeArray,tmpArray);

    }

    @Test
    public void calculateSumOfPeaksAndTroughsTest() throws Exception {
        ArrayList<Float> tmpArray = null;
        tmpArray = (ArrayList<Float>)PrivateMethodTest.getValue(motCal,"magnitudeArray");
        assertEquals(tmpArray.size(), 0);
        System.out.println(buffer.size());

//        Class [] cls = new Class[1];
//        cls[0] = motCal.getClass();
//        System.out.println("this class name:" + cls[0].toString());
        float[] tmpFloat = {2f,2f,2f};
        buffer.offer(tmpFloat);
        Method mMag = priMeth.getMethod(motCal,"calculateMagnitude");
        mMag.invoke(motCal,buffer) ;

        tmpArray = (ArrayList<Float>)priMeth.getValue(motCal,"magnitudeArray");
        System.out.println(tmpArray.size());

        Method mCal = priMeth.getMethod(motCal,"calculateSumOfPeaksAndTroughs");
        mCal.invoke(motCal);
        System.out.println(tmpArray.get(0));
    }


}