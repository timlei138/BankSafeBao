package com.android.bsb.ui.home;

import android.content.Intent;
import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.login.LoginActivity;

public class SplashActivity extends BaseActivity<SplashPersenter> implements SplashView{

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
        if(mPresenter.isFirstLogin()){
            hideProgress();

        }else{
            mPresenter.autoLogin();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void loginResult(boolean result) {
        if(result){
            Intent intent = new Intent();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
        }else{
            showDialog(DIALOG_TYPE_ERROR);
        }
    }
}
