package com.android.bsb.ui.task;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.tasklist.TaskListPresenter;
import com.android.bsb.ui.tasklist.TaskListView;
import com.android.bsb.util.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;

public class ErrorTaskEditorActivity extends BaseActivity<TaskListPresenter> implements TaskListView {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.error_desc)
    EditText mEditText;

    @BindView(R.id.error_rank)
    RadioGroup mRankGroup;

    private int mProcessId;

    private List<String> imageList;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_edit_error;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        mProcessId = getIntent().getIntExtra("processId",0);
        if(mProcessId == 0){
            finish();
        }
        mToolbar.setTitle("异常说明");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("提交");
        menu.getItem(0).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        String errStr = mEditText.getText().toString();

        if(TextUtils.isEmpty(errStr)){
            return false;
        }

        int rankId = mRankGroup.getCheckedRadioButtonId();
        int errorRank = 0;
        if(rankId == R.id.radio_serverity){
            errorRank = 1;
        }else if(rankId == R.id.radio_urgent){
            errorRank = 2;
        }


        File file = new File(Environment.getExternalStorageDirectory(),"test.jpeg");
        AppLogger.LOGD("demo","file:"+file.exists()+",path:"+Environment.getExternalStorageDirectory().getAbsolutePath());
        List<File> files = new ArrayList<>();
        files.add(file);
        files.add(file);
        mPresenter.submitErrorTask(mProcessId,null,errorRank,errStr,"122838:288338");


        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.error_img3,R.id.error_img2,R.id.error_img1})
    public void pictureClick(View v){

    }


    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

    }
}
