package com.android.bsb.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.component.DaggerLocalDataComponent;
import com.android.bsb.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity<LoginPersenter> implements LoginView {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_login;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent)
                .build().inject(this);
        DaggerLocalDataComponent.builder().applicationComponent(applicationComponent)
                .build().inject(this);

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getData();
    }

    @Override
    public void loginSuccess(User info) {

    }

    @Override
    public void loginFaild() {

    }


}
