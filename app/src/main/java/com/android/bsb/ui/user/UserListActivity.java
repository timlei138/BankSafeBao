package com.android.bsb.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.DeptUserListAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UserListActivity extends BaseActivity<UserManagerPresenter>
        implements UserManagerView {

    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.userdempt_list)
    RecyclerView mUserListRecycler;
    @BindView(R.id.select_done)
    Button mSelectDoneBtn;

    private String TAG = "UserListActivity";

    private DeptUserListAdapter mAdapter;

    private boolean isPickSecurity = false;

    private List<User> mSelectedList;



    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_deptuser;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).build()
                .inject(this);
    }

    @Override
    protected void initView() {
        String title  = getIntent().getStringExtra(BaseActivity.EXTRA_TITLE);
        isPickSecurity = getIntent().getBooleanExtra(BaseActivity.EXTRA_PICK_USER,false);
        mToolsBar.setTitle(TextUtils.isEmpty(title) ? "人员列表" : title);
        if(isPickSecurity){
            mSelectedList = getIntent().getParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST);
            AppLogger.LOGD(TAG,"mSelectList:"+mSelectedList.size());
        }
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new DeptUserListAdapter(this);
        mUserListRecycler.setAdapter(mAdapter);
        mUserListRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mUserListRecycler.setLayoutManager(new LinearLayoutManager(this));
        if(isPickSecurity){
            mSelectDoneBtn.setVisibility(View.VISIBLE);
        }else{
            mSelectDoneBtn.setVisibility(View.GONE);
            mAdapter.setListener(mSlideViewListener);
        }


    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        mAdapter.setShowCheckBox(isPickSecurity);
        if(mSelectedList != null && mSelectedList.size() > 0){
            mAdapter.setSelectedList(mSelectedList);
        }
        if(isPickSecurity){
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
            showDetailWindow(user);
        }
    };


    private void showDetailWindow(final User user){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("详细信息");
        String[] labels = new String[3];
        labels[0] = "姓名："+user.getUname();
        labels[1] = "单位："+user.getDeptName()+"("+user.getRoleName()+")";
        labels[2] = "电话："+(TextUtils.isEmpty(user.getLoginName()) ? user.getCellpahone() : user.getLoginName());
        builder.setItems(labels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 2){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + (TextUtils.isEmpty(user.getLoginName()) ? user.getCellpahone() : user.getLoginName()));
                    intent.setData(data);
                    startActivity(intent);
                }
            }
        });
        builder.create().show();
    }

    @OnClick(R.id.select_done)
    public void onSubmitDone(View view){
        mAdapter.getSelectedList();
        Intent intent = new Intent();
        ArrayList<User> list = (ArrayList<User>) mAdapter.getSelectedList();
        intent.putParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST,list);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
