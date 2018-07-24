package com.android.bsb.ui.tasklist;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskListAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.task.TaskManagerPresenter;
import com.android.bsb.ui.task.TaskManagerView;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TaskManagerListActivity extends BaseActivity<TaskManagerPresenter> implements TaskManagerView {


    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.data_list)
    RecyclerView mDataList;

    private boolean editAllFlag = false;

    private TaskListAdapter mAdapter;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_toolsbar_listview;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        String title = getIntent().getStringExtra("title");
        mToolsBar.setTitle(title);
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mAdapter = new TaskListAdapter(this);
        mDataList.setAdapter(mAdapter);
        mDataList.setLayoutManager(new LinearLayoutManager(this));
        mDataList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mPresenter.getGroupListInfo();
    }

    @Override
    public void showGroupListInfo(List<TaskGroupInfo> groupInfos) {

        AppLogger.LOGD("demo","groupInfo:"+groupInfos.size());

        List<TaskAdapterItem> items = new ArrayList<>(groupInfos.size());

        for (TaskGroupInfo group : groupInfos){

            TaskAdapterItem groupTask = TaskAdapterItem.asGroup(group);
            items.add(groupTask);
        }


        mAdapter.setItemList(items);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("编辑全部");
        return true;
    }
}
