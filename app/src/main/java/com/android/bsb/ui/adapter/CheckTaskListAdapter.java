package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckTaskListAdapter extends RecyclerView.Adapter<CheckTaskListAdapter.CheckTaskListViewHolder>{

    private Context mContext;


    private List<CheckTaskInfo> mList = new ArrayList<>();

    private boolean isRecentTask = false;

    public CheckTaskListAdapter(Context context,boolean isRecentTask){
        mContext = context;
        this.isRecentTask = isRecentTask;
    }


    public void setList(List<CheckTaskInfo> list){
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public CheckTaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_user_item,null);
        UserHolder holder = new UserHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckTaskListViewHolder holder, int position) {

        if(holder instanceof  UserHolder){
            CheckTaskInfo info = mList.get(position);
            ((UserHolder) holder).mDeptName.setText(info.getSecurityName());
            ((UserHolder) holder).mUserName.setText(info.getDeptName());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CheckTaskListViewHolder extends RecyclerView.ViewHolder{
        public CheckTaskListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }



    public class UserHolder extends CheckTaskListViewHolder{

        @BindView(R.id.tv_username)
        TextView mUserName;
        @BindView(R.id.tv_deptname)
        TextView mDeptName;

        public UserHolder(View itemView) {
            super(itemView);
        }

    }


    public class RecentTaskHolder extends CheckTaskListViewHolder{

        public RecentTaskHolder(View itemView) {
            super(itemView);
        }
    }




}






