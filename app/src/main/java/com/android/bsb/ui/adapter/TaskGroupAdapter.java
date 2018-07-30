package com.android.bsb.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.bsb.GlideApp;
import com.android.bsb.R;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.data.remote.NetComm;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskGroupAdapter extends RecyclerView.Adapter<TaskGroupHolder> {

    public static final int VIEW_TYPE_PARENT =  1 << 1;

    public static final int VIEW_TYPE_CHILD = 1 << 2;

    public static final int VIEW_TYPE_ADD = 1 << 3;


    private List<TaskAdapterItem> mItemList = new ArrayList<>();

    private Map<Integer,List<TaskAdapterItem>> msubMaps = new HashMap<>();

    private Context mContext;

    private OnScrollListener mOnScrollListener;

    private boolean isMultiSelected = false;

    private boolean isEditAllFlag = false;

    private ArrayList<TaskGroupInfo> mSelectedList = new ArrayList<>();

    public TaskGroupAdapter(Context context){
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

    public void setEditAll(boolean value){
        isEditAllFlag = value;
    }

    public ArrayList<TaskGroupInfo> getmSelectedList() {
        return mSelectedList;
    }


    public void setMultiiSelect(boolean value){
        isMultiSelected = value;
    }


    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getViewType();
    }

    @Override
    public TaskGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_PARENT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup,parent,false);
            return new TaskGroupHolder.ParentViewHolder(view);
        }else if(viewType == VIEW_TYPE_CHILD){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskitem,parent,false);
            return new TaskGroupHolder.ChildViewHolder(view);
        }else if(viewType == VIEW_TYPE_ADD){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_task_add,parent,false);
            return new TaskGroupHolder.AddViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup,parent,false);
            return new TaskGroupHolder.ParentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(TaskGroupHolder holder, int position) {
        TaskAdapterItem item = mItemList.get(position);
        int viewType = getItemViewType(position);
        if(viewType == VIEW_TYPE_PARENT){
            TaskGroupHolder.ParentViewHolder parentViewHolder = (TaskGroupHolder.ParentViewHolder) holder;
            TaskGroupInfo info = (TaskGroupInfo) item.getData();
            parentViewHolder.mGroupDesc.setText("说明：\n"+info.getGroupDesc());
            parentViewHolder.mGroupLabel.setText(info.getGroupName());
            parentViewHolder.mGroupInfo.setText("共 "+info.getTaskList().size()+" 项检查内容，" +
                    "由 "+info.getGroupCreator()+" 创建");

            if(info.isExpand()){
                parentViewHolder.mArrowImage.setRotation(90);
            }else{
                parentViewHolder.mArrowImage.setRotation(0);
            }
            parentViewHolder.setItemClickListener(item,itemClickListener);

            if(isMultiSelected){
                parentViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                parentViewHolder.mCheckBox.setTag(info);
                parentViewHolder.mCheckBox.setOnClickListener(partnerChangedListener);
            }else{
                parentViewHolder.mCheckBox.setVisibility(View.GONE);
            }


        }else if(viewType == VIEW_TYPE_CHILD){
            TaskGroupHolder.ChildViewHolder childViewHolder = (TaskGroupHolder.ChildViewHolder) holder;
            TaskInfo data = (TaskInfo) item.getData();
            childViewHolder.mTaskLabel.setText(data.getTaskName());
            if(data.isInitTask()){
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_green);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                childViewHolder.mTaskStatus.setCompoundDrawables(drawable,null,null,null);
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                childViewHolder.mTaskStatus.setText("正在进行中");
                childViewHolder.mOptions.setVisibility(View.VISIBLE);
                childViewHolder.mOptions.setTag(data);
                childViewHolder.mOptions.setOnClickListener(mOptionsClickListener);
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
            }else if(data.isDoneTask()){
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                childViewHolder.mTaskStatus.setText("已完成");
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mOptions.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
            }else if(data.isFailTask()){
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_error);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                childViewHolder.mTaskStatus.setCompoundDrawables(drawable,null,null,null);
                childViewHolder.mTaskStatus.setText("已完成（异常任务）");
                childViewHolder.mOptions.setVisibility(View.GONE);
                childViewHolder.mErrorImageLayout.setVisibility(View.VISIBLE);
                childViewHolder.mErrorDesc.setText(data.getErrMsg());
                if(TextUtils.isEmpty(data.getErrImages())){
                    childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                }else{
                    updateErrorImages(childViewHolder.mErrorImageLayout,data.getErrImages());
                }

            }else{
                childViewHolder.mTaskStatus.setVisibility(View.GONE);
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
                childViewHolder.mOptions.setVisibility(View.GONE);
            }
        }else if(viewType == VIEW_TYPE_ADD){
            TaskGroupHolder.AddViewHolder addViewHolder = (TaskGroupHolder.AddViewHolder) holder;
            addViewHolder.mAddTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnAddItemClicklistener != null){
                        mOnAddItemClicklistener.onAddClick();
                    }
                }
            });
        }
    }

    private void updateErrorImages(LinearLayout group, String errimgs){
        group.setVisibility(View.VISIBLE);
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
        AppLogger.LOGD("demo","error image size:"+images.size());
        for (int i =0 ;i<images.size();i++){
            ImageView view = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = width / 3;
            params.height = height / 5;
            GlideApp.with(mContext).asDrawable().load(images.get(i)).fitCenter().into(view);
            group.addView(view,params);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private TaskGroupHolder.ItemClickListener itemClickListener = new TaskGroupHolder.ItemClickListener() {
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


    CheckBox.OnClickListener partnerChangedListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            AppLogger.LOGD("demo","onClick"+v.isSelected()+",tag:"+v.getTag());
            if(v instanceof CheckBox){
                boolean ischeck = ((CheckBox)v).isChecked();
                TaskGroupInfo info = (TaskGroupInfo) v.getTag();
                if(ischeck){
                    mSelectedList.add(info);
                }else{
                    mSelectedList.remove(info);
                }
            }
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

    private OnAddItemClickListener mOnAddItemClicklistener;

    public void setOnAddClickListener(OnAddItemClickListener click){
        mOnAddItemClicklistener = click;
    }

    public interface OnAddItemClickListener{
        void onAddClick();
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
