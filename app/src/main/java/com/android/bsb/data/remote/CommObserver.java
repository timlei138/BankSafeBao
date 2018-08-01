package com.android.bsb.data.remote;

import com.android.bsb.util.AppLogger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class CommObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onRequestNext(t);
    }

    @Override
    public void onError(Throwable e) {
        AppLogger.LOGD(null,"onError->"+e.toString());
        if(e instanceof ApiException){
            onError(((ApiException) e).getCode(),((ApiException) e).getDisplayMessage());
        }else{

        }

    }

    @Override
    public void onComplete() {

    }


    public abstract void onRequestNext(T t);

    public abstract void onError(int code,String msg);
}
