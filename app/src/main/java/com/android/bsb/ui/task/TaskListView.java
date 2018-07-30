package com.android.bsb.ui.task;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

import java.util.List;

public interface TaskListView extends IBaseView{

    User getLoginUser();

    void showTaskGroupList(List<TaskGroupInfo> list);
}
