package com.android.bsb.data.remote;

import com.android.bsb.bean.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BankServer {


    @POST("api/login")
    @FormUrlEncoded
    Observable<BaseResultEntity<User>> userLogin(@Field("username") String username, @Field("pwd")String pwd);
}
