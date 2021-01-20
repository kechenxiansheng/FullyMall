package com.cm.fm.mall.common.util;

import android.util.Log;

/**
 * 自定义日志工具类
 * */
public class LogUtil {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;        //表示所有日志都不打印
    public static final int LEVEL = VERBOSE;    //当前日志等级

    public static void v(String tag, String msg){
        if (LEVEL <= VERBOSE){
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg){
        if (LEVEL<=DEBUG){
            Log.d(tag, msg);
        }
    }
    public static void i(String tag, String msg){
        if (LEVEL<=INFO){
            Log.i(tag, msg);
        }
    }
    public static void w(String tag, String msg){
        if (LEVEL<=WARN){
            Log.w(tag, msg);
        }
    }
    public static void e(String tag, String msg){
        if (LEVEL<=ERROR){
            Log.e(tag, msg);
        }
    }
}