package com.android.bsb.ui.adapter;

import android.animation.ValueAnimator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskGroupHolder extends RecyclerView.ViewHolder{

    public TaskGroupHolder(View itemView) {
        super(itemView);
    }


    public interface ItemClickListener{

        void onExpandChildren(TaskAdapterItem item);

        void onHideChildren(TaskAdapterItem item);
    }


    public static class AddViewHolder extends TaskGroupHolder{

        @BindView(R.id.task_add)
        TextView mAddTask;

        public AddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public static class ParentViewHolder extends TaskGroupHolder{

        @BindView(R.id.group_check)
        CheckBox mCheckBox;
        @BindView(R.id.group_container)
        ConstraintLayout mContainerLayout;
        @BindView(R.id.group_label)
        TextView mGroupLabel;
        @BindView(R.id.group_count)
        TextView mGroupInfo;
        @BindView(R.id.group_desc)
        TextView mGroupDesc;
        @BindView(R.id.arrow_image)
        ImageView mArrowImage;

        private ItemClickListener mItemClickListener;
        private TaskAdapterItem mInfo;

        public ParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setItemClickListener(TaskAdapterItem info,ItemClickListener listener){
            mItemClickListener = listener;
            mInfo = info;
        }

        @OnClick(R.id.group_container)
        void onPickClick(View view){
            if(mItemClickListener!=null){
                TaskGroupInfo info = (TaskGroupInfo) mInfo.getData();
                if(info.isExpand()){
                    mItemClickListener.onHideChildren(mInfo);
                    info.setExpand(false);
                    rotationExpandIcon(90,0);
                }else{
                    mItemClickListener.onExpandChildren(mInfo);
                    info.setExpand(true);
                    rotationExpandIcon(0,90);
                }
            }
        }

        void rotationExpandIcon(float from,float to){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(from,to);
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mArrowImage.setRotation((Float) animation.getAnimatedValue());
                }
            });

            valueAnimator.start();
        }

    }

    public static class ChildViewHolder extends TaskGroupHolder{

        @BindView(R.id.task_check)
        CheckBox mCheckBox;
        @BindView(R.id.task_status)
        TextView mTaskStatus;
        @BindView(R.id.task_label)
        TextView mTaskLabel;
        @BindView(R.id.options)
        ImageView mOptions;
        @BindView(R.id.error_desc)
        TextView mErrorDesc;
        @BindView(R.id.error_imagelayout)
        LinearLayout mErrorImageLayout;
        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
