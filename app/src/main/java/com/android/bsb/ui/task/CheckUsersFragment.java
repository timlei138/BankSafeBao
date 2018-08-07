package com.android.bsb.ui.task;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.CheckTaskListAdapter;
import com.android.bsb.ui.base.BaseFragment;

import java.util.List;

import butterknife.BindView;

public class CheckUsersFragment extends BaseFragment<TaskListPresenter> implements TaskListView {

    @BindView(R.id.checkList)
    RecyclerView mCheckList;

    private CheckTaskListAdapter mAdapter;

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
        mAdapter = new CheckTaskListAdapter(getContext(),false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCheckList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mCheckList.setLayoutManager(layoutManager);
        mCheckList.setAdapter(mAdapter);

    }

    @Override
    protected void updateView(boolean isRefresh) {

        mPresenter.getProcessResultForDept();

    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

    }

    @Override
    public void submitErrorInfoSuccess() {

    }

    @Override
    public void submitErrorInfoFail() {

    }

    @Override
    public void showAllProcessResult(List<CheckTaskInfo> list) {
        mAdapter.setList(list);
    }
}
