package com.aut.yuxiang.lbs_middleware.Utils;

/**
 * Created by yuxiang on 8/12/16.
 */

public class LogHelper {
    public static boolean DEBUG = true;

    public static void showLog(String tag, Object msg) {
        if (DEBUG) {
            android.util.Log.e(tag, String.valueOf(msg));
        }
    }
}
