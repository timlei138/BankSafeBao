package com.android.bsb.ui.login;

import android.util.Log;

import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.ui.base.IBaseView;
import com.android.bsb.util.AppLogger;

import javax.inject.Inject;

public class LoginPersenter extends IBasePresent<LoginView>{

    private String TAG = "LoginPersenter";

    private BankTaskApi mApi;


    @Inject
    public LoginPersenter(BankTaskApi api){
        mApi = api;
    }

    public void login(String name,String pwd){

    }


    @Override
    public void getData() {
        AppLogger.LOGD(TAG,"getData"+mApi);

    }
}
