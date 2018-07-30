package com.android.bsb.ui.task;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;

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

    @BindView(R.id.error_img1)
    AppCompatImageView mImage1;

    private int mProcessId;

    private List<File> imageList = new ArrayList<>();

    private final int PHOTO_REQUEST_CAREMA = 1;
    private final int PHOTO_REQUEST_GALLERY = 2;
    private final int PHOTO_REQUEST_CUT = 3;
    private final int PHOTO_CAMERA_PERMISSION = 4;
    private File tempFile;


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
        for (int i = 0;i<imageList.size();i++){
            AppLogger.LOGD("demo","file:"+imageList.get(i).getAbsolutePath());
        }

        mPresenter.submitErrorTask(mProcessId,imageList,errorRank,errStr,"122838:288338");


        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.error_img3,R.id.error_img2,R.id.error_img1})
    public void pictureClick(View v){
        showPictureOrCameraDialog();
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
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},PHOTO_CAMERA_PERMISSION);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = new String[]{"拍照","从图册选择"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLogger.LOGD("demo","which->"+which);
                switch (which){
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        pickPhoto();
                        break;
                }
            }
        });
        builder.create().show();


    }


    private void takePhoto(){

        tempFile = new File(Environment.getExternalStorageDirectory(),
                ""+System.currentTimeMillis()+".jpg");
        if(!tempFile.exists()){
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = Uri.fromFile(tempFile);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, PHOTO_REQUEST_CAREMA);

        }
    }

    private void pickPhoto(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_REQUEST_GALLERY);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        //将URI指向相应的file:///…
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 不返回图片文件
        intent.putExtra("return-data", false);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
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
        if(requestCode == PHOTO_REQUEST_CAREMA){
            if(data!=null && data.getData() != null){
                saveImage(data.getData());
            }else if(tempFile != null){
                saveImage(Uri.fromFile(tempFile));
            }

        }else if(requestCode == PHOTO_REQUEST_GALLERY){
            //startPhotoZoom(data.getData());
            saveImage(data.getData());
        }else if(requestCode == PHOTO_REQUEST_CUT){
            try {
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

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
            imageList.add(tmpFile);
            mImage1.setBackground(new BitmapDrawable(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
