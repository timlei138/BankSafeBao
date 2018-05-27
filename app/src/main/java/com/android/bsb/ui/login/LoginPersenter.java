package com.android.bsb.ui.login;

import android.content.Intent;
import android.util.Log;

import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.ui.base.IBaseView;
import com.android.bsb.util.AppLogger;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginPersenter extends IBasePresent<LoginView>{

    private String TAG = "LoginPersenter";

    private BankTaskApi mApi;


    @Inject
    public LoginPersenter(BankTaskApi api){
        mApi = api;
    }



    public void login(String name,String pwd){
        mApi.userLogin(name,pwd).compose(mView.<User>bindToLife())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        AppLogger.LOGD(TAG,"onNext"+user.toString());
                        mView.hideProgress();
                        mView.loginSuccess(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showNetError();
                        mView.hideProgress();
                        AppLogger.LOGD(TAG,"onError e"+e.toString());
                    }

                    @Override
                    public void onComplete() {
                        AppLogger.LOGD(TAG,"onNext");
                    }
                });
    }


    @Override
    public void getData() {
        AppLogger.LOGD(TAG,"getData"+mApi);

    }
}
