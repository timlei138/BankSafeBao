package com.android.bsb.module;

import android.content.Context;

import com.android.bsb.data.local.LocalDataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalDataModule {

    @Provides
    LocalDataManager getLocalDataManager(Context context){
        return  LocalDataManager.getInstance(context);
    }


}
