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

        for (int i=0;i<5;i++){
            TaskGroupInfo group = new TaskGroupInfo();
            group.setGroupId(10*(i+1));
            group.setGroupName("TaskGroup Id:"+i);
            group.setGroupDesc("curr group task desc "+i);
            ArrayList<TaskInfo> lists = new ArrayList<>();
            for (int j=0;j<5;j++){
                TaskInfo task = new TaskInfo();
                task.setTaskGroupId(group.getGroupId());
                task.setTaskId(100*(j+1));
                task.setTaskName("detail task name i"+j);
                lists.add(task);
            }
            group.setTaskList(lists);
            list.add(TaskAdapterItem.asGroup(group));
        }


        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter.setItemList(list);
        mTaskList.setLayoutManager(manager);
        mTaskList.setAdapter(mAdapter);
        mAdapter.setOnScrollListener(new TaskListAdapter.OnScrollListener() {
            @Override
            public void scrollTo(int pos) {
                mTaskList.scrollToPosition(pos);
            }
        });

    }

    @Override
    protected void updateView(boolean isRefresh) {

    }
}
