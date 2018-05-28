package com.android.bsb.ui.tasklist;

import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.ui.base.IBasePresent;

import javax.inject.Inject;

public class TaskListPresenter extends IBasePresent<TaskListView> {


    private BankTaskApi mApis;

    @Inject
    public TaskListPresenter(BankTaskApi apis){
        mApis = apis;
    }

    @Override
    public void getData() {

    }
}
