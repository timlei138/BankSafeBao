package com.android.bsb.ui.task;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.service.LocationService;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class TaskFragment extends BaseFragment<TaskPresenter> implements TaskView{


    @BindView(R.id.tasklist_recycle)
    RecyclerView mTaskListRecycler;
    @BindView(R.id.submit_btn)
    AppCompatButton mAllsubmatBtn;

    private TaskGroupAdapter mAdapter;

    private boolean isMutiSelect = false;

    private ServiceConnection mServiceConnect;

    private boolean isServiceConnected = false;

    private LocationService mLocationService;

    private String TAG = getClass().getSimpleName();


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist_page;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().
                applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        mAdapter = new TaskGroupAdapter(getContext());
        mAdapter.setOnOptionsSelectListener(mOptionSelectListener);
        mTaskListRecycler.setAdapter(mAdapter);
        mTaskListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTaskListRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            RxPermissions permissions = new RxPermissions(this);
            permissions.requestEach(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    bindService();
                }
            });
        }else{
            bindService();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(isServiceConnected && mLocationService != null){
            mLocationService.updateLocation();
        }
    }

    private void bindService(){
        mServiceConnect = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AppLogger.LOGD(TAG,"onServiceConnected");
                isServiceConnected = true;
                LocationService.LocalBinder  internalClass = (LocationService.LocalBinder) service;
                mLocationService = internalClass.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                AppLogger.LOGD(TAG,"onServiceDisconnected");
                isServiceConnected = false;

            }
        };

        Intent intent = new Intent();
        intent.setClass(getContext(),LocationService.class);
        getContext().bindService(intent,mServiceConnect, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mServiceConnect != null){
            getContext().unbindService(mServiceConnect);
        }
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getSecurityProcessList();
    }

    @Override
    public User getLoginUser() {
        return super.getLoginUser();
    }


    @Override
    public void actionBarItemClick(MenuItem item) {
        super.actionBarItemClick(item);
        if(item.getItemId() == R.id.action_sync){
            AppLogger.LOGD("demo","syncData");
            updateView(false);
        }else if(item.getItemId() == R.id.action_edit){
            isMutiSelect = !isMutiSelect;
            mAdapter.setMultiiSelect(isMutiSelect);
            if(isMutiSelect){
                mAllsubmatBtn.setVisibility(View.VISIBLE);
            }else{
                mAllsubmatBtn.setVisibility(View.GONE);
            }

        }
    }



    @OnClick(R.id.submit_btn)
    public void submatAllTask(View view){
        Map<Integer,List<TaskInfo>> mSelectMap = mAdapter.getmSelectedList();
        if(mSelectMap.size() <=0){
            Toast.makeText(getContext(),"请选择要提交的任务",Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> pendingList = new ArrayList<>();
        for (List<TaskInfo> list : mSelectMap.values()){
            for (TaskInfo id : list){
                pendingList.add(id.getProcessId());
            }
        }
        List<String> geos = new ArrayList<>();
        String geo = "unknown";
        if(isServiceConnected){
            double[] location = mLocationService.getLocation();
            geo = location[0]+":"+location[1];

        }
        for (int i =0;i<pendingList.size();i++){
            geos.add(geo);
        }
        mPresenter.feedbackNormalTaskList(pendingList,geos);
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
    public void submitTaskResult(boolean success) {
        if(success){
            //Toast.makeText(getContext(),"")
        }
    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {

    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {

    }



    private TaskGroupAdapter.OptionSelectListener mOptionSelectListener = new TaskGroupAdapter.OptionSelectListener() {
        @Override
        public void onOptionsNormal(TaskInfo info) {
            List<Integer> ids = new ArrayList<>();
            ids.add(info.getProcessId());
            List<String> geos = new ArrayList<>();
            if(isServiceConnected){
                double[] location = mLocationService.getLocation();
                geos.add(location[0]+":"+location[1]);
            }else{
                geos.add("unknown");
            }
            mPresenter.feedbackNormalTaskList(ids,geos);
        }

        @Override
        public void onOptionsError(TaskInfo info) {
            Intent intent = new Intent();
            intent.setClass(getContext(), ErrorTaskEditorActivity.class);
            intent.putExtra("processId",info.getProcessId());
            double[] geos = mLocationService.getLocation();
            intent.putExtra("geo",""+geos[0]+""+geos[1]);
            startActivity(intent);
        }
    };
}
