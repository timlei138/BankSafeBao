package com.android.bsb.widget;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.util.AppLogger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.jar.Attributes;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmptyLayout extends FrameLayout{

    public static final int STATUS_HIDE = 1001;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_NO_NET = 2;
    public static final int STATUS_NO_DATA = 3;

    @BindView(R.id.tv_net_error)
    TextView mTvEmptyTextView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_layout)
    FrameLayout mEmptyLayout;

    private Context mContext;

    private int emptyBackgroundColor;

    private int mStatus = STATUS_HIDE;


    private onRetryListener mRetryListener;



    public EmptyLayout(@NonNull Context context) {
        this(context,null);
    }

    public EmptyLayout(Context context, AttributeSet arrts){
        super(context,arrts);
        mContext = context;
        init(arrts);
    }

    private void init(AttributeSet attrs){
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.EmptyLayout);
        emptyBackgroundColor = a.getColor(R.styleable.EmptyLayout_empty_background, Color.TRANSPARENT);
        a.recycle();

        View.inflate(mContext,R.layout.layout_empty_loading,this);

        ButterKnife.bind(this);
        mEmptyLayout.setBackgroundColor(emptyBackgroundColor);
        switchEmptyView();

    }


    public void setEmptyStatus(@EmptyStatus int status){
        mStatus = status;
        switchEmptyView();
    }


    public int getEmptyStatus(){
        return mStatus;
    }


    private void switchEmptyView(){
        AppLogger.LOGD("EmptyLayout","switchEmptyView:"+mStatus);
        switch (mStatus){
            case STATUS_HIDE:
                setVisibility(GONE);
                break;
            case STATUS_LOADING:
                setVisibility(VISIBLE);
                mTvEmptyTextView.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                break;
            case STATUS_NO_DATA:
            case STATUS_NO_NET:
                setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                mTvEmptyTextView.setVisibility(VISIBLE);
                break;
        }
    }


    /**
     * 重试
     */

    public interface onRetryListener{
        void onRetry();
    }

    public void setRetryListener(onRetryListener listener){
        mRetryListener = listener;
    }


    @OnClick(R.id.tv_net_error)
    public void onClick(){
        if(mRetryListener!=null){
            mRetryListener.onRetry();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_HIDE,STATUS_LOADING,STATUS_NO_DATA,STATUS_NO_NET})
    public @interface EmptyStatus{}

}
