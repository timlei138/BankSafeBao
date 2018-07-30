package com.android.bsb.ui.task;

import android.util.Log;
import android.widget.Toast;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class TaskManagerPresenter extends IBasePresent<TaskManagerView>{


    private BankTaskApi mApis;

    @Inject
    public TaskManagerPresenter(BankTaskApi apis){
        mApis = apis;
    }

    @Override
    public void getData() {

    }

    public void getGroupListInfo(){
        User loginUser = mView.getLoginUser();
        if(loginUser != null){
            mApis.queryUserTaskGroup(loginUser.getUid())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CommObserver<List<TaskGroupInfo>>() {
                        @Override
                        public void onRequestNext(List<TaskGroupInfo> groupInfos) {
                            mView.showGroupListInfo(groupInfos);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            AppLogger.LOGD(null,"code:"+code+",msg:"+msg);
                        }
                    });
        }else{

        }

    }



    public void addTaskGroupOrTask(TaskGroupInfo info){
        User user = mView.getLoginUser();
        mApis.createTaskGroupAndTask(user,info)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<String>() {
            @Override
            public void onRequestNext(String s) {
                Toast.makeText(mView.getContext(),s,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(mView.getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateOrDelTaskGroup(int groupId,int type,List<Integer> delIds,List<String> addTasks){
        User loginUser = mView.getLoginUser();
        mApis.updateOrDelTaskGroup(groupId,type,loginUser.getUid(),delIds,addTasks)
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


    // groupId:taskId,taskId

    public void publishTask(List<User> securityIds,List<TaskGroupInfo> tasks){
        User user = mView.getLoginUser();
        List<Integer> ids = new ArrayList<>(securityIds.size());
        for (User execUser : securityIds){
            if(execUser.getUid() != -1){
                ids.add(execUser.getUid());
            }
        }
        List<String> taskIds = new ArrayList<>();
        for (TaskGroupInfo info : tasks){
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            for (TaskInfo task : info.getTaskList()){
                if(i == 0){
                    buffer.append(""+info.getGroupId()+":"+task.getTaskId());
                }else{
                    buffer.append("-"+task.getTaskId());
                }
                i++;
            }
            taskIds.add(buffer.toString());
        }

        mApis.publishTask(ids,taskIds,user.getUid())
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
}
