package com.android.bsb.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.util.AppLogger;
import com.android.bsb.widget.EmptyLayout;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity<T1 extends IBasePresent> extends RxAppCompatActivity implements IBaseView{


    private String TAG = "BaseActivity";

    public static final int DIALOG_TYPE_NOT_NETWORK = 101;
    public static final int DIALOG_TYPE_NOT_PERMISSION = 102;

    @Nullable
    @BindView(R.id.empty_layout)
    protected EmptyLayout mEmptyLayout;

    @Nullable
    @Inject
    protected T1 mPresenter;

    /**
     * 绑定布局文件
     * @return
     */
    protected abstract int attachLayoutRes();

    /**
     * Dagger 注入
     */
    protected abstract void initInjector(ApplicationComponent applicationComponent);

    /**
     * 初始化视图控件
     */
    protected abstract void initView();

    protected abstract Activity addActivityStack();


    protected abstract void updateView(boolean isRefresh);



    protected ApplicationComponent getApplicationComponent(){
        return AppApplication.getApplicationComponent();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemUI();
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);
        initInjector(AppApplication.getApplicationComponent());
        initView();
        attachWindow();
        updateView(false);
        AppApplication.getAppActivityManager().addActivity(addActivityStack());
    }

    public void setLoginUser(User user,boolean online){
        AppApplication.setLoginUser(user,online);
    }

    public User getLoginUser(){
        return AppApplication.getLoginUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.detachView();
        }
        AppApplication.getAppActivityManager().finishActivity(addActivityStack());
    }

    private void attachWindow(){
        if(mPresenter!=null){
            mPresenter.attachView(this);
        }
    }


    @Override
    public void showNetError() {
        if(mEmptyLayout!=null){
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_NO_NET);
        }
    }

    @Override
    public void showProgress() {
        AppLogger.LOGD(TAG,"showProgress->"+mEmptyLayout);
        if(mEmptyLayout!=null){
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
    }

    @Override
    public void hideProgress() {
        if(mEmptyLayout!=null){
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_HIDE);
        }
    }

    @Override
    public void finishRefresh() {

    }


    public boolean isAdmin(){
        return getLoginUser().isAdmin();
    }


    protected void addFragment(int containerViewId, Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId,fragment);
        fragmentTransaction.commit();
    }

    //ic_add fragment
    protected void addFragment(int containerViewId,Fragment fragment,String tag){
        AppLogger.LOGD(null,"addFragment->"+fragment.toString());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    //
    protected void replaceFragment(int containerViewId,Fragment fragment,String tag){
        AppLogger.LOGD(null,"replaceFragment->"+fragment.toString());
        if(getSupportFragmentManager().findFragmentByTag(tag) == null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerViewId,fragment,tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }else{
            getSupportFragmentManager().popBackStack(tag,0);
        }
    }



    @Override
    protected Dialog onCreateDialog(@DialogType int id) {

        switch (id){
            case DIALOG_TYPE_NOT_NETWORK:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_notnet_itle);
                builder.setMessage(R.string.dialog_notNet_message);
                builder.setCancelable(true);
                builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                return builder.create();
            case DIALOG_TYPE_NOT_PERMISSION:
                break;
        }

        return super.onCreateDialog(id);
    }


    public void setSystemUI(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }else{
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRetry() {
        updateView(false);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIALOG_TYPE_NOT_NETWORK,DIALOG_TYPE_NOT_PERMISSION})
    public @interface DialogType{}
}
