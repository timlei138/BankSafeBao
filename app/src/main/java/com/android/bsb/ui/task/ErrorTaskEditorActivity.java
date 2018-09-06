package com.android.bsb.ui.task;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.bsb.GlideApp;
import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;

public class ErrorTaskEditorActivity extends BaseActivity<TaskPresenter> implements TaskView {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.error_desc)
    EditText mEditText;

    @BindView(R.id.error_rank)
    RadioGroup mRankGroup;

    @BindView(R.id.error_images)
    LinearLayout mImagesLayout;

    private int mProcessId;

    private ArrayList<String> imageList = new ArrayList<>();

    private final int REQUEST_PICKER_IMAGES = 1;

    private int MAX_IMAGES_SELECT = 3;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private int paddingSize;

    private int imageWidth,imageHeight;

    private View addView;

    private String geoStr;

    private int errorRank = 0;

    private String errorDesc;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_edit_error;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        mProcessId = getIntent().getIntExtra("processId",0);
        if(mProcessId == 0){
            finish();
        }
        geoStr = getIntent().getStringExtra("geo");
        mToolbar.setTitle("异常说明");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        paddingSize = getResources().getDimensionPixelSize(R.dimen.image_padding_left);
        imageHeight = screenHeight / 3;
        imageWidth = screenWidth / 3 - 2 * paddingSize;

    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        addOrModifyAddView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return false;
        }
        String errStr = mEditText.getText().toString();
        if(TextUtils.isEmpty(errStr)){
            Toast.makeText(getBaseContext(),"请填写异常说明！",Toast.LENGTH_SHORT).show();
            return false;
        }
        errorDesc = errStr;
        int rankId = mRankGroup.getCheckedRadioButtonId();

        if(rankId == R.id.radio_serverity){
            errorRank = 1;
        }else if(rankId == R.id.radio_urgent){
            errorRank = 2;
        }
        mPresenter.feedbackErrorTaskResult(mProcessId,imageList,errorRank,errStr,geoStr);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

    }

    @Override
    public void submitTaskResult(boolean success, Map<Integer,String> normal, TaskInfo error) {
        if(success){
            Toast.makeText(getBaseContext(),"任务提交成功",Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("images",imageList);
            resultIntent.putExtra("rank",errorRank);
            resultIntent.putExtra("msg",errorDesc);
            setResult(RESULT_OK,resultIntent);
            finish();
        }else{
            Toast.makeText(getBaseContext(),"任务提交失败，请稍后再试！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {

    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {
        Toast.makeText(getBaseContext(),"code:"+code+"msg:"+msg,Toast.LENGTH_SHORT).show();
    }

    private void startPicturePicker(){
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            File root = new File(getCacheDir(),"images");
                            if(!root.exists() && !root.isDirectory()){
                                root.mkdirs();
                            }
                            Matisse.from(ErrorTaskEditorActivity.this)
                                    .choose(MimeType.ofImage(), false)
                                    .countable(true)
                                    .capture(true)
                                    .captureStrategy(new CaptureStrategy(false,root,"com.android.bsb.fileprovider"))
                                    .maxSelectable(MAX_IMAGES_SELECT - imageList.size())
                                    //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(screenWidth / 3)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new GlideEngine())
                                    .originalEnable(true)
                                    .maxOriginalSize(10)
                                    .forResult(REQUEST_PICKER_IMAGES);
                        }else {
                            Toast.makeText(getBaseContext(),"缺少相机及读写存储权限，请进入设置打开",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        AppLogger.LOGD("demo","requestCode:"+requestCode+",resultCode:"+resultCode);
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(getBaseContext(),"未获取到图片",Toast.LENGTH_SHORT).show();
            return;
        }
        if(requestCode == REQUEST_PICKER_IMAGES){
            List<String> files = Matisse.obtainPathResult(data);
            List<String> updatelist = new ArrayList<>();
            for(String path : files){
                if(imageList.contains(path)){
                    continue;
                }else{
                    imageList.add(path);
                    updatelist.add(path);
                }
            }
            if(!updatelist.isEmpty()){
                updateErrorImages(updatelist);
            }

        }
    }


    private void updateErrorImages(List<String> updateList){
        LayoutInflater inflater = getLayoutInflater();
        View view = null;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth,imageHeight);
        params.leftMargin = paddingSize ;
        for (int i = 0; i<updateList.size();i++){
            view = inflater.inflate(R.layout.layout_upload_images,null);
            ImageView image = view.findViewById(R.id.image);
            final ImageView delBtn = view.findViewById(R.id.image_del);
            GlideApp.with(this).asDrawable().load(updateList.get(i)).fitCenter().into(image);
            delBtn.setTag(view);
            view.setTag(updateList.get(i));
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = (View) v.getTag();
                    if(view!=null){
                        mImagesLayout.removeView(view);
                        imageList.remove((String) view.getTag());
                    }
                    addOrModifyAddView();
                }
            });
            view.setTag(updateList.get(i));
            if(i==0){
                params.rightMargin = paddingSize / 2;
            }else if(i == 1){
                params.leftMargin = params.rightMargin = paddingSize / 2;
            }else{
                params.leftMargin = params.rightMargin = paddingSize /2;
            }
            mImagesLayout.addView(view,i,params);
        }

        addOrModifyAddView();



    }

    private void addOrModifyAddView(){
        if(imageList.size() < MAX_IMAGES_SELECT && addView == null){
            addView = getLayoutInflater().inflate(R.layout.layout_upload_images,null);
            ImageView image = addView.findViewById(R.id.image);
            ImageView delBtn = addView.findViewById(R.id.image_del);
            delBtn.setVisibility(View.GONE);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPicturePicker();
                }
            });
            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams(imageWidth,imageHeight);
            params.leftMargin = paddingSize /2;
            mImagesLayout.addView(addView,params);
        }else if(imageList.size() == MAX_IMAGES_SELECT && addView != null){
            mImagesLayout.removeView(addView);
            addView = null;
        }else{

        }
    }
}
