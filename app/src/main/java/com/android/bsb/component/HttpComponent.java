package com.android.bsb.component;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.bsb.module.ApplicationModule;
import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.splash.SplashActivity;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(LoginActivity activity);

    void inject(SplashActivity activity);

    void inject(Fragment fragment);

}
