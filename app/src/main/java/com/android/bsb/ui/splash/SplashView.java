package com.android.bsb.ui.splash;

import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

public interface SplashView extends IBaseView{

    void loginResult(boolean result, User user);

}
