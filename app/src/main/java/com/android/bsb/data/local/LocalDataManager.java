package com.android.bsb.data.local;

import android.content.Context;

import com.android.bsb.BuildConfig;
import com.android.bsb.bean.MyObjectBox;
import com.android.bsb.util.AppLogger;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class LocalDataManager {

    private static LocalDataManager mInstance;

    private Context mContext;

    private BoxStore appBoxStore;

    private String TAG = "LocalDataManager";


    public static LocalDataManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new LocalDataManager(context);
        }
        return mInstance;
    }

    private LocalDataManager(Context context) {
        mContext = context;
        mContext = context;
        appBoxStore  = MyObjectBox.builder().androidContext(context).build();

        if(BuildConfig.DEBUG){
            new AndroidObjectBrowser(appBoxStore).start(mContext);
        }

        AppLogger.LOGD(TAG,"Using ObjectBox "+BoxStore.getVersion() + ",("+BoxStore.getVersionNative()+")");
    }


    public BoxStore getBoxStore(){
        return appBoxStore;
    }

}
