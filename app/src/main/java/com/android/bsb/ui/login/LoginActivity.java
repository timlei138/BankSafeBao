package com.android.bsb.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
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

    EditText mUserEditText;
    EditText mPwdEditText;

    @BindView(R.id.btn_login)
    Button btnLogin;

    boolean isAccountVaild = false;
    boolean isPasswordVaild = false;

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
        mUserEditText = mUserTextIntupLayout.getEditText();
        mPwdEditText = mPwdTextInputLayout.getEditText();

        mUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AppLogger.LOGD(null,"length:"+s.length());
                if(s.length() <=0){
                    isAccountVaild = false;
                    mUserTextIntupLayout.setErrorEnabled(true);
                    mUserTextIntupLayout.setError(getString(R.string.account_null_error));
                    return;
                }

                if(Utils.checkCellphone(s.toString())){
                    mUserTextIntupLayout.setErrorEnabled(false);
                    isAccountVaild = true;
                }else{
                    isAccountVaild = false;
                    mUserTextIntupLayout.setErrorEnabled(true);
                    mUserTextIntupLayout.setError(getString(R.string.account_not_phone));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <=0){
                    isPasswordVaild = false;
                    mPwdTextInputLayout.setErrorEnabled(true);
                    mPwdTextInputLayout.setError(getString(R.string.password_null_title));
                }else if(s.length() > 18){
                    isPasswordVaild = false;
                    mPwdTextInputLayout.setErrorEnabled(true);
                    mPwdTextInputLayout.setError(getString(R.string.password_length_max));
                }else{
                    isPasswordVaild = true;
                    mPwdTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
    public void onLogin(){
        AppLogger.LOGD(null,"Login-->");
    }


}
