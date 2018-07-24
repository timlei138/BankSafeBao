package com.android.bsb.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.DeptUserListAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerUserListActivity extends BaseActivity<UserManagerPresenter> implements UserManagerView {


    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.userdempt_list)
    RecyclerView mUserListRecycler;

    private String TAG = "ManagerUserListActivity";

    private DeptUserListAdapter mAdapter;

    private boolean isShowUser;
    private boolean isSelectSecurity = false;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_deptuser;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent).build()
                .inject(this);
    }

    @Override
    protected void initView() {
        isShowUser = getIntent().getBooleanExtra("show_user",true);
        isSelectSecurity = getIntent().getBooleanExtra("publish",false);
        mToolsBar.setTitle(isShowUser ? "人员列表" : "机构列表");
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new DeptUserListAdapter(this);
        mUserListRecycler.setAdapter(mAdapter);
        mUserListRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mUserListRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected Activity addActivityStack() {
        //InputStream in = new BufferedInputStream(null,100);
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mAdapter.setShowCheckBox(isSelectSecurity);
        if(isSelectSecurity){
            mPresenter.querySecurityList();
        }else{
            mPresenter.queryAllDept();
        }

    }

    @Override
    public void addUserDeptSuccess(String reslult) {

    }

    @Override
    public void addUserDeptFaild(int code, String e) {

    }

    @Override
    public User getUserInfo() {
        return getLoginUser();
    }

    @Override
    public void showAllDeptInfo(List<User> list) {
        AppLogger.LOGD(TAG,"list.size:"+list.size());
        mAdapter.setData(list);

    }

    private DeptUserListAdapter.IonSlidingViewClickListener mSlideViewListener = new DeptUserListAdapter.IonSlidingViewClickListener() {
        @Override
        public void onItemClick(User user, int position) {

        }

        @Override
        public void onDeleteBtnCilck(User user, int position) {

        }

        @Override
        public void onUpdateBtn(User user, int position) {

        }

        @Override
        public void onDetailBtn(User user, int positon) {

        }
    };

    @OnClick(R.id.select_done)
    public void onSubmitDone(View view){
        mAdapter.getSelectedList();
        Intent intent = new Intent();
        ArrayList<Integer> list = (ArrayList<Integer>) mAdapter.getSelectedList();
        intent.putIntegerArrayListExtra("security",list);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
