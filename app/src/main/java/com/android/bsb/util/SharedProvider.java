package com.android.bsb.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.bsb.AppComm;

public class SharedProvider {


    private static SharedProvider mInstance;

    private SharedPreferences mSharedPreferences;

    public static SharedProvider getInstance(Context context){

        if(mInstance == null){
            synchronized (SharedProvider.class){
                if(mInstance == null){
                    mInstance = new SharedProvider(context);
                }
            }
        }
        return mInstance;
    }


    private SharedProvider(Context context){
        mSharedPreferences = context.getSharedPreferences(AppComm.SHAREDPREF_NAME,
                Context.MODE_PRIVATE);

    }

    public boolean getBoolValue(String key,boolean def){
        return mSharedPreferences.getBoolean(AppComm.KEY_FIRST_USED,true);
    }

    public void setBoolValue(String key,boolean value){
        mSharedPreferences.edit().putBoolean(key,value).apply();
    }

    public int getIntValue(String key,int def){
        return mSharedPreferences.getInt(key,def);
    }

    public void setIntValue(String key,int value){
        mSharedPreferences.edit().putInt(key,value).apply();
    }

    public String getStringValue(String key,String def){
        return mSharedPreferences.getString(key,def);
    }

    public void setStringValue(String key,String value){
        mSharedPreferences.edit().putString(key,value).apply();
    }

}
