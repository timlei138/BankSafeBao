package com.android.bsb.service;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import com.android.bsb.AppApplication;
import com.android.bsb.bean.StepData;
import com.android.bsb.bean.StepData_;
import com.android.bsb.bean.User;
import com.android.bsb.data.local.LocalDataManager;
import com.android.bsb.data.remote.BankServer;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.data.remote.RetrofitConfig;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.SharedProvider;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadStepService extends JobService {

    private String TAG = getClass().getSimpleName();

    private final int UPDATE_JOB_ID = 1000;

    private final int UPLOAD_SUCCESS = 2000;

    private final int UPLOAD_FAILD = 2001;

    private User mCurrUser;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppLogger.LOGD(TAG,"finish job->"+msg.what);
            jobFinished((JobParameters) msg.obj,true);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrUser = AppApplication.getLoginUser();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob();
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        AppLogger.LOGD(TAG,"onStartJob");
        new UpdateThread(getApplicationContext(),params).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    private void scheduleJob(){
        AppLogger.LOGD(TAG,"scheduleJob");
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);


        JobInfo pendingJob  = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pendingJob = jobScheduler.getPendingJob(UPDATE_JOB_ID);
        }else{
            List<JobInfo> lists = jobScheduler.getAllPendingJobs();
            for (JobInfo info : lists){
                if(info.getId() == UPDATE_JOB_ID);
                pendingJob = info;
                return;
            }
        }

        if(pendingJob!=null){
            AppLogger.LOGD(TAG,"job exists return !");
            return;
        }

        JobInfo.Builder builder = new JobInfo.Builder(UPDATE_JOB_ID,
                new ComponentName(this,getClass()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_METERED);
        builder.setOverrideDeadline(getOverideDeadLine());
        jobScheduler.schedule(builder.build());

    }



    private long getOverideDeadLine(){
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        Calendar deadLine = Calendar.getInstance();
        long randomTime = Math.round(Math.random() * 1000 * 60 * 5);
        deadLine.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),22,0,0);
        return deadLine.getTimeInMillis() - time + randomTime;
    }


    private class UpdateThread extends Thread{

        Box<StepData> dataBox;

        JobParameters parameters;

        UpdateThread(Context context,JobParameters parameters){
            BoxStore boxStore = LocalDataManager.getInstance(context).getBoxStore();
            dataBox = boxStore.boxFor(StepData.class);
            this.parameters = parameters;
        }

        @Override
        public void run() {
            //super.run();
            List<StepData> unmergedList = dataBox.query().equal(StepData_.merge,false).build().find();
            AppLogger.LOGD(TAG,"unmergedList:"+unmergedList.size());

            if(unmergedList.isEmpty()){
                return;
            }

            for (final StepData data : unmergedList){
                String[] time = data.getToday().split("-");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(time[2]));

                int uid = data.getUid() != -1 ? data.getUid() : mCurrUser.getUid();
                getApi().uploadStep(uid,data.getStep(),calendar.getTimeInMillis())
                        .observeOn(Schedulers.io())
                        .subscribe(new CommObserver<String>() {
                            @Override
                            public void onRequestNext(String s) {
                                AppLogger.LOGD(TAG,"update step success");
                                data.setMerge(true);
                                dataBox.put(data);
                                Message message = mHandler.obtainMessage();
                                message.obj = parameters;
                                message.what = UPLOAD_SUCCESS;
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(int code, String msg) {
                                AppLogger.LOGD(TAG,"update step faild");
                                Message message = mHandler.obtainMessage();
                                message.obj = parameters;
                                message.what = UPLOAD_FAILD;
                                mHandler.sendMessage(message);
                            }
                        });
            }

        }



        private BankTaskApi getApi(){
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(providerOkHttpClient().build());

            SharedProvider provider = SharedProvider.getInstance(AppApplication.getContext());
            String saveConfig = provider.getStringValue(NetComm.KEY_IP,"");
            AppLogger.LOGD("demo","saveConfig:"+saveConfig);
            if(!TextUtils.isEmpty(saveConfig)){
                NetComm.setIpConfig(saveConfig);
            }

            return BankTaskApi.getInstance(retrofitBuilder
                    .baseUrl(NetComm.getHost())
                    .build().create(BankServer.class));
        }

        OkHttpClient.Builder providerOkHttpClient(){
            Cache cache = new Cache(new File(AppApplication.getContext().getCacheDir(),
                    "httpCache"),1024 * 1024 * 50);
            return new OkHttpClient.Builder().cache(cache)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(RetrofitConfig.sLoggerInterceptor)
                    .addInterceptor(RetrofitConfig.sHttpLoggerInterceptor)
                    .addInterceptor(RetrofitConfig.sRawWriteCacheControlInterceptor)
                    .addNetworkInterceptor(RetrofitConfig.sRawWriteCacheControlInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15,TimeUnit.SECONDS)
                    .readTimeout(15,TimeUnit.SECONDS);
        }


    }
}
