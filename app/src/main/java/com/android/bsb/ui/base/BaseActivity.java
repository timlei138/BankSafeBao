package com.android.bsb.ui.base;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.widget.EmptyLayout;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity<T1 extends IBasePresent> extends RxAppCompatActivity implements IBaseView{


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

    @Override
    public void onRetry() {
        updateView(false);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }
}
