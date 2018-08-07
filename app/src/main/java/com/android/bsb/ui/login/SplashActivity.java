package com.android.bsb.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.home.MainActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.NetWorkUtils;

public class SplashActivity extends BaseActivity<LoginPersenter> implements LoginView {

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_splash;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).
                build().inject(this);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }


    @Override
    protected void updateView(boolean isRefresh) {
        Intent intent = new Intent();
        boolean isFirstLogin = mPresenter.isFirstLogin();
        AppLogger.LOGD(null,"isFirstLogin:"+isFirstLogin);
        if(mPresenter.isFirstLogin()){
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

        }else{
            //如果有网络则进行登陆
            mPresenter.autoLogin();
        }
    }


    @Override
    public void loginSuccess(User info,boolean online) {
        if(info != null){
            setLoginUser(info,online);
            Intent intent = new Intent();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void loginFaild(int code ,String e) {

    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}
