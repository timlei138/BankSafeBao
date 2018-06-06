package com.android.bsb.ui.login;

import android.content.Context;

import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

public interface LoginView extends IBaseView{

    void loginSuccess(User user,boolean offline);

    void loginFaild(Exception e);

    Context getActivityContext();
}
