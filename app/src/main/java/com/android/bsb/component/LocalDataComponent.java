package com.android.bsb.component;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.bsb.module.ApplicationModule;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface LocalDataComponent {

    void inject(Activity activity);
    void inject(Fragment fragment);
}
