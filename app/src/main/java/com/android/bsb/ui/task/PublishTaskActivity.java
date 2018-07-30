package com.android.bsb.ui.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.PendingUserAdapter;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.user.ManagerUserListActivity;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PublishTaskActivity extends BaseActivity<TaskManagerPresenter> implements TaskManagerView {

    @BindView(R.id.people_select_list)
    GridView mPeopleGridView;
    @BindView(R.id.task_group_list)
    RecyclerView mTaskGroupRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.publish_btn)
    AppCompatButton mPublishBtn;


    private List<User> mSelectedPeoples;
    private List<TaskGroupInfo> mSelectedTasks;

    private int REQUESTCODE_SELECT_PEOPLE = 10;

    private int REQUESTCODE_SELECT_TASK = 11;

    private TaskGroupAdapter mTaskAdapter;

    private PendingUserAdapter mUserAdapter;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_publish;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent)
                .build().inject(this);

    }

    @Override
    protected void initView() {
        mSelectedPeoples = new ArrayList<>();
        mSelectedTasks = new ArrayList<>();
        mToolsBar.setTitle(R.string.menu_pushtask_title);
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTaskAdapter = new TaskGroupAdapter(this);
        mUserAdapter = new PendingUserAdapter(this,addUserClick);
        mPeopleGridView.setAdapter(mUserAdapter);
        mTaskGroupRecyclerView.setAdapter(mTaskAdapter);
        mTaskGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskGroupRecyclerView.addItemDecoration(new TaskListItemDecoration(this));
        mTaskAdapter.setOnAddClickListener(addTaskClick);
        TaskAdapterItem addItem = TaskAdapterItem.asAddItem();
        List<TaskAdapterItem> items = new ArrayList<>();
        items.add(addItem);
        mTaskAdapter.setItemList(items);



    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        User addItem = new User();
        addItem.setUid(-1);
        addItem.setUname("点击添加");
        mUserAdapter.addPendItem(addItem);


    }

    @Override
    public void showGroupListInfo(List<TaskGroupInfo> groupInfos) {

    }

    @Override
    public Context getContext() {
        return this;
    }


    private PendingUserAdapter.OnAddUserClick addUserClick = new
            PendingUserAdapter.OnAddUserClick() {
        @Override
        public void onAddUser() {
            startPeopleSelected();
        }
    };

    private TaskGroupAdapter.OnAddItemClickListener addTaskClick = new
            TaskGroupAdapter.OnAddItemClickListener() {
        @Override
        public void onAddClick() {
            startTaskSelected();
        }
    };

    @OnClick(R.id.publish_btn)
    public void onPublishTask(View v){
        publishTask();
    }

    private void publishTask(){
        if(mSelectedPeoples== null || mSelectedPeoples.size() <= 0){
            Toast.makeText(this,"请选择执行人员",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mSelectedTasks == null || mSelectedTasks.size() <= 0){
            Toast.makeText(this,"请选择要执行的任务列表",Toast.LENGTH_SHORT).show();
            return;
        }
        mPresenter.publishTask(mSelectedPeoples,mSelectedTasks);
    }


    private void startTaskSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), TaskGroupListActivity.class);
        intent.putExtra("title","请选择任务组");
        intent.putExtra("select",true);
        startActivityForResult(intent,REQUESTCODE_SELECT_TASK);
    }

    private void startPeopleSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), ManagerUserListActivity.class);
        intent.putExtra("publish",true);
        intent.putParcelableArrayListExtra("select",mUserAdapter.getPendingUserList());
        startActivityForResult(intent,REQUESTCODE_SELECT_PEOPLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppLogger.LOGD("demo","requestCode:"+requestCode+",resultCode:"+resultCode+",data:"+data);
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == REQUESTCODE_SELECT_PEOPLE){
                mSelectedPeoples = data.getParcelableArrayListExtra("security");
                updateUserView();
            }else if(requestCode == REQUESTCODE_SELECT_TASK){
                mSelectedTasks = data.getParcelableArrayListExtra("select");
                updateTaskView();
            }

        }
    }


    private void updateUserView(){
        if(mSelectedPeoples!= null){
            mUserAdapter.setPendingList(mSelectedPeoples);
        }
    }

    private void updateTaskView(){
        if(mSelectedTasks != null){
            List<TaskAdapterItem> items = new ArrayList<>();
            for (TaskGroupInfo info : mSelectedTasks){
                items.add(TaskAdapterItem.asGroup(info));
            }
            mTaskAdapter.setItemList(items);
        }
    }
}
