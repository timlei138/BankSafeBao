package com.android.bsb.component;

import android.content.Context;

import com.android.bsb.data.local.LocalDataManager;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.module.ApplicationModule;
import com.android.bsb.module.HttpModule;
import com.android.bsb.module.LocalDataModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ApplicationModule.class, HttpModule.class, LocalDataModule.class})
public interface ApplicationComponent {

    //provider
    Context getContext();

    //provider BankTaskApi
    BankTaskApi getBankTaskApi();

    //provider LocalDataManager

    LocalDataManager getLocalDataManager();
}
