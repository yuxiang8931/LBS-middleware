package com.aut.yuxiang.lbs_middleware.lbs_utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yuxiang on 8/12/16.
 */

public class LogHelper {
    public static boolean DEBUG = true;

    public static void showLog(String tag, Object msg) {
        if (DEBUG) {
            android.util.Log.e(tag, msg==null?"null":String.valueOf(msg));
        }
    }

    public static void showToast(Context context, String msg)
    {
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }
}
