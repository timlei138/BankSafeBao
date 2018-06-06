package com.android.bsb.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.bsb.AppComm;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.component.DaggerLocalDataComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.home.MainActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.NetWorkUtils;
import com.android.bsb.util.SharedProvider;
import com.android.bsb.util.Utils;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    protected void updateToolsBar(int title) {

    }

    @Override
    protected void updateToolsBar(String title) {

    }

    @Override
    public void loginSuccess(User info,boolean offline) {
        if(info != null){
            setLoginUser(info,offline);
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            info.setLoginPwd(mPwdEditText.getText().toString());
            saveLoginInfo(info);
        }
    }

    @Override
    public void loginFaild(Exception e) {

    }

    @Override
    public Context getActivityContext() {
        return this;
    }



    @OnClick({R.id.btn_login,R.id.tv_forget})
    public void onLogin(View view){
        if(view.getId() == R.id.btn_login){
            String phone = mUserEditText.getText().toString();
            String pwd = mPwdEditText.getText().toString();
            if(!vaildPhone(phone) || !vaildPwd(pwd)){
                return;
            }
            if(!NetWorkUtils.isNetworkAvailable(this)){
                showDialog(DIALOG_TYPE_NOT_NETWORK);
                return;
            }
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


    private void saveLoginInfo(final User info){
        Observable observable  = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                StringBuffer buffer = new StringBuffer();
                buffer.append(Utils.encrypt(info.getUid()+"")+":");
                buffer.append(Utils.encrypt(info.getLoginName())+":");
                buffer.append(Utils.encrypt(info.getUname())+":");
                buffer.append(Utils.encrypt(info.getRole()+"")+":");
                buffer.append(Utils.encrypt(info.getRoleName())+":");
                buffer.append(Utils.encrypt(info.getDeptName())+":");
                buffer.append(Utils.encrypt(info.getLoginPwd())+":");
                buffer.append(Utils.encrypt(info.getDeptCode()+""));
                emitter.onNext(buffer.toString());
                emitter.onComplete();
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String o) {
                AppLogger.LOGD(null,"saveAccountInfo:"+o);
                SharedProvider.getInstance(getApplicationContext())
                        .setStringValue(AppComm.KEY_ACCOUNT,o);
                SharedProvider.getInstance(getApplicationContext())
                        .setBoolValue(AppComm.KEY_FIRST_USED,false);
            }

            @Override
            public void onError(Throwable e) {
                AppLogger.LOGE(null,"saveAccountInfo error");
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }



}
