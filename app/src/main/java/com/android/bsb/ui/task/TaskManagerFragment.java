package com.android.bsb.ui.task;


import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.adapter.FunctinAdapter;
import com.android.bsb.ui.adapter.FunctionItem;
import com.android.bsb.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TaskManagerFragment extends BaseFragment<TaskManagerPresenter> {

    private final int MANAGER_TAKSGROUP_FLAG = 10;
    private final int ADD_TASK_FLAG = 11;
    private final int PUBLISH_TASK_FLAG = 12;


    @BindView(R.id.grid_view)
    GridView mTableLaout;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_gridmanager;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        List<FunctionItem> list = new ArrayList<>();
        list.add(new FunctionItem(R.string.menu_managertaskgroup_title,R.drawable.ic_add,MANAGER_TAKSGROUP_FLAG));
        list.add(new FunctionItem(R.string.menu_add_task_title,R.drawable.ic_add,ADD_TASK_FLAG));
        list.add(new FunctionItem(R.string.menu_pushtask_title,R.drawable.ic_add,PUBLISH_TASK_FLAG));
        FunctinAdapter adapter = new FunctinAdapter(getContext(),list);
        mTableLaout.setAdapter(adapter);

        mTableLaout.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }


    GridView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int action = (int) view.getTag();
            Intent intent = new Intent();
            switch (action){
                case MANAGER_TAKSGROUP_FLAG:
                    intent.setClass(getContext(), TaskGroupListActivity.class);
                    intent.putExtra("title",getString(R.string.menu_managertaskgroup_title));
                    startActivity(intent);
                    break;
                case ADD_TASK_FLAG:

                    intent.setClass(getContext(),AddTaskGroupOrTaskActivity.class);
                    startActivity(intent);
                    break;
                case PUBLISH_TASK_FLAG:
                    intent.setClass(getContext(),PublishTaskActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
