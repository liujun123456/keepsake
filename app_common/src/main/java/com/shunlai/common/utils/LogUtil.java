package com.shunlai.common.utils;

import android.util.Log;

/**
 * Created by jason on 2017/9/5.
 */

public class LogUtil {
    public static boolean DEBUG= false;
    private final static String TAG="keepsake";

    public static void v(String msg) {
        if (DEBUG)
            Log.v(TAG, getCaller() + msg);
    }
    public static void v(String msg, Throwable e) {
        if (DEBUG)
            Log.v(TAG, getCaller() + msg, e);
    }
    public static void d(String msg) {
        if (DEBUG)
            Log.d(TAG, getCaller() + msg);
    }

    public static void d(String tag , String msg) {
        if (DEBUG) {
            Log.d(tag , getCaller() + msg) ;
        }
    }

    public static void d(String msg, Throwable e) {
        if (DEBUG)
            Log.d(TAG, getCaller() + msg, e);
    }
    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, getCaller() + msg);
    }
    public static void i(String msg, Throwable e) {
        if (DEBUG)
            Log.i(TAG, getCaller() + msg, e);
    }
    public static void w(String msg) {
        if (DEBUG)
            Log.w(TAG, getCaller() + msg);
    }
    public static void w(String msg, Throwable e) {
        if (DEBUG)
            Log.w(TAG, getCaller() + msg, e);
    }
    public static void e(String msg) {
        if (DEBUG)
        Log.e(TAG, getCaller() + msg);
    }
    public static void e(String msg, Throwable e) {
        if (DEBUG)
            Log.e(TAG, getCaller() + msg, e);
    }

    private static String getCaller() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length < 5)
            return null;
        StackTraceElement caller = stack[4];
        String className = caller.getClassName();
        int shortIndex = className.lastIndexOf(".");
        if (shortIndex > 0)
            className = className.substring(shortIndex + 1, className.length());
        return "[" + className + " - " + caller.getLineNumber() + "] ";
    }

}
