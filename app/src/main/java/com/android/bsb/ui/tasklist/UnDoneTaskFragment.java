package com.android.bsb.ui.tasklist;

import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.base.BaseFragment;

public class UnDoneTaskFragment extends BaseFragment<TaskListPresenter>{
    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist_page;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void updateView(boolean isRefresh) {

    }
}
