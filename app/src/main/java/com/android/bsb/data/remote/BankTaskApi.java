package com.android.bsb.data.remote;


import com.android.bsb.bean.User;
import com.android.bsb.util.AppLogger;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


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

    public Observable<User> userLogin(String name, String password){
        return mService.userLogin(name,password)
                .map(new ServerResultFunc<User>())
                .onErrorResumeNext(new HttpResultFunc<User>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> createDept(String deptname,String username,String phone,int parentcode,int userid){
        return mService.createDeptment(deptname,username,phone,parentcode,userid)
                .map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> createUser(String name,String phone,int deptCode,String loginname,int role,int uid){
        return mService.createUser(name,phone,deptCode,loginname,role,uid)
                .map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }


    private class ServerResultFunc<T> implements Function<BaseResultEntity<T>, T> {
        @Override
        public T apply(BaseResultEntity<T> httpResult) throws Exception {
            AppLogger.LOGD(null,"httpResult:"+httpResult.toString());
            if (httpResult.getCode() != 200) {
                throw new ServerException(httpResult.getCode(),httpResult.getMsg());

            }
            return httpResult.getData();
        }
    }

    private class HttpResultFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

}
