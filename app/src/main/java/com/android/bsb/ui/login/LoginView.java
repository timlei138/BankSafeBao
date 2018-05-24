package com.android.bsb.ui.login;

import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

public interface LoginView extends IBaseView{

    void loginSuccess(User info);

    void loginFaild();
}
