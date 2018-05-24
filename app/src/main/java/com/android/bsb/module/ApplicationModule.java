package com.android.bsb.module;

import android.content.Context;

import com.android.bsb.AppApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final AppApplication mApplication;


    public ApplicationModule(AppApplication application){
        mApplication = application;
    }

    @Provides
    Context providerApplicatinContext(){
        return mApplication.getApplicationContext();
    }

}
