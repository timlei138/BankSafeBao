package com.android.bsb.ui.task;

import android.util.Log;
import android.widget.Toast;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;

public class TaskPresenter extends IBasePresent<TaskView>{

    private BankTaskApi mApis;
    private User mLoginUser;

    @Inject
    public TaskPresenter(BankTaskApi api){
        mApis = api;
        mLoginUser = mView.getLoginUser();
    }


    public void getTaskGroupList(){
        mApis.queryUserTaskGroup(mLoginUser.getUid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<TaskGroupInfo>>() {
                    @Override
                    public void onRequestNext(List<TaskGroupInfo> groupInfos) {

                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD(null,"code:"+code+",msg:"+msg);
                    }
                });
    }


    public void addNewTaskToGroup(int groupId,int type,List<Integer> delIds,List<String> addTasks){
        mApis.updateOrDelTaskGroup(groupId,type,mLoginUser.getUid(),delIds,addTasks)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {
                        AppLogger.LOGD("demo",""+o);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                    }
                });

    }

    public void createNewTaskGroup(TaskGroupInfo info){
        mApis.createTaskGroupAndTask(mLoginUser,info)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {
                        //Toast.makeText(mView.getContext(),s,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        //Toast.makeText(mView.getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void publishTaskToSecurity(List<Integer> securityIds,List<String> taskIds){
        mApis.publishTask(securityIds,taskIds,mLoginUser.getUid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {
                        Log.d("demo","");
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Log.d("demo","code:"+code+",msg:"+msg);
                    }
                });
    }


    public void getSecurityProcessList(){
        mApis.querySecurityTaskGroup(mLoginUser.getUid())
                .compose(mView.<List<TaskGroupInfo>>bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<TaskGroupInfo>>() {
                    @Override
                    public void onRequestNext(List<TaskGroupInfo> list) {

                    }

                    @Override
                    public void onError(int code, String msg) {

                    }
                });

    }


    public void feedbackErrorTaskResult(int processId,List file,int errorRank,String errorMsg){
        mApis.taskErrorPrcessResult(processId,mLoginUser.getUid(),file,errorRank,errorMsg,"")
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mView.<String>bindToLife())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {

                    }

                    @Override
                    public void onError(int code, String msg) {

                    }
                });
    }

    public void feedbackNormalTaskList(List<Integer> ids,List<String> geos){
        mApis.taskProcessResult(mLoginUser.getUid(),ids,geos)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mView.<String>bindToLife())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {

                    }

                    @Override
                    public void onError(int code, String msg) {

                    }
                });

    }


    @Override
    public void getData() {

    }
}
