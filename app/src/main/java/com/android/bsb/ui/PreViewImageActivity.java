package com.android.bsb.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.bsb.GlideApp;
import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.adapter.PreViewAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;

public class PreViewImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener, PreViewAdapter.Callback {

    @BindView(R.id.preview)
    ViewPager mViewPager;

    @BindView(R.id.page_indicator)
    LinearLayout mPageIndicator;

    private PreViewAdapter mAdapter;

    private String TAG = "PreViewImageActivity";

    private List<String> mImageList;

    private int mIndex = 0;

    private PopupWindow mOptionsWindow;

    private View optionContent;



    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_preview;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        mImageList = getIntent().getStringArrayListExtra("images");
        if (mImageList == null || mImageList.size() <= 0) {
            AppLogger.LOGD(TAG, "image list is null return!");
            finish();
        }
        mIndex = getIntent().getIntExtra("index", 0);

        AppLogger.LOGD(TAG, "index:" + mIndex + ",size:" + mImageList.size());

        mAdapter = new PreViewAdapter(this, mImageList);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setCurrentItem(mIndex);
        mViewPager.addOnPageChangeListener(this);

        createPageIndicator();

        mAdapter.setCallback(this);

    }


    private void createPageIndicator() {
        View view = null;
        LinearLayout.LayoutParams params;
        int size = getResources().getDimensionPixelSize(R.dimen.page_indicator_size);
        int padding = getResources().getDimensionPixelSize(R.dimen.page_indicator_padding);
        AppLogger.LOGD(TAG, "" + mImageList.size());
        for (int i = 0; i < mImageList.size(); i++) {
            view = new View(this);
            view.setBackgroundResource(R.drawable.page_indicator);
            view.setEnabled(i == mIndex ? true : false);
            params = new LinearLayout.LayoutParams(size, size);
            if (i != 0) {
                params.leftMargin = padding;
            }
            mPageIndicator.addView(view, params);
        }
    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mIndex != position) {
            mPageIndicator.getChildAt(mIndex).setEnabled(false);
            mIndex = position;
            mPageIndicator.getChildAt(position).setEnabled(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick() {
        finish();
    }

    @Override
    public void onOptions(int position) {
        showOptionWindow();
    }



    private void showOptionWindow() {
        if (mOptionsWindow == null) {
            mOptionsWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            optionContent = getLayoutInflater().inflate(R.layout.layout_preview_options, null);
            View saveBtn = optionContent.findViewById(R.id.save);
            saveBtn.setOnClickListener(preSaveListener);
            mOptionsWindow.setContentView(optionContent);
            mOptionsWindow.setFocusable(true);
            mOptionsWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mOptionsWindow.setOutsideTouchable(true);
            mOptionsWindow.setTouchable(true);
            mOptionsWindow.setAnimationStyle(R.style.PreviewOptionsStyle);
        }
        mOptionsWindow.showAtLocation(optionContent, Gravity.BOTTOM, 0, 0);

    }


    private View.OnClickListener preSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOptionsWindow != null && mOptionsWindow.isShowing()){
                mOptionsWindow.dismiss();
            }

            String url = mImageList.get(mIndex);

            if(!url.contains("http")){
                Toast.makeText(getContext(),"已经在本地啦，不用保存...",Toast.LENGTH_SHORT).show();
            }else{
                showProgress();
                new ThreadSaveImage().execute(url);
            }

        }
    };


    private Context getContext(){
        return this;
    }


    private class ThreadSaveImage extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            FileOutputStream fos = null;
            File file = null;
            try {
                    Bitmap bitmap = GlideApp.with(getContext()).asBitmap().load(strings[0]).
                        into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).get();
                    if(bitmap != null){
                        File root = new File(Environment.getExternalStorageDirectory(),"banksafe");
                        if(!root.exists() || !root.isDirectory()){
                            root.mkdirs();
                        }

                        file = new File(root,""+System.currentTimeMillis()+".jpg");

                        if(!file.exists()){
                            file.createNewFile();
                        }

                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                        fos.flush();
                    }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return file != null ? file.getAbsolutePath() : "";
        }

        @Override
        protected void onPostExecute(String file) {
            hideProgress();
            if(TextUtils.isEmpty(file)){
                Toast.makeText(getContext(),"图片保存失败",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"图片保存成功，保存路径："+file,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
