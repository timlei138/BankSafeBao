package com.android.bsb.data.remote;


import com.android.bsb.bean.User;

import io.reactivex.Observable;


public class BankTaskApi {

    private BankServer mService;

    private static BankTaskApi mInstance;


    public static BankTaskApi getInstance(BankServer server){
        if(mInstance == null){
            mInstance = new BankTaskApi(server);
        }
        return mInstance;
    }


    private BankTaskApi(BankServer server){
        mService = server;
    }


    /**
     * 用户登录接口
     * @param name 用户名
     * @param password 密码
     * @return 用户信息
     */

    public Observable<BaseResultEntity<User>> userLogin(String name, String password){
        return mService.userLogin(name,password);
    }

}
