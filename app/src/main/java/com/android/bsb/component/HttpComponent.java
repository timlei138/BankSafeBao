package com.android.bsb.component;

import android.support.v4.app.Fragment;

import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.login.SplashActivity;
import com.android.bsb.ui.user.UserManagerFragment;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(LoginActivity activity);

    void inject(SplashActivity activity);

    void inject(UserManagerFragment fragment);

}
