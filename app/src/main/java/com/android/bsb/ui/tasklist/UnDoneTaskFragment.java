package com.android.bsb.ui.tasklist;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskListAdapter;
import com.android.bsb.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class UnDoneTaskFragment extends BaseFragment<TaskListPresenter>{

    @BindView(R.id.tasklist_recycle)
    RecyclerView mTaskList;

    private TaskListAdapter mAdapter;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist_page;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        mAdapter = new TaskListAdapter(getContext());

        List<TaskAdapterItem> list = new ArrayList<>();

        TaskGroupInfo info = new TaskGroupInfo();
        List<TaskInfo> taskInfos = new ArrayList<>();

        TaskInfo task = new TaskInfo();
        taskInfos.add(task);

        info.setGroupId(100);
        info.setGroupName("dddddd");
        info.setGroupDegree(1);
        info.setGroupDesc("dfsdafdsfasdfadfadfadsfadsf");

        info.setTaskList(taskInfos);

        list.add(TaskAdapterItem.asGroup(0,info));

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter.setItemList(list);
        mTaskList.setLayoutManager(manager);
        mTaskList.setAdapter(mAdapter);

    }

    @Override
    protected void updateView(boolean isRefresh) {

    }
}
