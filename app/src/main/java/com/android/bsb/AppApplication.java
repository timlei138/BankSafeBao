package com.android.bsb;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerApplicationComponent;
import com.android.bsb.module.ApplicationModule;
import com.android.bsb.module.HttpModule;
import com.android.bsb.module.LocalDataModule;
import com.android.bsb.service.LocationService;
import com.android.bsb.ui.AppActivityManager;
import com.android.bsb.util.SharedProvider;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

public class AppApplication extends Application {

    private static Context sContext;

    private static User mLoginUser;

    private static boolean isOnline;

    private static ApplicationComponent mApplicationComponent;

    private static AppActivityManager mActivityManager;

    private static LocationService mLocationService;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpModule(new HttpModule())
                .localDataModule(new LocalDataModule())
                .build();
        mActivityManager = AppActivityManager.getInstance();
        Bugly.init(getApplicationContext(), "48eac4f88c", false);

        mLocationService = new LocationService(getApplicationContext());

    }


    public static AppActivityManager getAppActivityManager(){
        return mActivityManager;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    public static ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }


    public static LocationService getLocationService(){
        return mLocationService;
    }

    public static Context getContext(){
        return sContext;
    }

    public static void setLoginUser(User user,boolean online){
        mLoginUser = user;
        isOnline = online;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mActivityManager.finishAllActivity();
    }

    public static User getLoginUser(){

        if(mLoginUser == null){
            User user ;
            String account = SharedProvider.getInstance(sContext).getStringValue(AppComm.KEY_ACCOUNT,"");
            if(!TextUtils.isEmpty(account)){
                 user = new User(account);
                mLoginUser = new User(user);
            }
        }
        return mLoginUser;
    }

    public static boolean getOnline(){
        return isOnline;
    }
}
