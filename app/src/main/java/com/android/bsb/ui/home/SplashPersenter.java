package com.android.bsb.ui.home;

import android.content.Context;

import com.android.bsb.AppComm;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.SharedProvider;

import javax.inject.Inject;

public class SplashPersenter extends IBasePresent<SplashView>{


    private SharedProvider mSharedProvider;

    private BankTaskApi mApis;


    @Inject
    public SplashPersenter(Context context, BankTaskApi apis){
        mSharedProvider = SharedProvider.getInstance(context);
        mApis = apis;
    }

    public boolean isFirstLogin(){
        return mSharedProvider.getBoolValue(AppComm.KEY_FIRST_USED,true);
    }


    public void autoLogin(){

    }


    @Override
    public void getData() {

    }
}
