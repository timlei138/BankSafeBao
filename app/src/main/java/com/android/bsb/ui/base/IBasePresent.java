package com.android.bsb.ui.base;

public abstract class IBasePresent<T extends IBaseView> {

    protected T mView;


    public void attachView(T view){
        mView = view;
    }

    public void detachView(){
        if(mView != null){
            mView = null;
        }
    }

    public abstract void getData();

}
