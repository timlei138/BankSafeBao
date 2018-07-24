package com.android.bsb.ui.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.tasklist.TaskManagerListActivity;
import com.android.bsb.ui.user.ManagerUserListActivity;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PublishTaskActivity extends BaseActivity<TaskManagerPresenter> implements TaskManagerView {

    @BindView(R.id.peopleselect_tv)
    TextView mSelectPeople;
    @BindView(R.id.taskselect_tv)
    TextView mSelectTask;

    private List<Integer> mSelectedPeoples;
    private List<String> mSelectedTasks;

    private int REQUESTCODE_SELECT_PEOPLE = 10;

    private int REQUESTCODE_SELECT_TASK = 11;

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
    }

    @Override
    protected Activity addActivityStack() {
        return null;
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }

    @Override
    public void showGroupListInfo(List<TaskGroupInfo> groupInfos) {

    }

    @Override
    public Context getContext() {
        return this;
    }


    @OnClick({R.id.peopleselect_btn,R.id.taskselect_btn,R.id.publish_btn})
    public void preClick(View view){
        int id = view.getId();
        if(id == R.id.peopleselect_btn){
            startPeopleSelected();
        }else if(id == R.id.taskselect_btn){
            startTaskSelected();
        }else if(id == R.id.publish_btn){
            publishTask();
        }
    }


    private void publishTask(){
            mSelectedTasks = new ArrayList<>();


            mSelectedTasks.add("6:19");
            mSelectedTasks.add("7:21,22");

            mPresenter.publishTask(mSelectedPeoples,mSelectedTasks);
    }


    private void startTaskSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), TaskManagerListActivity.class);
        startActivityForResult(intent,REQUESTCODE_SELECT_TASK);
    }

    private void startPeopleSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), ManagerUserListActivity.class);
        intent.putExtra("publish",true);
        startActivityForResult(intent,REQUESTCODE_SELECT_PEOPLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AppLogger.LOGD("demo","requestCode:"+requestCode+",resultCode:"+resultCode+",data:"+data);
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == REQUESTCODE_SELECT_PEOPLE){
                mSelectedPeoples = data.getIntegerArrayListExtra("security");
            }else if(requestCode == REQUESTCODE_SELECT_TASK){
                //mSelectedTasks = data.getIntegerArrayListExtra("task");
            }

        }
    }
}
