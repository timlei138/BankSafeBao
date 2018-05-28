package com.android.bsb.ui.splash;

import android.content.Context;
import android.text.TextUtils;

import com.android.bsb.AppComm;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.SharedProvider;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

//import com.android.bsb.data.remote.ExceptionEngine;

public class SplashPersenter extends IBasePresent<SplashView> {


    private SharedProvider mSharedProvider;

    private BankTaskApi mApis;


    @Inject
    public SplashPersenter(Context context, BankTaskApi apis) {
        mSharedProvider = SharedProvider.getInstance(context);
        mApis = apis;
    }

    public boolean isFirstLogin() {
        return mSharedProvider.getBoolValue(AppComm.KEY_FIRST_USED, true);
    }


    public void autoLogin() {
        ObservableOnSubscribe subscribe = new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                String phone = mSharedProvider.getStringValue(AppComm.KEY_ACCOUNT,"");
                String pwd = mSharedProvider.getStringValue(AppComm.KEY_PWD,"");
                if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwd)){
                    //emitter.onError(ExceptionEngine.handleException(new Throwable("encode error")));
                }else{
                    emitter.onNext(new String[]{phone,pwd});
                    emitter.onComplete();
                }
            }
        };
        Observable.create(subscribe).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .flatMap(new Function<String[], ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(String[] strings) throws Exception {
                        return mApis.userLogin(strings[0], strings[1]);
                    }
                })
                .observeOn(Schedulers.io())
                .compose(mView.<User>bindToLife())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        mView.hideProgress();
                        mView.loginResult(true,user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.loginResult(false,null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void getData() {

    }


}
