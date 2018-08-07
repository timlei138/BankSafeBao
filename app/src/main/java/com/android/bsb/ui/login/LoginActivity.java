package com.android.bsb.ui.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.AppApplication;
import com.android.bsb.AppComm;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.data.remote.ServerException;
import com.android.bsb.ui.AppActivityManager;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.home.MainActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.NetWorkUtils;
import com.android.bsb.util.SharedProvider;
import com.android.bsb.util.Utils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import dagger.internal.Beta;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    @BindView(R.id.tv_forget)
    TextView mForgetBtn;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_login;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent)
                .build().inject(this);

    }

    @Override
    protected void initView() {
        AppApplication.getAppActivityManager().addActivity(this);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {

                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                        if(aBoolean){
                            AppLogger.LOGD(null,"aboolean");
                        }

                    }
                });
    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getData();
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
            finish();
        }
    }

    @Override
    public void loginFaild(int code,String e) {
        if(code == 201){
            Toast.makeText(this,e,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"网络异常，请稍后再试～",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Context getActivityContext() {
        return this;
    }



    @OnClick({R.id.btn_login,R.id.tv_forget})
    public void onLogin(View view){
        Toast.makeText(getBaseContext(), "this", Toast.LENGTH_SHORT).show();
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
        }else if(view.getId() == R.id.tv_forget){

        }


    }


    @OnLongClick(R.id.tv_forget)
     boolean onforgetLongClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("网络配置");
        View view = getLayoutInflater().inflate(R.layout.layout_net_config,null);
        final EditText etIp = view.findViewById(R.id.et_ip);
        final EditText etPort = view.findViewById(R.id.et_port);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String ipAddress = etIp.getText().toString();
                String port = etPort.getText().toString();
                if(TextUtils.isEmpty(ipAddress)){
                    Toast.makeText(getActivityContext(),"请填写IP地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkIpAddress(ipAddress)){
                    Toast.makeText(getActivityContext(),"IP地址格式错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                String ipConfig = ipAddress +":"+ (TextUtils.isEmpty(port)?"8080":port);
                SharedProvider provider = SharedProvider.getInstance(getApplicationContext());
                provider.setStringValue(NetComm.KEY_IP,ipConfig);
                Toast.makeText(getActivityContext(),"配置完成",Toast.LENGTH_SHORT).show();
                AppActivityManager.getInstance().exitAppAndRestart(getActivityContext());


            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false).create().show();
        return true;
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


    private boolean checkIpAddress(String ip){
        Pattern pattern = Pattern.compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }


}
