
package com.example.kurtqin.test.Account.utils;

import android.util.Log;

import com.example.kurtqin.test.SnowApplication;

public class LogUtil {
    private static final String APP_TAG = "XiaoMiAccount";
    private static final String TAG_FORMAT = "%s-%s";

    
    /**
     * Print INFO information
     */
    public static void i(String tag, String log) {
        Log.i(String.format(TAG_FORMAT, APP_TAG, tag), log);
    }

    /**
     * Print VERBOSE information
     */
    public static void v(String tag, String log) {
        Log.v(String.format(TAG_FORMAT, APP_TAG, tag), log);
    }

    /**
     * Print debug information
     */
    public static void d(String tag, String log) {
        if (SnowApplication.isUserDebug()) {
            Log.d(String.format(TAG_FORMAT, APP_TAG, tag), log);
        }
    }

    /**
     * Print error information
     */
    public static void e(String tag, String log) {
        Log.e(String.format(TAG_FORMAT, APP_TAG, tag), log);
    }

    /**
     * Print warning information
     */
    public static void w(String tag, String log) {
        Log.w(String.format(TAG_FORMAT, APP_TAG, tag), log);
    }

    /**
     * Print warning information
     */
    public static void w(String tag, Throwable tr) {
        Log.w(String.format(TAG_FORMAT, APP_TAG, tag), tr);
    }

    /**
     * Print warning information
     */
    public static void w(String tag, String log, Throwable tr) {
        Log.w(String.format(TAG_FORMAT, APP_TAG, tag), log, tr);
    }
}
