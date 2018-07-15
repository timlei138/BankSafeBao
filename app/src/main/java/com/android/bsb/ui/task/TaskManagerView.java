package com.android.bsb.ui.task;

import android.content.Context;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

import java.util.List;

public interface TaskManagerView extends IBaseView{


    void showGroupListInfo(List<TaskGroupInfo> groupInfos);

    User getLoginUser();

    Context getContext();
}
