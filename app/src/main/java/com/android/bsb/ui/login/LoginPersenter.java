package com.android.bsb.ui.login;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.AppComm;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.ApiException;
import com.android.bsb.data.remote.AppError;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.ui.base.IBaseView;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.NetWorkUtils;
import com.android.bsb.util.SharedProvider;
import com.android.bsb.util.Utils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginPersenter extends IBasePresent<LoginView>{

    private String TAG = "LoginPersenter";

    private BankTaskApi mApi;


    private SharedProvider mSharedProvider;

    @Inject
    public LoginPersenter(BankTaskApi api,Context context){
        mApi = api;
        mSharedProvider = SharedProvider.getInstance(context);
    }


    public boolean isFirstLogin(){
        return mSharedProvider.getBoolValue(AppComm.KEY_FIRST_USED,true);
    }


    /**
     * 自动登陆
     */
    public void autoLogin(){
        if(!NetWorkUtils.isNetworkAvailable(mView.getActivityContext())){
            loginlocal();
        }else{
            String account = mSharedProvider.getStringValue(AppComm.KEY_ACCOUNT,"");
            if(TextUtils.isEmpty(account)){
                mView.showNetError();
                return;
            }
            User user = new User(account);
            login(user.getLoginName(),user.getLoginPwd());
        }
    }


    private void loginlocal(){
        Observable<User> observable = Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                String account = mSharedProvider.getStringValue(AppComm.KEY_ACCOUNT,"");
                if(TextUtils.isEmpty(account)){
                    emitter.onError(new ApiException(new Throwable("ic_account error!"), AppError.APP_ERROR_CODE));
                }
                User user = new User(account);
                if(user == null){
                    emitter.onError(new ApiException(new Throwable("ic_account error"),AppError.APP_ERROR_CODE));
                }else{
                    emitter.onNext(user);
                }
            }
        });
        observable.subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(User user) {
                mView.loginSuccess(user,false);
            }

            @Override
            public void onError(Throwable e) {
                mView.loginFaild(201,"用户名密码错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * 用户登陆
     * @param name
     * @param pwd
     */
    public void login(String name,String pwd){
        mApi.userLogin(name,pwd)
                .compose(mView.<User>bindToLife())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<User>() {
                    @Override
                    public void onComplete() {
                        AppLogger.LOGD(TAG,"onComplete");
                    }

                    @Override
                    public void onRequestNext(User user) {
                        mView.hideProgress();
                        mView.loginSuccess(user,true);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        mView.hideProgress();
                        mView.loginFaild(code,msg);
                    }
                });
    }


    @Override
    public void getData() {
        AppLogger.LOGD(TAG,"getData"+mApi);

    }
}
