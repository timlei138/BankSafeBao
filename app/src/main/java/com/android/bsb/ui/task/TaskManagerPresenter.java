package com.android.bsb.ui.task;

import android.widget.Toast;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

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
        //mApis.queryUserTaskGroup()
    }



    public void addTaskGroupOrTask(TaskGroupInfo info){
        AppLogger.LOGD(null,info.toString());
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
}
