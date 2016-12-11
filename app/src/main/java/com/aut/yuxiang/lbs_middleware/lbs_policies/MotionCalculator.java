package com.aut.yuxiang.lbs_middleware.lbs_policies;

import com.aut.yuxiang.lbs_middleware.Utils.LogHelper;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by yuxiang on 8/12/16.
 */

public class MotionCalculator {
    private static final String TAG = "MotionCalculator";
    private ArrayList<Float> magnitudeArray;

    public MotionCalculator() {
        magnitudeArray = new ArrayList<>();
    }

    public boolean calculateMotion(Queue<float[]> buffer) {
        calculateMagnitude(buffer);
        float sum = calculateSumOfPeaksAndTroughs();
        magnitudeArray.clear();
        if (sum>4)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    private void calculateMagnitude(Queue<float[]> buffer) {
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            float[] bufferItem = buffer.poll();
            float magnitude = (float) Math.sqrt((double) (bufferItem[0] * bufferItem[0] + bufferItem[1] * bufferItem[1] + bufferItem[2] * bufferItem[2]));
            magnitudeArray.add(magnitude);
        }
    }

    private float calculateSumOfPeaksAndTroughs() {
        float sum = 0;
        // increase 1, decrease -1
        int previousTend = 0;
        int tend = 0;
        for (float value : magnitudeArray) {
            LogHelper.showLog(TAG, value);
        }
        for (int i = 1; i < magnitudeArray.size() - 1; i++) {
            float currentMagnitude = magnitudeArray.get(i);
            float nextMagnitude = magnitudeArray.get(i + 1);
            if (currentMagnitude < nextMagnitude) {
                tend = 1;
            } else if (currentMagnitude > nextMagnitude) {
                tend = -1;
            }
            if (tend != previousTend && previousTend != 0) {
                LogHelper.showLog(TAG, "point:  " + currentMagnitude);
                sum += currentMagnitude;
            }
            previousTend = tend;

        }
        LogHelper.showLog(TAG, "SUM: "+sum);
        return sum;
    }

}
