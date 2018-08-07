package com.android.bsb.ui.task;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.adapter.TaskListPageAdapter;
import com.android.bsb.ui.base.BaseFragment;

import butterknife.BindView;

public class TaskListFragment extends BaseFragment{


    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.pager_layout)
    ViewPager mViewPager;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        String[] titles = new String[]{getString(R.string.tab_task_undone_title),
                getString(R.string.tab_task_done_title),getString(R.string.tab_task_error_title)};
        TaskListPageAdapter mAdapter = new TaskListPageAdapter(getFragmentManager(),titles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }
}
