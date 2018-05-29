package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_PARENT =  1 >> 1;

    public static final int VIEW_TYPE_CHILD = 1 >> 2;


    private List<TaskAdapterItem> mItemList = new ArrayList<>();

    private Context mContext;


    public TaskListAdapter(Context context){
        mContext = context;
    }

    public void setItemList(List<TaskAdapterItem> itemList){
        mItemList = itemList;
    }

    public void addItem(int position,TaskAdapterItem item){
        mItemList.add(position,item);
        notifyItemInserted(position);
    }

    public void addItem(int position,List<TaskAdapterItem> list){
        mItemList.addAll(list);
        notifyItemRangeInserted(position,list.size());
    }

    public void remove(int position){
        mItemList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getViewType();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_PARENT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup,parent,false);
            return new BaseViewHolder.ParentViewHolder(view);
        }else if(viewType == VIEW_TYPE_CHILD){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskitem,parent,false);
            return new BaseViewHolder.ChildViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup,parent,false);
            return new BaseViewHolder.ParentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        TaskAdapterItem item = mItemList.get(position);
        int viewType = getItemViewType(position);
        if(viewType == VIEW_TYPE_PARENT){
            BaseViewHolder.ParentViewHolder parentViewHolder = (BaseViewHolder.ParentViewHolder) holder;
            TaskGroupInfo info = (TaskGroupInfo) item.getData();
            parentViewHolder.mGroupDesc.setText(""+info.getGroupDesc());
            parentViewHolder.mGroupLabel.setText(info.getGroupName());
            parentViewHolder.mGroupInfo.setText(""+info.getGroupDegree());

            if(info.isExpand()){
                parentViewHolder.mArrowImage.setRotation(90);
            }else{
                parentViewHolder.mArrowImage.setRotation(0);
            }

            parentViewHolder.setItemClickListener(item,itemClickListener);



        }else if(viewType == VIEW_TYPE_CHILD){

        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private BaseViewHolder.ItemClickListener itemClickListener = new BaseViewHolder.ItemClickListener() {
        @Override
        public void onExpandChildren(TaskAdapterItem item) {
            int position = item.getPosition();
            TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
            List<TaskInfo> list = groupInfo.getTaskList();
            List<TaskAdapterItem> childList = new ArrayList<>();
            for (TaskInfo info : list){
                childList.add(TaskAdapterItem.asChild(0,info));

            }
            addItem(position,childList);
        }

        @Override
        public void onHideChildren(TaskAdapterItem item) {

        }
    };
}
