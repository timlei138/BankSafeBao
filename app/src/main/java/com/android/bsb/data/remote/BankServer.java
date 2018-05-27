package com.android.bsb.data.remote;

import com.android.bsb.bean.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BankServer {


    @POST("user/")
    @FormUrlEncoded
    Observable<BaseResultEntity<User>> userLogin(@Field("loginname") String username,
                                                 @Field("password")String pwd);
}
