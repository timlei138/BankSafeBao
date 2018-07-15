package com.android.bsb.ui.user;

import com.android.bsb.AppComm;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankServer;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.data.remote.ServerException;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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

        mApis.createDept(deptName,uname,phone,info.getDeptCode(),info.getUid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<String>() {

                    @Override
                    public void onRequestNext(String s) {
                        mView.addUserDeptSuccess(s);
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        mView.hideProgress();
                        mView.addUserDeptFaild(code,msg);
                    }
                });
    }


    public void addUser(String uname,String phone ,int role){
        User user = mView.getUserInfo();
        mApis.createUser(uname,phone,user.getDeptCode(),phone,role,user.getUid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<String>() {

                    @Override
                    public void onRequestNext(String s) {
                        mView.addUserDeptSuccess(s);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        mView.addUserDeptFaild(code,msg);
                    }
                });

    }

    public void queryAllDept(){
        mApis.queryAllUser(mView.getUserInfo().getUid())
                .compose(mView.<List<User>>bindToLife())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<User>>() {
                    @Override
                    public void onRequestNext(List<User> list) {
                        mView.hideProgress();
                        mView.showAllDeptInfo(list);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD(null,"onError");
                        mView.hideProgress();
                        List<User> userList = new ArrayList<>();

                        for (int i =0;i<10;i++){
                            User user = new User();
                            user.setRole(AppComm.ROLE_TYPE_POSTMAN);
                            user.setUname("Tom"+i);
                            user.setDeptName("d大家都反对");
                            userList.add(user);
                        }
                        mView.showAllDeptInfo(userList);
                    }
                });
    }



}
