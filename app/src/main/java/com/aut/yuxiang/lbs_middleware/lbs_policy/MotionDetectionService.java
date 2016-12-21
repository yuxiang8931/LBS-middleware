package com.aut.yuxiang.lbs_middleware.lbs_policy;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

import java.util.ArrayDeque;
import java.util.Queue;


public class MotionDetectionService extends Service {
    private static final String TAG = "MotionDetectionService";
    private MotionDetectionBinder mBinder = new MotionDetectionBinder();
    public Queue<float[]> buffer;
    private CalculateMotionThread calculateMotionThread;
    private MotionCalculator motionCalculator;
    private boolean run = false;

    public MotionDetectionService() {
    }

    public class MotionDetectionBinder extends Binder {

        MotionDetectionService getService() {
            return MotionDetectionService.this;
        }
    }

    @Override
    public void onCreate() {
        LogHelper.showLog(TAG, "onCreate");
        super.onCreate();
        buffer = new ArrayDeque<>();
        motionCalculator = new MotionCalculator();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogHelper.showLog(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogHelper.showLog(TAG,"onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogHelper.showLog(TAG,"onUnbind");
        return false;
    }

    @Override
    public void onDestroy() {
        LogHelper.showLog(TAG,"onDestroy");
        super.onDestroy();
    }

    public boolean insertIntoBuffer(float[] bufferItem) {
        return buffer.add(bufferItem);
    }


    public void clearBuffer()
    {
        if (buffer!=null)
        {
            buffer.clear();
        }

    }

    public void startCalculate(MotionCalculateListener listener) {
        if (calculateMotionThread == null) {
            calculateMotionThread = new CalculateMotionThread(listener);
            calculateMotionThread.start();
        }
        run = true;
    }

    class CalculateMotionThread extends Thread {
        private MotionCalculateListener listener;

        public CalculateMotionThread(MotionCalculateListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                if (run) {
//                    LogHelper.showLog(TAG,"service is running...");
                    if (buffer != null&& buffer.size()>0) {
                        boolean isMoved = motionCalculator.calculateMotion(buffer);
                        listener.onCalculatorFinish(isMoved, System.currentTimeMillis());
//                        LogHelper.showLog(TAG, "buffer is cleared!");
                    } else {
                        listener.onCalculatorFinish(false, -1);
                    }
                    run = false;
                }
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface MotionCalculateListener {
        public void onCalculatorFinish(boolean isMoved, long startTime);
    }

}
