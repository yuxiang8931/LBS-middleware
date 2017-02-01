package com.aut.yuxiang.lbs_middleware.lbs_policy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.aut.yuxiang.lbs_middleware.lbs_db.DBHelper;
import com.aut.yuxiang.lbs_middleware.lbs_db.SQL.CellTowerReadingsTable;
import com.aut.yuxiang.lbs_middleware.lbs_mechanism_manager.Mechanism;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.MotionDetectionService.MotionCalculateListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.MotionDetectionService.MotionDetectionBinder;
import com.aut.yuxiang.lbs_middleware.lbs_scenarios_adatper.ScenarioAdapter;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

import java.util.Timer;

import static com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.ACCELEROMETER_RUNNING_PERIOD;
import static com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.accelerometerInterval;

/**
 * Created by yuxiang on 8/12/16.
 */

public class LBSPolicy {
    private static final String TAG = "LBSPolicy";
    private static final String LBS_PREFERENCES = "lbs_preferences";
    private static final String LBS_PRE_TIME = "lbs_timestamp";
    private static final long LBS_PRE_DEFAULT = -1;
    private static final String MOTION_DETECTION_RESULT_RECEIVER = "com.lbs.motion_result_broadcast";
    private static final String IS_MOVED_KEY = "isMoved";
    private static final String MOVE_TIME_KEY = "move_time";
    private Context context;
    private PolicyReferenceValues policyReferenceValues;
    private LocationManager locationManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MotionAccelerometerListener motionListener;
    private ServiceConnection motionDetectionServiceConnection;
    private MotionDetectionService motionDetectionService;
    private Timer accelerometerStopTimer;
    private Timer accelerometerDetectionRepeatTimer;
    private ScenarioAdapter scenarioAdapter;
    private Mechanism currentMechanism;
    private boolean motionServiceRunning;
    private Intent motionDetectionResult;
    private BroadcastReceiver motionResultBroadcastReceiver;
    private Intent motionResultReceiverIntent;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    class MotionDetectionResultsReceiver extends BroadcastReceiver {
        private LBSLocationListener listener;

        public MotionDetectionResultsReceiver(LBSLocationListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == MOTION_DETECTION_RESULT_RECEIVER) {
                boolean isMove = intent.getBooleanExtra(IS_MOVED_KEY, false);
                if (isMove) {
                    LogHelper.showLog(TAG, "Moving");
                    LogHelper.showToast(context, "Moving");
                    currentMechanism = scenarioAdapter.runMechanism(false, listener, policyReferenceValues);
                    if (currentMechanism == null) {
                        listener.onLocationUpdated(null);
                    }
                } else {
                    LogHelper.showLog(TAG, "Stationary");
                    LogHelper.showToast(context, "Stationary");
                    Location currentLocation;
                    if ((currentLocation = getCachedLocation()) != null) {
                        listener.onLocationUpdated(currentLocation);
                    }
                }
            }
        }
    }

    public LBSPolicy(Context context, PolicyReferenceValues values) {
        this.context = context;
        motionDetectionResult = new Intent(context.getApplicationContext(), MotionDetectionService.class);
        motionResultReceiverIntent = new Intent(MOTION_DETECTION_RESULT_RECEIVER);
        policyReferenceValues = values;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        motionListener = new MotionAccelerometerListener();
        scenarioAdapter = new ScenarioAdapter(context);
        dbHelper = new DBHelper(context);
        motionDetectionServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                motionDetectionService = ((MotionDetectionBinder) iBinder).getService();
                LogHelper.showLog(TAG, "onServiceConnected");
                startPeriodicAccelerometerSensor();
                motionServiceRunning = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                LogHelper.showLog(TAG, "onServiceDisconnected");
                startDetectingMotion();
            }
        };
    }

    private void closeDB() {
        sqLiteDatabase.close();
        dbHelper.close();
    }

    private Location getLatestCellTowerLocationsFromDB(int limitNum) {
        if (sqLiteDatabase==null||!sqLiteDatabase.isOpen())
        {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
        String table = CellTowerReadingsTable.CELL_TOWER_READINGS_TABLE_NAME;
        String[] columns = {CellTowerReadingsTable.LATITUDE, CellTowerReadingsTable.LONGITUDE, CellTowerReadingsTable.TIME_STAMP};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = CellTowerReadingsTable.TIME_STAMP + " DESC";
        String limit = String.valueOf(limitNum);
        Location latestLocation = null;
        Cursor cursor = sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor != null) {
            if (cursor.getCount() < limitNum) {
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                latestLocation = new Location(LocationManager.NETWORK_PROVIDER);
                double latitude = cursor.getDouble(cursor.getColumnIndex(CellTowerReadingsTable.LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(CellTowerReadingsTable.LONGITUDE));
                long time = cursor.getLong(cursor.getColumnIndex(CellTowerReadingsTable.TIME_STAMP));
                latestLocation.setLatitude(latitude);
                latestLocation.setLongitude(longitude);
                latestLocation.setTime(time);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return latestLocation;
    }

    public void getCurrentLocation(LBSLocationListener listener) {
        Location currentLocation;
        if (checkMotion() || (currentLocation = getCachedLocation()) == null) {
            LogHelper.showLog(TAG, "Get mechanism Location");
            if (scenarioAdapter.runMechanism(true, listener, policyReferenceValues) == null) {
                listener.onLocationUpdated(null);
            }
        } else {
            LogHelper.showLog(TAG, "Get cached Location");
            listener.onLocationUpdated(currentLocation);
        }
    }

    public void getMechanismLocation(LBSLocationListener listener) {
        currentMechanism = scenarioAdapter.runMechanism(true, listener, policyReferenceValues);
        if (currentMechanism == null) {
            listener.onLocationUpdated(null);
        }
    }

    public void getContinuouslyLocation(LBSLocationListener listener) {
        getCurrentLocation(listener);
        if (motionResultBroadcastReceiver == null) {
            LogHelper.showLog(TAG, "getContinuouslyLocation");
            IntentFilter filter = new IntentFilter(MOTION_DETECTION_RESULT_RECEIVER);
            motionResultBroadcastReceiver = new MotionDetectionResultsReceiver(listener);
            context.registerReceiver(motionResultBroadcastReceiver, filter);
        }
    }

    public void stopContinuousLocation() {
        try {
            context.unregisterReceiver(motionResultBroadcastReceiver);
            motionResultBroadcastReceiver = null;
            scenarioAdapter.stopMechanism();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }


    public void startDetectingMotion() {
        // connect to accelerometer motion algorithm
//        context.startService(intent);
        bindAccelerometerMotionAlgorithmService();
    }

    public void stopDetectingMotion() {
        try {
            context.unbindService(motionDetectionServiceConnection);
            LogHelper.showLog(TAG, "stopDection suc");
//        context.stopService(intent);
        } catch (Exception e) {
            LogHelper.showLog(TAG, "stopDection fail");
        } finally {
            closeDB();
            stopPeriodicAccelerometerSensor();
        }
        motionServiceRunning = false;
    }

    private void sendMotionResult(boolean isMoved, long moveTime) {
        motionResultReceiverIntent.putExtra(IS_MOVED_KEY, isMoved);
        motionResultReceiverIntent.putExtra(MOVE_TIME_KEY, moveTime);
        context.sendBroadcast(motionResultReceiverIntent);
    }

    private void bindAccelerometerMotionAlgorithmService() {
        Intent intent = new Intent(context.getApplicationContext(), MotionDetectionService.class);
        context.bindService(intent, motionDetectionServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //    private TimerTask accelerometerStopUseTask = new TimerTask() {
//        @Override
//        public void run() {
//            accelerometerStopTimer.cancel();
//            stopPeriodicAccelerometerSensor();
//            motionDetectionService.startCalculate(new MotionCalculateListener() {
//                @Override
//                public void onCalculatorFinish(boolean isMoved, long startTime) {
//                    LogHelper.showLog(TAG, "onCalculatorFinish");
//                    sendMotionResult(isMoved, startTime);
//                    motionDetectionService.clearBuffer();
//                    saveLatestMovementTimeStamp(startTime);
//                    accelerometerDetectionRepeatTimer = new Timer();
//                    accelerometerDetectionRepeatTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            accelerometerDetectionRepeatTimer.cancel();
//                            startPeriodicAccelerometerSensor();
//                        }
//                    }, accelerometerInterval);
//
//                }
//            });
//        }
//    };
    private AccHandler accHandler = new AccHandler();

    class AccHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                stopPeriodicAccelerometerSensor();
                motionDetectionService.startCalculate(new MotionCalculateListener() {
                    @Override
                    public void onCalculatorFinish(boolean isMoved, long startTime) {
//                        LogHelper.showLog(TAG, "onCalculatorFinish");
                        sendMotionResult(isMoved, startTime);
                        motionDetectionService.clearBuffer();
                        saveLatestMovementTimeStamp(startTime);
                        accHandler.sendEmptyMessageDelayed(1, accelerometerInterval);
                    }
                });
            } else if (msg.what == 1) {
                startPeriodicAccelerometerSensor();
            }
        }
    }

    private void startPeriodicAccelerometerSensor() {
        startAccelerometerSensor();
        accHandler.sendEmptyMessageDelayed(0, ACCELEROMETER_RUNNING_PERIOD);
//        accelerometerStopTimer = new Timer();
//        accelerometerStopTimer.schedule(accelerometerStopUseTask, ACCELEROMETER_RUNNING_PERIOD);
    }

    private void stopPeriodicAccelerometerSensor() {
        stopAccelerometerSensor();
//        accelerometerStopTimer.cancel();
//        if (accelerometerDetectionRepeatTimer!=null)
//        {
//            accelerometerDetectionRepeatTimer.cancel();
//        }
        accHandler.removeMessages(0);
        accHandler.removeMessages(1);
    }

    private void startAccelerometerSensor() {
        mSensorManager.registerListener(motionListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopAccelerometerSensor() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(motionListener, mSensor);
        }
    }


    private boolean checkMotion() {
        long timeStamp = context.getSharedPreferences(LBS_PREFERENCES, Context.MODE_PRIVATE).getLong(LBS_PRE_TIME, LBS_PRE_DEFAULT);
        if (timeStamp == LBS_PRE_DEFAULT) {
            return false;
        }
        if (System.currentTimeMillis() - timeStamp < policyReferenceValues.acceptedIntervalForNewMotion) {
            return true;
        }
        return false;
    }

    private Location getCachedLocation() throws SecurityException {
        Location lastGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Location lastNetWorkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location lastNetWorkLocation = getLatestCellTowerLocationsFromDB(1);
        int result = isBetterLocation(lastGPSLocation, lastNetWorkLocation);
        if (result > 0) {
            return lastGPSLocation;
        } else if (result < 0) {
            return lastNetWorkLocation;
        } else {
            return null;
        }
    }


    private void saveLatestMovementTimeStamp(long currentTimeStamp) {
        context.getSharedPreferences(LBS_PREFERENCES, Context.MODE_PRIVATE).edit().putLong(LBS_PRE_TIME, currentTimeStamp).commit();
    }


    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param gpsLocation The new Location that you want to evaluate
     * @param netLocation The current Location fix, to which you want to compare the new one
     */
    private int isBetterLocation(Location gpsLocation, Location netLocation) {
        long currentTime = System.currentTimeMillis();


        if (netLocation != null && gpsLocation != null) {
            // Check whether the new location fix is newer or older
            long timeDelta = gpsLocation.getTime() - netLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > policyReferenceValues.acceptedIntervalForNewLocation;
            boolean isSignificantlyOlder = timeDelta < -policyReferenceValues.acceptedIntervalForNewLocation;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            if (isSignificantlyNewer) {
                return 1;
            } else if (isSignificantlyOlder) {
                return -1;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (gpsLocation.getAccuracy() - netLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(gpsLocation.getProvider(),
                    netLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return 1;
            } else if (isNewer && !isLessAccurate) {
                return 1;
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return 1;
            }
            return -1;
        } else {
            if (netLocation == null && gpsLocation == null) {
                return 0;
            } else {
                if (netLocation == null && currentTime - gpsLocation.getTime() < policyReferenceValues.acceptedIntervalForNewLocation) {
                    return 1;
                }

                if (gpsLocation == null && currentTime - netLocation.getTime() < policyReferenceValues.acceptedIntervalForNewLocation) {
                    return -1;
                }
            }
            return 0;
        }
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    class MotionAccelerometerListener implements SensorEventListener {
        float[] gravity = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] linear_acceleration = new float[4];
            // Isolate the force of gravity with the low-pass filter.xx
            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            linear_acceleration[3] = System.currentTimeMillis();
//            LogHelper.showLog(TAG, linear_acceleration[0] + " : " + linear_acceleration[1] + " : " + linear_acceleration[2]);
//            LogHelper.showLog(TAG, linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);

            motionDetectionService.insertIntoBuffer(linear_acceleration);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    public void setPolicyReferenceValues(PolicyReferenceValues policyReferenceValues) {
        this.policyReferenceValues = policyReferenceValues;
    }

}
