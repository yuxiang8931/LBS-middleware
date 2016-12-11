package com.aut.yuxiang.lbs_middleware.lbs_policies;

/**
 * Created by yuxiang on 8/12/16.
 */

public class PolicyReferenceValues {
    public static long acceptedIntervalForNewLocation;

    public static enum Accuracy {
        HIGH_LEVEL_ACCURACY, LOW_LEVEL_ACCURACY, SHUTTER_PRIORITY
    }

    public static final long ACCELEROMETER_RUNNING_PERIOD = 2 * 1000;
    public static long accelerometerInterval = 3 * 1000;
    public boolean isGPSAvailable;
    public boolean isWIFIAvailable;
    public boolean isAccelerometerAvailable;
    public boolean isCellTowerAvailable;
    Accuracy accuracy;

    public PolicyReferenceValues() {

    }

    public PolicyReferenceValues(Accuracy accuracy, long acceptedIntervalForNewLocation) {
        this.accuracy = accuracy;
        this.acceptedIntervalForNewLocation = acceptedIntervalForNewLocation;
    }

    public PolicyReferenceValues(Accuracy accuracy, long acceptedIntervalForNewLocation, long accelerometerInterval) {
        this.accuracy = accuracy;
        this.acceptedIntervalForNewLocation = acceptedIntervalForNewLocation;
        this.accelerometerInterval = accelerometerInterval;
    }

    public PolicyReferenceValues(boolean isGPSAvailable, boolean isWIFIAvailable, boolean isAccelerometerAvailable, boolean isCellTowerAvailable, Accuracy accuracy, long acceptedIntervalForNewLocation) {
        this.isGPSAvailable = isGPSAvailable;
        this.isWIFIAvailable = isWIFIAvailable;
        this.isAccelerometerAvailable = isAccelerometerAvailable;
        this.isCellTowerAvailable = isCellTowerAvailable;
        this.accuracy = accuracy;
        this.acceptedIntervalForNewLocation = acceptedIntervalForNewLocation;
    }
}
