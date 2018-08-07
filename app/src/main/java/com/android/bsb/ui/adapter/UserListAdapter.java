package com.android.bsb.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends BaseAdapter {


    private Context mContext;

    private List<CheckTaskInfo> mList = new ArrayList<>();

    private LayoutInflater inflater;


    public UserListAdapter(Context context){
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setList(List<CheckTaskInfo> list){
        mList = list;
        notifyDataSetChanged();
    }


    public List<TaskInfo> getExecTaskList(int position){
        return mList.get(position).getExecList();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_user_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CheckTaskInfo info = mList.get(position);
        holder.tvUname.setText(info.getSecurityName());
        holder.tvUdept.setText(info.getDeptName());
        return convertView;
    }


    public class ViewHolder{
        TextView tvUname;
        TextView tvUdept;
        public ViewHolder(View itemView){
            tvUname = itemView.findViewById(R.id.tv_username);
            tvUdept = itemView.findViewById(R.id.tv_deptname);
        }

    }

}
