package com.android.bsb.ui.task;

import android.content.Context;

import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

import java.util.List;

public interface TaskListView extends IBaseView{

    User getLoginUser();

    void showTaskGroupList(List<TaskGroupInfo> list);

    void submitErrorInfoSuccess();

    void submitErrorInfoFail();

    void showAllProcessResult(List<CheckTaskInfo> list);

    Context getContext();

}
