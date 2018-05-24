package com.android.bsb.util;

import android.util.Log;

public class AppLogger {

    private static final boolean DEBUG = true;

    private static final String TAG  = "BankSafeBao";

    public static void LOGD(String tag,String msg){
        if (DEBUG)
            Log.d(TAG + (tag == null ? "":tag) ,msg);
    }

    public static void LOGE(String tag,String msg){
        if (DEBUG)
            Log.e(TAG + (tag == null ? "":tag) ,msg);
    }

    public static void LOGW(String tag,String msg){
        if (DEBUG)
            Log.w(TAG + (tag == null ? "":tag) ,msg);
    }

}
