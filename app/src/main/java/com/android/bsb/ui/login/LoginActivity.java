package com.android.bsb.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.component.DaggerLocalDataComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPersenter> implements LoginView {

    @BindView(R.id.til_username)
    TextInputLayout mUserTextIntupLayout;
    @BindView(R.id.til_password)
    TextInputLayout mPwdTextInputLayout;
    @BindView(R.id.ed_username)
    EditText mUserEditText;
    @BindView(R.id.ed_password)
    EditText mPwdEditText;

    @BindView(R.id.btn_login)
    Button btnLogin;

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


    @OnClick({R.id.btn_login,R.id.tv_forget})
    public void onLogin(View view){
        if(view.getId() == R.id.btn_login){
            String phone = mUserEditText.getText().toString();
            String pwd = mPwdEditText.getText().toString();
            AppLogger.LOGD(null,"phone:"+phone+",pwd:"+pwd);
            if(!vaildPhone(phone) || !vaildPwd(pwd)){
                return;
            }
            AppLogger.LOGD(null,"phone:"+phone+",pwd:"+pwd);
            mPresenter.login(phone,pwd);
        }
    }


    private boolean vaildPhone(String s){

        if(s.length() <=0){
            mUserTextIntupLayout.setErrorEnabled(true);
            mUserTextIntupLayout.setError(getString(R.string.account_null_error));
            return false;
        }

        if(Utils.checkCellphone(s.toString())){
            mUserTextIntupLayout.setErrorEnabled(false);
            return true;
        }else{
            mUserTextIntupLayout.setErrorEnabled(true);
            mUserTextIntupLayout.setError(getString(R.string.account_not_phone));
            return false;
        }
    }

    private boolean vaildPwd(String s){
        if(s.length()<=0){
            mPwdTextInputLayout.setErrorEnabled(true);
            mPwdTextInputLayout.setError(getString(R.string.password_null_title));
            return false;
        }

        if(s.length() > 18){
            mPwdTextInputLayout.setErrorEnabled(true);
            mPwdTextInputLayout.setError(getString(R.string.password_length_max));
            return false;
        }else{
            return true;
        }
    }


}
