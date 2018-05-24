package com.android.bsb.ui.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

public interface IBaseView {

    /**
     * 显示网络错误
     */

    void showNetError();

    /**
     * 显示进度条
     */
    void showProgress();

    /**
     * 隐藏进度条
     */
    void hideProgress();

    /**
     * 刷新成功
     */

    void finishRefresh();

    /**
     * 重试
     */
    void onRetry();

    /**
     * 绑定生命周期
     * @param <T>
     * @return
     */
    <T>LifecycleTransformer<T> bindToLife();
}
