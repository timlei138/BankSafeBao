package com.android.bsb.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import com.android.bsb.AppApplication;
import com.android.bsb.R;
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


    public static final int DIALOG_TYPE_ALERT = 100;
    public static final int DIALOG_TYPE_ERROR = 101;
    public static final int DIALOG_TYPE_MESSAGE = 102;

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


    protected abstract void updateView(boolean isRefresh);

    protected abstract void updateToolsBar(int title);

    protected abstract void updateToolsBar(String title);


    protected ApplicationComponent getApplicationComponent(){
        return AppApplication.getApplicationComponent();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);
        initInjector(AppApplication.getApplicationComponent());
        initView();
        updateView(false);
        attachWindow();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.detachView();
        }
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


    protected void addFragment(int containerViewId, Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId,fragment);
        fragmentTransaction.commit();
    }

    //add fragment
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
            case DIALOG_TYPE_ALERT:
                break;
            case DIALOG_TYPE_ERROR:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_error_title);
                builder.setMessage(R.string.dialog_error_message);
                builder.setCancelable(false);
                builder.create();
                break;
            case DIALOG_TYPE_MESSAGE:
                break;
        }

        return super.onCreateDialog(id);
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
    @IntDef({DIALOG_TYPE_ALERT,DIALOG_TYPE_ERROR,DIALOG_TYPE_MESSAGE})
    public @interface DialogType{}
}
