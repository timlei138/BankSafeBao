package com.android.bsb.ui.task;

import android.content.Context;

import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.ui.base.IBaseView;

import java.util.List;

public interface TaskView extends IBaseView {

    Context getContext();

    //显示任务组列表
    void showTaskGroupList(List<TaskGroupInfo> groups);

    //结果反馈
    void submitTaskResult(boolean success,List ids,List<String> img);

    //显示所有历史列表
    void showAllProcessTaskResult(List<CheckTaskInfo> recents);

    void onFaildCodeMsg(int code,String msg);

}
