package com.android.bsb.module;

import java.util.concurrent.TimeUnit;

import com.android.bsb.AppApplication;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.data.remote.BankServer;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.RetrofitConfig;
import com.android.bsb.util.AppLogger;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class HttpModule {

    @Provides
    OkHttpClient.Builder providerOkHttpClient(){
        Cache cache = new Cache(new File(AppApplication.getContext().getCacheDir(),
                "httpCache"),1024 * 1024 * 50);
        return new OkHttpClient.Builder().cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(RetrofitConfig.sLoggerInterceptor)
                .addInterceptor(RetrofitConfig.sRawWriteCacheControlInterceptor)
                .addNetworkInterceptor(RetrofitConfig.sRawWriteCacheControlInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS);
    }

    @Provides
    BankTaskApi providerBankTaskApis(OkHttpClient.Builder builder){
        //builder.addInterceptor(RetrofitConfig.)
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build());
        return BankTaskApi.getInstance(retrofitBuilder
                .baseUrl(NetComm.HOST_NAME)
                .build().create(BankServer.class));
    }

}
