package com.android.bsb.ui.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class TaskGroupListActivity extends BaseActivity<TaskManagerPresenter> implements TaskManagerView {


    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.data_list)
    RecyclerView mDataList;
    @BindView(R.id.submitBtn)
    Button mSubmitBtn;

    private TaskGroupAdapter mAdapter;

    private boolean isSelectTaskFlag = false;

    private Map<Integer,TaskGroupInfo> mTaskGroupList;

    private Map<Integer,List<TaskInfo>> mSelectedList;

    private String TAG = getClass().getSimpleName();

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
        String title = getIntent().getStringExtra(BaseActivity.EXTRA_TITLE);
        mToolsBar.setTitle(title);
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isSelectTaskFlag = getIntent().getBooleanExtra(BaseActivity.EXTRA_PICK_TASK,false);
        if(isSelectTaskFlag){
            mSubmitBtn.setVisibility(View.VISIBLE);
            List<TaskGroupInfo> selectList = getIntent().getParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST);
            mSelectedList = new HashMap();
            for (TaskGroupInfo groupInfo :selectList){
                mSelectedList.put(groupInfo.getGroupId(),groupInfo.getTaskList());
            }

        }else{
            mSubmitBtn.setVisibility(View.GONE);
        }

    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mAdapter = new TaskGroupAdapter(this);
        mDataList.setAdapter(mAdapter);
        mDataList.setLayoutManager(new LinearLayoutManager(this));
        mDataList.addItemDecoration(new TaskListItemDecoration(this));
        mPresenter.getGroupListInfo();
        if(mSelectedList !=null && mSelectedList.size() > 0){
            mAdapter.setSelectList(mSelectedList);
        }

    }

    @Override
    public void showGroupListInfo(List<TaskGroupInfo> groupInfos) {

        AppLogger.LOGD("demo","groupInfo:"+groupInfos.size());
        mTaskGroupList = new HashMap<>();
        mTaskGroupList.clear();
        List<TaskAdapterItem> items = new ArrayList<>(groupInfos.size());

        for (TaskGroupInfo group : groupInfos){
            AppLogger.LOGD("","subSize:"+group.getTaskList().size());
            for (TaskInfo info : group.getTaskList()){
                info.setTaskGroupId(group.getGroupId());

            }
            mTaskGroupList.put(group.getGroupId(),group);

            TaskAdapterItem groupTask = TaskAdapterItem.asGroup(group);
            items.add(groupTask);
        }

        mAdapter.setItemList(items);
        if(isSelectTaskFlag){
            mAdapter.setMultiiSelect(true);
        }else{
            mAdapter.setMultiiSelect(false);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public long getStartDate() {
        return 0;
    }

    @Override
    public long getEndDate() {
        return 0;
    }

    @Override
    public List<Integer> getWeeks() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isSelectTaskFlag){

        }else{
            menu.add("编辑全部");
        }

        return true;
    }


    @OnClick(R.id.submitBtn)
    public void onSubmit(View view){
        Intent intent = new Intent();
        Map<Integer,List<TaskInfo>> selectList = mAdapter.getmSelectedList();
        ArrayList<TaskGroupInfo> finalList  = new ArrayList<>();
        for (Map.Entry<Integer,List<TaskInfo>> entry : selectList.entrySet()){
            AppLogger.LOGD(TAG,"select entry key"+entry.getKey()+",subSize:"+entry.getValue().size());
            if(mTaskGroupList.get(entry.getKey())!= null){
                TaskGroupInfo groupInfo = mTaskGroupList.get(entry.getKey());
                groupInfo.setExpand(false);
                groupInfo.setTaskList(entry.getValue());
                finalList.add(groupInfo);
            }
        }
        intent.putParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST,finalList);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
