package com.android.bsb.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.widget.EmptyLayout;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseFragment<T1 extends IBasePresent> extends RxFragment implements IBaseView{

    @Nullable
    @BindView(R.id.empty_layout)
    protected EmptyLayout mEmptyLayout;

    @Nullable
    @Inject
    protected T1 mPresenter;


    private View mRootView;
    private Context mContext;

    private boolean isMulti;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mRootView == null){
            mRootView = inflater.inflate(attachLayoutRes(),null);
            ButterKnife.bind(this,mRootView);
            initInjector(AppApplication.getApplicationComponent());
            initView();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if(parent!=null){
            parent.removeView(mRootView);
        }

        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getUserVisibleHint() && mRootView!=null && !isMulti){
            isMulti = true;
            updateView(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser && isVisible() && mRootView!=null && !isMulti){
            isMulti = true;
            updateView(false);
        }else
            super.setUserVisibleHint(isVisibleToUser);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void updateToolsBar(String title){
        ((BaseActivity)getActivity()).updateToolsBar(title);
    }

    @Override
    public void showNetError() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void finishRefresh() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return null;
    }

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
}