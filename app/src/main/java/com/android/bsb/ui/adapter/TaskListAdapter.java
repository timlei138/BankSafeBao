package com.android.bsb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_PARENT =  1 << 1;

    public static final int VIEW_TYPE_CHILD = 1 << 2;


    private List<TaskAdapterItem> mItemList = new ArrayList<>();

    private Map<Integer,List<TaskAdapterItem>> msubMaps = new HashMap<>();

    private Context mContext;

    private OnScrollListener mOnScrollListener;

    private boolean isMultSelected;


    public TaskListAdapter(Context context){
        mContext = context;
    }

    public void setItemList(List<TaskAdapterItem> itemList){
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public void addItem(int position,int groudId,TaskAdapterItem item){
        mItemList.add(position,item);
        notifyItemInserted(position);
    }

    public void addItem(int position,int groupId,List<TaskAdapterItem> list){
        mItemList.addAll(position,list);
        msubMaps.put(groupId,list);
        notifyItemRangeInserted(position,list.size());
    }

    public void remove(int position,int groupId,List<TaskInfo> removeList){
        mItemList.removeAll(msubMaps.get(groupId));
        notifyItemRangeRemoved(position + 1,removeList.size());
    }

    public void remove(int position,int groupId){
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
            BaseViewHolder.ChildViewHolder childViewHolder = (BaseViewHolder.ChildViewHolder) holder;
            TaskInfo data = (TaskInfo) item.getData();
            childViewHolder.mTaskLabel.setText(data.getTaskName());
            if(data.isInitTask()){
                childViewHolder.mOptions.setVisibility(View.VISIBLE);
                childViewHolder.mOptions.setTag(data);
                childViewHolder.mOptions.setOnClickListener(mOptionsClickListener);
            }else if(data.isDoneTask()){
                childViewHolder.mErrorLayout.setVisibility(View.GONE);
                childViewHolder.mOptions.setVisibility(View.GONE);
            }else if(data.isFailTask()){
                childViewHolder.mErrorLayout.setVisibility(View.VISIBLE);
                childViewHolder.mOptions.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private BaseViewHolder.ItemClickListener itemClickListener = new BaseViewHolder.ItemClickListener() {
        @Override
        public void onExpandChildren(TaskAdapterItem item) {
            TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
            int position = getPositionForGroup(groupInfo.getGroupId());
            List<TaskInfo> list = groupInfo.getTaskList();
            List<TaskAdapterItem> childList = new ArrayList<>();
            for (TaskInfo info : list){
                childList.add(TaskAdapterItem.asChild(info));

            }
            addItem(position +1 ,groupInfo.getGroupId(),childList);
            if(mOnScrollListener!=null){
                mOnScrollListener.scrollTo(position);
            }
        }

        @Override
        public void onHideChildren(TaskAdapterItem item) {
            TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
            int position = getPositionForGroup(groupInfo.getGroupId());
            remove(position,groupInfo.getGroupId(),groupInfo.getTaskList());
        }
    };


    private int getPositionForGroup(int groupId){
        for (int i = 0 ;i<mItemList.size();i++){
            Object obj = mItemList.get(i).getData();
            if(obj != null && obj instanceof TaskGroupInfo){
                TaskGroupInfo info = (TaskGroupInfo) obj;
                if(info.getGroupId() == groupId){
                    return i;
                }
            }
        }
        return -1;
    }


    private View.OnClickListener mOptionsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showOptionsWindow(v);
        }
    };


    private void showOptionsWindow(View v){
        PopupMenu menu = new PopupMenu(mContext,v);
        menu.getMenuInflater().inflate(R.menu.menu_task_options,menu.getMenu());
        menu.setOnMenuItemClickListener(onMenuItemClickListener);
        for (int i=0;i<menu.getMenu().size();i++){
            menu.getMenu().getItem(i).setActionView(v);
        }
        menu.show();
    }


    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            TaskInfo info = (TaskInfo) item.getActionView().getTag();
            if(item.getItemId() == R.id.menu_ok){
                if(mOptionsListener != null){
                    mOptionsListener.onOptionsNormal(info);
                }
            }else if(item.getItemId() == R.id.menu_error){
                if(mOptionsListener != null){
                    mOptionsListener.onOptionsError(info);
                }
            }
            return false;
        }
    };




    private OptionSelectListener mOptionsListener;

    public void setOnOptionsSelectListener(OptionSelectListener listener){
        mOptionsListener = listener;
    }

    public interface OptionSelectListener{
        void onOptionsNormal(TaskInfo info);
        void onOptionsError(TaskInfo info);
    }


    /**
     * 滚动监听接口
     */
    public interface OnScrollListener{
        void scrollTo(int pos);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.mOnScrollListener = onScrollListener;
    }





}
