package com.android.bsb.ui.task;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TaskFragment extends BaseFragment<TaskListPresenter> implements TaskListView{


    @BindView(R.id.tasklist_recycle)
    RecyclerView mTaskListRecycler;

    private TaskGroupAdapter mAdapter;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist_page;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().
                applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        mAdapter = new TaskGroupAdapter(getContext());
        mAdapter.setOnOptionsSelectListener(mOptionSelectListener);
        mTaskListRecycler.setAdapter(mAdapter);
        mTaskListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTaskListRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getSecurityTaskList();
    }

    @Override
    public User getLoginUser() {
        return super.getLoginUser();
    }


    @Override
    public void syncData() {
        super.syncData();
        AppLogger.LOGD("demo","syncData");
        updateView(false);
    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

        List<TaskAdapterItem> items = new ArrayList<>(list.size());

        for (TaskGroupInfo group : list){
            items.add(TaskAdapterItem.asGroup(group));
        }
        mAdapter.setItemList(items);
    }

    @Override
    public void submitErrorInfoSuccess() {

    }

    @Override
    public void submitErrorInfoFail() {

    }


    private TaskGroupAdapter.OptionSelectListener mOptionSelectListener = new TaskGroupAdapter.OptionSelectListener() {
        @Override
        public void onOptionsNormal(TaskInfo info) {
            List<Integer> ids = new ArrayList<>();
            ids.add(info.getProcessId());
            List<String> geos = new ArrayList<>();
            geos.add("199928:112933");
            mPresenter.submitNormalTask(ids,geos);
        }

        @Override
        public void onOptionsError(TaskInfo info) {
            Intent intent = new Intent();
            intent.setClass(getContext(), ErrorTaskEditorActivity.class);
            intent.putExtra("processId",info.getProcessId());
            startActivity(intent);
        }
    };
}
