package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.util.AppLogger;

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
        holder.mErrorLayout.setVisibility(View.GONE);
        holder.mHeadLayout.setVisibility(View.GONE);
        holder.mTaskContent.setVisibility(View.VISIBLE);
        holder.mTaskContent.setText(info.getTaskName());
    }


    @Override
    public int getItemCount() {
        return mGroupInfo.getTaskList().size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.task_head)
        LinearLayout mHeadLayout;
        @BindView(R.id.error_body)
        LinearLayout mErrorLayout;
        @BindView(R.id.task_label)
        TextView mTaskContent;

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
