package com.android.bsb;

import android.app.Application;
import android.content.Context;

import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerApplicationComponent;
import com.android.bsb.module.ApplicationModule;
import com.android.bsb.module.HttpModule;
import com.android.bsb.module.LocalDataModule;

public class AppApplication extends Application {

    private static Context sContext;

    private static ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpModule(new HttpModule())
                .localDataModule(new LocalDataModule())
                .build();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;
    }

    public static ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public static Context getContext(){
        return sContext;
    }
}