package com.android.bsb.ui.adapter;

import android.content.Context;
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
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskInfoAdapter extends RecyclerView.Adapter<TaskInfoAdapter.TaskViewHolder> {

    private String TAG = "TaskInfoAdapter";

    private Context mContext;

    private TaskGroupInfo mGroupInfo;

    public TaskInfoAdapter(Context context){
        mContext = context;
        mGroupInfo = new TaskGroupInfo();
    }

    public void setList(TaskGroupInfo info){
        mGroupInfo = info;
        notifyDataSetChanged();
    }

    public void addTask(TaskInfo info){
        mGroupInfo.getTaskList().add(info);
        int positin = mGroupInfo.getTaskList().size() -1;
        if(positin <0){
            positin = 0;
        }
        AppLogger.LOGD(TAG,"position:"+positin);
        notifyItemInserted(positin);
    }

    public void removeTask(int position){
        mGroupInfo.getTaskList().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new TaskViewHolder(inflater.inflate(R.layout.layout_taskitem,parent,false));
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        TaskInfo info = mGroupInfo.getTaskList().get(position);
        holder.mErrorDesc.setVisibility(View.GONE);
        holder.mTaskStatus.setVisibility(View.GONE);
        holder.mCheckBox.setVisibility(View.GONE);
        holder.mOptions.setVisibility(View.GONE);
        holder.mTaskLabel.setText(info.getTaskName());
        AppLogger.LOGD(TAG,"taskInfo->"+info.toString());

        if(!TextUtils.isEmpty(info.getErrMsg())){
            holder.mErrorDesc.setText(info.getErrMsg());
        }else{
            holder.mErrorDesc.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(info.getErrImages())){
            holder.mErrorImageLayout.setVisibility(View.VISIBLE);
            updateErrorImages(holder.mErrorImageLayout,info.getErrImages());
        }else{
            holder.mErrorImageLayout.setVisibility(View.GONE);
        }


    }

    private void updateErrorImages(LinearLayout group,String errimgs){
        int indexspile = errimgs.indexOf(",");
        List<String> images = new ArrayList<>();
        if(indexspile == -1){
            images.add(NetComm.getImageHost()+indexspile);
        }else{
            String[] imgs = errimgs.split(",");
            for (String url : imgs){
                images.add(NetComm.getImageHost()+url);
            }
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        AppLogger.LOGD(TAG,"error image size:"+images.size());

        for (int i =0 ;i<images.size();i++){
            View view = new View(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = width / 3;
            params.height = height / 5;
            group.addView(view,params);
        }

    }


    @Override
    public int getItemCount() {
        return mGroupInfo.getTaskList().size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{

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

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
