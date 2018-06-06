package com.android.bsb.data.remote;

import com.android.bsb.AppApplication;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.NetWorkUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

public class RetrofitConfig {

    private static final String TAG = "Retrofit";

    //cache 有效期
    static final long CACHE_STALE_SEC = 60 * 60 * 24;
    //查询缓存的Cache-Control设置，为if-only-cache时只会查询缓存而不会请求服务器，max-stale 可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale="+CACHE_STALE_SEC;


    public static final Interceptor sRawWriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtils.isNetworkAvailable(AppApplication.getContext())) {
                AppLogger.LOGE(TAG, "no network !");
            }

            Response originalResponse = chain.proceed(request);

            if (NetWorkUtils.isNetworkAvailable(AppApplication.getContext())) {
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, " + CACHE_CONTROL_CACHE)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    /**
     * 拦截打印Gson信息
     */
    public static final Interceptor sLoggerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request request = chain.request();
            Buffer requestBuffer = new Buffer();
            if(request.body()!=null){
                request.body().writeTo(requestBuffer);
            }else{
                AppLogger.LOGE(TAG,"request.body == null");
            }
            AppLogger.LOGD(TAG,"intercept:"+request.url()+(request.body() != null ? "?" +
                    _parseParams(request.body(),requestBuffer) : ""));
            final Response response = chain.proceed(request);
            return response;
        }
    };


    public static final HttpLoggingInterceptor sHttpLoggerInterceptor = new HttpLoggingInterceptor(
            new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            AppLogger.LOGD("RetrofitServer:",message);
        }
    }).setLevel(HttpLoggingInterceptor.Level.BODY);

    @NonNull
    private static String _parseParams(RequestBody body,Buffer requestBuffer) throws UnsupportedEncodingException {
        if(body.contentType() != null && !body.contentType().toString().contains("multipart")){
            return URLDecoder.decode(requestBuffer.readUtf8(),"utf-8");
        }
        return null;
    }

}
