package com.android.bsb.ui.splash;

import android.content.Intent;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.home.MainActivity;
import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.util.AppLogger;

public class SplashActivity extends BaseActivity<SplashPersenter> implements SplashView {

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_splash;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent).
                build().inject(this);
    }

    @Override
    protected void initView() {
        showProgress();
    }


    @Override
    protected void updateView(boolean isRefresh) {
        Intent intent = new Intent();
        boolean isFirstLogin = mPresenter.isFirstLogin();
        AppLogger.LOGD(null,"isFirstLogin:"+isFirstLogin);
        if(mPresenter.isFirstLogin()){
            hideProgress();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);

        }else{
            mPresenter.autoLogin();

        }
    }

    @Override
    protected void updateToolsBar(int title) {

    }

    @Override
    protected void updateToolsBar(String title) {

    }

    @Override
    public void loginResult(boolean result,User user) {
        if(result){
            Intent intent = new Intent();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
        }else{
            showDialog(DIALOG_TYPE_ERROR);
        }
    }
}
