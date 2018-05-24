package com.android.bsb.data.local;

import android.content.Context;

public class LocalDataManager {

    private static LocalDataManager mInstance;

    private Context mContext;



    public static LocalDataManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new LocalDataManager(context);
        }
        return mInstance;
    }

    private LocalDataManager(Context context) {
        mContext = context;
    }

}
