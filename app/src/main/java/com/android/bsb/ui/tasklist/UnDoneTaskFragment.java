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
            group.setGroupName("日程任务组:"+i);
            group.setGroupDesc("阿娇考虑是否卡拉斯科水淀粉福建路上的风景拉萨的房间里的房间里水淀粉" +
                    "的房间啊善良的风景拉萨地方登陆书法家三闾大夫"+i);
            ArrayList<TaskInfo> lists = new ArrayList<>();
            for (int j=0;j<5;j++){
                TaskInfo task = new TaskInfo();
                task.setTaskGroupId(group.getGroupId());
                task.setTaskId(100*(j+1));
                task.setTaskName("我的任务第一条是神恶魔但是房间啊善良大方"+j);
                lists.add(task);
            }
            group.setTaskList(lists);
            list.add(TaskAdapterItem.asGroup(group));
        }


        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAdapter.setItemList(list);
        mTaskList.setLayoutManager(manager);
        mTaskList.setAdapter(mAdapter);
        mTaskList.addItemDecoration(new TaskListItemDecoration());
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
