package com.android.bsb.ui.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.TaskInfoAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AddTaskGroupOrTaskActivity extends BaseActivity<TaskPresenter> implements TaskView{

    @BindView(R.id.toolbar)
    Toolbar mToolsBar;

    @BindView(R.id.group_spinner)
    AppCompatSpinner mGroupSpinner;

    @BindView(R.id.group_layout)
    View mGroupLayout;

    @BindView(R.id.group_title)
    EditText mGroupTileEd;
    @BindView(R.id.group_desc)
    EditText mGroupDescEd;

    @BindView(R.id.fab)
    FloatingActionButton mAddButton;
    @BindView(R.id.taskListRecycler)
    RecyclerView mPeddingRecycler;

    private ArrayAdapter<TaskGroupInfo> mTaskGroupSpinnerAdapter;

    private List<TaskGroupInfo> mTaskGroupList;

    private TaskGroupInfo mSelectedTaskGroupInfo;

    private List<TaskInfo> mPeddingTaskList = new ArrayList<>();

    private TaskInfoAdapter mPeddingAdapter;

    private boolean isAddTaskToExistsGroup = false;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_addtaskgroup;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).build()
                .inject(this);
    }

    @Override
    protected void initView() {
        mToolsBar.setTitle(R.string.menu_add_task_title);
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTaskGroupList = new ArrayList<>();
        mTaskGroupSpinnerAdapter = new ArrayAdapter<TaskGroupInfo>(this,android.R.layout.simple_spinner_dropdown_item);
        mTaskGroupList.add(new TaskGroupInfo(TaskGroupInfo.TYPE_NONE,"请选择任务组"));
        mTaskGroupList.add(new TaskGroupInfo(TaskGroupInfo.TYPE_CREATE,"新建任务组"));
        mTaskGroupSpinnerAdapter.addAll(mTaskGroupList);
        mTaskGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupSpinner.setAdapter(mTaskGroupSpinnerAdapter);
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedTaskGroupInfo = mTaskGroupList.get(position);

                if(mSelectedTaskGroupInfo.getGroupId() == TaskGroupInfo.TYPE_CREATE){
                    mGroupLayout.setVisibility(View.VISIBLE);
                    isAddTaskToExistsGroup = false;
                }else if(mSelectedTaskGroupInfo.getGroupId() == TaskGroupInfo.TYPE_NONE){
                    mGroupLayout.setVisibility(View.GONE);
                    isAddTaskToExistsGroup = false;
                }else{
                    isAddTaskToExistsGroup = true;
                    mGroupLayout.setVisibility(View.GONE);
                }
                AppLogger.LOGD(null,"SelectedTaskGroup:"+mSelectedTaskGroupInfo.toString());
                mPeddingAdapter.setList(mSelectedTaskGroupInfo);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedTaskGroupInfo = mTaskGroupList.get(0);
                mPeddingAdapter.setList(mSelectedTaskGroupInfo);
                isAddTaskToExistsGroup = false;
            }
        });

        mPeddingRecycler.setLayoutManager(new LinearLayoutManager(this));
        mPeddingAdapter = new TaskInfoAdapter(this);
        mPeddingRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPeddingRecycler.setAdapter(mPeddingAdapter);


    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getTaskGroupList();
    }


    @OnClick(R.id.fab)
    public void addNewTask(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("任务描述");
        View view = getLayoutInflater().inflate(R.layout.layout_peddingtask,null);
        final EditText content = view.findViewById(R.id.task_content);
        build.setView(view);
        build.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = content.getText().toString();
                if(TextUtils.isEmpty(task)){
                    Toast.makeText(getBaseContext(),"任务不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                TaskInfo info = new TaskInfo();
                info.setTaskName(task);
                mPeddingTaskList.add(info);
                mPeddingAdapter.addTask(info);
                dialog.dismiss();
            }
        });
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        build.setCancelable(false).create().show();
    }

    @OnClick(R.id.submitBtn)
    public void onSubmit(){
        if(mSelectedTaskGroupInfo.getGroupId() == TaskGroupInfo.TYPE_NONE){
            Toast.makeText(getBaseContext(),"请选择或创建任务组",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mSelectedTaskGroupInfo.getGroupId() == TaskGroupInfo.TYPE_CREATE){
            String groupTitle = mGroupTileEd.getText().toString();
            String desc = mGroupDescEd.getText().toString();
            if(TextUtils.isEmpty(groupTitle) || TextUtils.isEmpty(desc)){
                Toast.makeText(getBaseContext(),"请填写任务组信息",Toast.LENGTH_SHORT).show();
                return;
            }
            mSelectedTaskGroupInfo = new TaskGroupInfo();
            mSelectedTaskGroupInfo.setGroupName(groupTitle);
            mSelectedTaskGroupInfo.setGroupDesc(desc);
            mSelectedTaskGroupInfo.setTaskList(mPeddingTaskList);
        }

        if(isAddTaskToExistsGroup){
            List<String> peddingString = new ArrayList<>();
            for (TaskInfo info : mPeddingTaskList){
                peddingString.add(info.getTaskName());
            }
            mPresenter.addNewTaskToGroup(mSelectedTaskGroupInfo.getGroupId(),TaskCommon.ACTION_ADD_TASK_TO_EXISTSGROUP,
                    new ArrayList<Integer>(),peddingString);
        }else{
            mPresenter.createNewTaskGroup(mSelectedTaskGroupInfo);
        }

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> groups) {
        mTaskGroupList.addAll(groups);
        mTaskGroupSpinnerAdapter.addAll(groups);
        mTaskGroupSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void submitTaskResult(boolean success) {
        if(success){
            Toast.makeText(getContext(),"添加任务成功",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(getContext(),"添加任务失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {

    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {

    }

    @Override
    public long[] getTaskExecuteDate() {
        return new long[0];
    }

    @Override
    public List<Integer> getWeeks() {
        return null;
    }
}