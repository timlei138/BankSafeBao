package com.android.bsb.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bsb.GlideApp;
import com.android.bsb.R;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentTaskAdapter extends RecyclerView.Adapter<RecentTaskAdapter.RecentHolder>{

    private String TAG = getClass().getSimpleName();

    private Context mContext;

    private LayoutInflater inflater;

    private List<RecentItem> mItemList = new ArrayList<>();



    public RecentTaskAdapter(Context context){
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setList(List<RecentItem> itemList){
        AppLogger.LOGD(TAG,"setList size:"+itemList.size());
        mItemList.clear();
        mItemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getType();
    }

    @NonNull
    @Override
    public RecentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentHolder holder;
        AppLogger.LOGD(TAG,"viewType:"+viewType);
        if(viewType == RecentItem.TYPE_STAMP){
            View timeStamp = inflater.inflate(R.layout.layout_timestap,parent,false);
            holder = new TimeStampHolder(timeStamp);
        }else if(viewType == RecentItem.TYPE_TASK){
            View taskView = inflater.inflate(R.layout.layout_taskitem,parent,false);
            holder = new TaskItemHolder(taskView);
        }else{
            View timeStamp = inflater.inflate(R.layout.layout_timestap,parent,false);
            holder = new TimeStampHolder(timeStamp);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentHolder holder, int position) {

        RecentItem item = mItemList.get(position);

        if(item.getType() == RecentItem.TYPE_STAMP){
            TimeStampHolder stampHolder = (TimeStampHolder) holder;
            stampHolder.setTimeStamp(item.getTimeStamp());

        }else if(item.getType() == RecentItem.TYPE_TASK){
            TaskItemHolder taskHolder = (TaskItemHolder) holder;
            taskHolder.mOptions.setVisibility(View.GONE);
            TaskInfo task = item.getRecentTask();
            taskHolder.mTaskLabel.setText(task.getTaskName());
            if(task.isDoneTask()){
                taskHolder.mTaskStatus.setVisibility(View.VISIBLE);
                taskHolder.mTaskStatus.setText("已完成");
                taskHolder.mErrorImageLayout.setVisibility(View.GONE);
                taskHolder.mOptions.setVisibility(View.GONE);
                taskHolder.mErrorDesc.setVisibility(View.GONE);
            }else if(task.isInitTask()){
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_green);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                taskHolder.mTaskStatus.setCompoundDrawables(drawable, null, null, null);
                taskHolder.mTaskStatus.setVisibility(View.VISIBLE);
                taskHolder.mTaskStatus.setText("正在进行中");
                taskHolder.mErrorImageLayout.setVisibility(View.GONE);
                taskHolder.mErrorDesc.setVisibility(View.GONE);
            }else if(task.isFailTask()){
                taskHolder.mTaskStatus.setVisibility(View.VISIBLE);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_error);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                taskHolder.mTaskStatus.setCompoundDrawables(drawable, null, null, null);
                taskHolder.mTaskStatus.setText("已完成（异常任务）");
                taskHolder.mOptions.setVisibility(View.GONE);
                taskHolder.mErrorImageLayout.setVisibility(View.VISIBLE);
                taskHolder.mErrorDesc.setText(task.getErrMsg());
                if (TextUtils.isEmpty(task.getErrImages())) {
                    taskHolder.mErrorImageLayout.setVisibility(View.GONE);
                } else {
                    updateErrorImages(taskHolder.mErrorImageLayout, task.getErrImages());
                }

            }

        }
    }

    private void updateErrorImages(LinearLayout group, String errimgs) {
        group.setVisibility(View.VISIBLE);
        int indexspile = errimgs.indexOf(",");
        List<String> images = new ArrayList<>();
        if (indexspile == -1) {
            images.add(NetComm.getImageHost() + indexspile);
        } else {
            String[] imgs = errimgs.split(",");
            for (String url : imgs) {
                images.add(NetComm.getImageHost() + url);
            }
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        AppLogger.LOGD("demo", "error image size:" + images.size());
        for (int i = 0; i < images.size(); i++) {
            ImageView view = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = width / 3;
            params.height = height / 5;
            GlideApp.with(mContext).asDrawable().load(images.get(i)).fitCenter().into(view);
            group.addView(view, params);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }



    public class RecentHolder extends RecyclerView.ViewHolder{

        public RecentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public class TimeStampHolder extends RecentHolder{

        @BindView(R.id.timestamp)
        TextView mTimeStamp;

        public TimeStampHolder(View itemView) {
            super(itemView);
        }

        public void setTimeStamp(String timeStamp){
            String[] time = timeStamp.split(":");
            mTimeStamp.setText(time[0]+"年"+time[1]+"月"+time[2]+"日");
        }
    }

    public class TaskItemHolder extends RecentHolder{

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
        public TaskItemHolder(View itemView) {
            super(itemView);
        }


    }

}
