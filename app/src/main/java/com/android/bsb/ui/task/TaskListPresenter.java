package com.android.bsb.ui.task;

import android.util.Log;

import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.data.local.LocalDataManager;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import java.security.acl.AclNotFoundException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TaskListPresenter extends IBasePresent<TaskListView> {


    private BankTaskApi mApis;

    private LocalDataManager mDbManager;

    @Inject
    public TaskListPresenter(BankTaskApi apis, LocalDataManager db){
        mApis = apis;
        mDbManager = db;
    }

    @Override
    public void getData() {

    }

    public void getSecurityTaskList(){
        User user  = mView.getLoginUser();
        mApis.querySecurityTaskGroup(user.getUid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<TaskGroupInfo>>() {
            @Override
            public void onRequestNext(List<TaskGroupInfo> groupInfos) {
                mView.showTaskGroupList(groupInfos);
            }

            @Override
            public void onError(int code, String msg) {
                AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
            }
        });
    }

    public void submitErrorTask(int processId,List file,int errorRank,String errorMsg,String geo){
        User user = mView.getLoginUser();
        mApis.taskErrorPrcessResult(user.getUid(),processId,file,errorRank,errorMsg,geo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {
                        AppLogger.LOGD("","");
                        mView.submitErrorInfoSuccess();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                        mView.submitErrorInfoFail();
                    }
                });


    }

    public void submitNormalTask(List<Integer> ids,List<String> geos){
        User user = mView.getLoginUser();
        mApis.taskProcessResult(user.getUid(),ids,geos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {

                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                    }
                });
    }


    public void getProcessResultForDept(){
        User user = mView.getLoginUser();
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        mApis.queryProcessResult(start,end,user.getDeptCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<CheckTaskInfo>>() {
                    @Override
                    public void onRequestNext(List<CheckTaskInfo> checkTaskInfos) {
                        AppLogger.LOGD("demo",""+checkTaskInfos.size());
                        mView.showAllProcessResult(checkTaskInfos);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                    }
                });
    }
}
