package com.android.bsb.ui.user;

import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankServer;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class UserManagerPresenter extends IBasePresent<UserManagerView>{


    private BankTaskApi mApis;

    @Inject
    public UserManagerPresenter(BankTaskApi api){
        mApis = api;
    }

    @Override
    public void getData() {

    }



    public void addNewDept(String deptName,String phone,String uname){
        User info = mView.getUserInfo();

        mApis.createDept(deptName,uname,phone,info.getDeptCode(),info.getUid()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                AppLogger.LOGD("UserManagerP","s:"+s);
            }

            @Override
            public void onError(Throwable e) {
                AppLogger.LOGD("UserManagerP","s"+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public void addUser(String uname,String phone ,int role){
        User user = mView.getUserInfo();
        mApis.createUser(uname,phone,user.getDeptCode(),phone,role,user.getUid()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                AppLogger.LOGD("UserManagerP","s:"+s);
            }

            @Override
            public void onError(Throwable e) {
                AppLogger.LOGD("UserManagerP","s"+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

    }

}
