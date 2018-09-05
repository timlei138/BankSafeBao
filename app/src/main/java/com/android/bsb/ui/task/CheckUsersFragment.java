package com.android.bsb.ui.task;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.UserListAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CheckUsersFragment extends BaseFragment<TaskPresenter> implements TaskView {

    @BindView(R.id.userList)
    ListView mCheckList;

    private UserListAdapter mAdapter;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_check_list;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).
                build().inject(this);
    }

    @Override
    protected void initView() {
        mAdapter = new UserListAdapter(getContext());
        mCheckList.setAdapter(mAdapter);
        mCheckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<TaskInfo> recentList = new ArrayList<>();
                List<TaskInfo> list = mAdapter.getExecTaskList(position);
                recentList.addAll(list);
                Intent intent = new Intent();
                intent.setClass(getContext(),RecentTaskListActivity.class);
                intent.putParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST,recentList);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getProcessResultForDept();
    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

    }

    @Override
    public void submitTaskResult(boolean success,List ids,List imgs) {

    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {
        mAdapter.setList(recents);
    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {

    }

}
