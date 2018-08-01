package com.android.bsb.ui.task;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;
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

    @BindView(R.id.error_images)
    LinearLayout mImagesLayout;

    private int mProcessId;

    private List<String> imageList = new ArrayList<>();

    private final int REQUEST_PICKER_IMAGES = 1;

    private int MAX_IMAGES_SELECT = 3;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private int paddingSize;

    private int imageWidth,imageHeight;

    private View addView;


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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        paddingSize = getResources().getDimensionPixelSize(R.dimen.image_padding);
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
        menu.add("提交");
        menu.getItem(0).setShowAsAction(SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String errStr = mEditText.getText().toString();
        if(TextUtils.isEmpty(errStr)){
            Toast.makeText(getBaseContext(),"请填写异常说明！",Toast.LENGTH_SHORT).show();
            return false;
        }

        int rankId = mRankGroup.getCheckedRadioButtonId();
        int errorRank = 0;
        if(rankId == R.id.radio_serverity){
            errorRank = 1;
        }else if(rankId == R.id.radio_urgent){
            errorRank = 2;
        }
        for (int i = 0;i<imageList.size();i++){
            AppLogger.LOGD("demo","file:"+imageList.get(i));
        }

        mPresenter.submitErrorTask(mProcessId,imageList,errorRank,errStr,"122838:288338");


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

    }



    private void showPictureOrCameraDialog(){
        boolean haspermission = false;
        AppLogger.LOGD("","------");
        if(checkCallingPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            haspermission = false;
        }

        if(checkCallingPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            haspermission = false;
        }

        AppLogger.LOGD("demo","haspermission:"+haspermission);

        if(!haspermission && Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            return;
        }

        startPicturePicker();



    }


    private void startPicturePicker(){
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i =0 ;i<grantResults.length;i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getBaseContext(),"权限未被授予"+permissions[i],Toast.LENGTH_SHORT).show();
            }
        }

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
            delBtn.setTag(imageList.get(i));
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fp = (String) v.getTag();
                    AppLogger.LOGD("de,p","fp:"+fp);
                    imageList.remove(fp);
                    for (int i = 0 ; i< mImagesLayout.getChildCount();i++){
                        View delview = mImagesLayout.getChildAt(i);
                        if(fp.equals((String) delview.getTag())){
                            mImagesLayout.removeView(delview);
                            break;
                        }
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
            mImagesLayout.addView(view,0,params);
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


    private void saveImage(Uri uri){
        FileOutputStream fos ;
        File root = new File(getCacheDir(),"images");
        if(!root.exists() || !root.isDirectory()){
            root.mkdirs();
        }
        File tmpFile = new File(root,""+System.currentTimeMillis()+".jpg");
        try {
            if(!tmpFile.exists()){
                tmpFile.createNewFile();
            }
            fos = new FileOutputStream(tmpFile);
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,fos);
            fos.flush();
            fos.close();
            //imageList.add(tmpFile);
           // mImage1.setBackground(new BitmapDrawable(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}