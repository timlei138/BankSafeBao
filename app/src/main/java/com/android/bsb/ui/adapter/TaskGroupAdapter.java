package com.android.bsb.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
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

    public static final int VIEW_TYPE_PARENT = 1 << 1;

    public static final int VIEW_TYPE_CHILD = 1 << 2;

    public static final int VIEW_TYPE_ADD = 1 << 3;


    private List<TaskAdapterItem> mItemList = new ArrayList<>();

    private Map<Integer, List<TaskAdapterItem>> msubMaps = new HashMap<>();

    private Context mContext;

    private OnScrollListener mOnScrollListener;

    private boolean isMultiSelected = false;

    private Map<Integer, List<TaskInfo>> mSelectedList = new HashMap<>();


    CheckBox.OnClickListener checkItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            AppLogger.LOGD("demo", "onClick " + v.isSelected() + ",tag:" + v.getTag());
            TaskAdapterItem item = (TaskAdapterItem) v.getTag();
            if (item.getViewType() == VIEW_TYPE_PARENT) {
                TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
                if (mSelectedList.containsKey(groupInfo.getGroupId())) {
                    mSelectedList.remove(groupInfo.getGroupId());
                } else {
                    List<TaskInfo> unOptionList = new ArrayList<>();
                    for (TaskInfo info : groupInfo.getTaskList()){
                        if(!(info.isDoneTask() || info.isFailTask())){
                            unOptionList.add(info);
                        }
                    }
                    mSelectedList.put(groupInfo.getGroupId(),unOptionList);
                }
                notifyDataSetChanged();
            } else if (item.getViewType() == VIEW_TYPE_CHILD) {
                TaskInfo childInfo = (TaskInfo) item.getData();
                for (TaskAdapterItem tmp : mItemList) {
                    if (tmp.getViewType() == VIEW_TYPE_PARENT) {
                        TaskGroupInfo parent = (TaskGroupInfo) tmp.getData();
                        if (parent.getGroupId() == childInfo.getTaskGroupId()) {
                            List childList = mSelectedList.get(parent.getGroupId());
                            if (childList != null && childList.contains(childInfo)) {
                                mSelectedList.get(parent.getGroupId()).remove(childInfo);
                                if(mSelectedList.get(parent.getGroupId()).size() == 0){
                                    mSelectedList.remove(parent.getGroupId());
                                }
                            } else if(childList == null) {
                                List<TaskInfo> list = new ArrayList<>();
                                list.add(childInfo);
                                mSelectedList.put(parent.getGroupId(), list);
                            }else{
                                childList.add(childInfo);
                                mSelectedList.put(parent.getGroupId(),childList);
                            }
                        }

                    }
                }
            }
            notifyDataSetChanged();
        }
    };
    private TaskGroupHolder.ItemClickListener itemClickListener = new TaskGroupHolder.ItemClickListener() {
        @Override
        public void onExpandChildren(TaskAdapterItem item) {
            TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
            int position = getPositionForGroup(groupInfo.getGroupId());

            List<TaskAdapterItem> childList;
            childList = msubMaps.get(groupInfo.getGroupId());

            if(childList == null || childList.isEmpty()){
                childList = new ArrayList<>();
                List<TaskInfo> list = groupInfo.getTaskList();
                for (TaskInfo info : list) {
                    childList.add(TaskAdapterItem.asChild(info));
                }
            }
            addItem(position + 1, groupInfo.getGroupId(), childList);
            if (mOnScrollListener != null) {
                mOnScrollListener.scrollTo(position);
            }
        }

        @Override
        public void onHideChildren(TaskAdapterItem item) {
            TaskGroupInfo groupInfo = (TaskGroupInfo) item.getData();
            int position = getPositionForGroup(groupInfo.getGroupId());
            remove(position, groupInfo.getGroupId(), groupInfo.getTaskList());
        }
    };
    private OptionSelectListener mOptionsListener;
    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            TaskInfo info = (TaskInfo) item.getActionView().getTag();
            if (item.getItemId() == R.id.menu_ok) {
                if (mOptionsListener != null) {
                    mOptionsListener.onOptionsNormal(info);
                }
            } else if (item.getItemId() == R.id.menu_error) {
                if (mOptionsListener != null) {
                    mOptionsListener.onOptionsError(info);
                }
            }
            return false;
        }
    };
    private View.OnClickListener mOptionsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showOptionsWindow(v);
        }
    };
    private OnAddItemClickListener mOnAddItemClicklistener;


    public TaskGroupAdapter(Context context) {
        mContext = context;
    }

    public void setItemList(List<TaskAdapterItem> itemList) {
        mItemList = itemList;
        msubMaps.clear();
        notifyDataSetChanged();
    }

    public void addItem(int position, int groudId, TaskAdapterItem item) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }


    public void updateItemsResult(List<Integer> ids,List<String> imgs){
        int position = 0;
        if(imgs != null){
            int processId = ids.get(0);
            int i =0;
            for (TaskAdapterItem item : mItemList){
                if(item.getViewType() == VIEW_TYPE_CHILD){
                    TaskInfo data = (TaskInfo) item.getData();
                    if(data.getProcessId() == processId){
                        //data.setResult();
                        break;
                    }
                }
                i++;
            }
            notifyItemChanged(position);
        }

        if(ids != null && imgs == null){
            for (Integer processId : ids){
                int j = 0;
                for (TaskAdapterItem item : mItemList){
                    if(item.getViewType() == VIEW_TYPE_CHILD){
                        TaskInfo child = (TaskInfo) item.getData();
                        if(child.getProcessId() == processId){
                            position = j;
                            break;
                        }
                    }
                    j++;
                }
                notifyItemChanged(position);
            }
        }

    }

    public void addItem(int position, int groupId, List<TaskAdapterItem> list) {
        mItemList.addAll(position, list);
        msubMaps.put(groupId, list);
        notifyItemRangeInserted(position, list.size());
    }

    public void remove(int position, int groupId, List<TaskInfo> removeList) {
        mItemList.removeAll(msubMaps.get(groupId));
        notifyItemRangeRemoved(position + 1, removeList.size());
    }

    public void remove(int position, int groupId) {
        mItemList.remove(position);
        notifyItemRemoved(position);
    }

    public Map<Integer, List<TaskInfo>> getmSelectedList() {
        return mSelectedList;
    }

    public void setSelectList(Map<Integer,List<TaskInfo>> list){
        if(list != null){
            mSelectedList = list;
            notifyDataSetChanged();
        }
    }

    public void setMultiiSelect(boolean value) {
        isMultiSelected = value;
        notifyDataSetChanged();
        if(!isMultiSelected){
            mSelectedList.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getViewType();
    }

    @Override
    public TaskGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PARENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup, parent, false);
            return new TaskGroupHolder.ParentViewHolder(view);
        } else if (viewType == VIEW_TYPE_CHILD) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskitem, parent, false);
            return new TaskGroupHolder.ChildViewHolder(view);
        } else if (viewType == VIEW_TYPE_ADD) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_task_add, parent, false);
            return new TaskGroupHolder.AddViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_taskgroup, parent, false);
            return new TaskGroupHolder.ParentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(TaskGroupHolder holder, int position) {
        TaskAdapterItem item = mItemList.get(position);
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_PARENT) {
            TaskGroupHolder.ParentViewHolder parentViewHolder = (TaskGroupHolder.ParentViewHolder) holder;
            TaskGroupInfo info = (TaskGroupInfo) item.getData();
            parentViewHolder.mGroupDesc.setText("说明：\n" + info.getGroupDesc());
            parentViewHolder.mGroupLabel.setText(info.getGroupName());
            parentViewHolder.mGroupInfo.setText("共 " + info.getTaskList().size() + " 项检查内容");

            if (info.isExpand()) {
                parentViewHolder.mArrowImage.setRotation(90);
            } else {
                parentViewHolder.mArrowImage.setRotation(0);
            }
            parentViewHolder.setItemClickListener(item, itemClickListener);

            if (isMultiSelected) {
                parentViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                parentViewHolder.mCheckBox.setTag(item);
                parentViewHolder.mCheckBox.setOnClickListener(checkItemClickListener);

                if(mSelectedList.containsKey(info.getGroupId())){
                    parentViewHolder.mCheckBox.setChecked(true);
                }else{
                    parentViewHolder.mCheckBox.setChecked(false);
                }

            } else {
                parentViewHolder.mCheckBox.setVisibility(View.GONE);
            }


        } else if (viewType == VIEW_TYPE_CHILD) {
            TaskGroupHolder.ChildViewHolder childViewHolder = (TaskGroupHolder.ChildViewHolder) holder;
            TaskInfo data = (TaskInfo) item.getData();
            childViewHolder.mTaskLabel.setText(data.getTaskName());
            if (data.isInitTask()) {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_green);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                childViewHolder.mTaskStatus.setCompoundDrawables(drawable, null, null, null);
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                childViewHolder.mTaskStatus.setText("正在进行中");
                childViewHolder.mOptions.setVisibility(View.VISIBLE);
                childViewHolder.mOptions.setTag(data);
                childViewHolder.mOptions.setOnClickListener(mOptionsClickListener);
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
                childViewHolder.mLocationInfo.setVisibility(View.GONE);
            } else if (data.isDoneTask()) {
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                childViewHolder.mTaskStatus.setText("已完成");
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mOptions.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
                childViewHolder.mLocationInfo.setVisibility(View.VISIBLE);
                childViewHolder.mLocationInfo.setText(data.getGeographicInfo());
            } else if (data.isFailTask()) {
                childViewHolder.mTaskStatus.setVisibility(View.VISIBLE);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.status_error);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                childViewHolder.mTaskStatus.setCompoundDrawables(drawable, null, null, null);
                childViewHolder.mTaskStatus.setText("已完成（异常任务）");
                childViewHolder.mOptions.setVisibility(View.GONE);
                childViewHolder.mErrorImageLayout.setVisibility(View.VISIBLE);
                childViewHolder.mErrorDesc.setVisibility(View.VISIBLE);
                childViewHolder.mErrorDesc.setText("异常说明:\n"+data.getErrMsg());
                childViewHolder.mLocationInfo.setVisibility(View.VISIBLE);
                childViewHolder.mLocationInfo.setText(data.getGeographicInfo());
                if (TextUtils.isEmpty(data.getErrImages())) {
                    childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                } else {
                    updateErrorImages(childViewHolder.mErrorImageLayout, data.getErrImages());
                }

            } else {
                childViewHolder.mTaskStatus.setVisibility(View.GONE);
                childViewHolder.mErrorImageLayout.setVisibility(View.GONE);
                childViewHolder.mErrorDesc.setVisibility(View.GONE);
                childViewHolder.mOptions.setVisibility(View.GONE);
                childViewHolder.mLocationInfo.setVisibility(View.GONE);
            }

            if (isMultiSelected && !(data.isDoneTask() || data.isFailTask())) {
                childViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                childViewHolder.mCheckBox.setTag(item);
                childViewHolder.mCheckBox.setOnClickListener(checkItemClickListener);
                if(mSelectedList.get(data.getTaskGroupId()) != null
                        && mSelectedList.get(data.getTaskGroupId()).contains(data)){
                    childViewHolder.mCheckBox.setChecked(true);

                }else{
                    childViewHolder.mCheckBox.setChecked(false);
                }

            } else {
                childViewHolder.mCheckBox.setVisibility(View.GONE);
            }

        } else if (viewType == VIEW_TYPE_ADD) {
            TaskGroupHolder.AddViewHolder addViewHolder = (TaskGroupHolder.AddViewHolder) holder;
            addViewHolder.mAddTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnAddItemClicklistener != null) {
                        mOnAddItemClicklistener.onAddClick();
                    }
                }
            });
        }
    }

    private void updateErrorImages(LinearLayout group, String errimgs) {
        group.setVisibility(View.VISIBLE);
        group.removeAllViews();
        int indexspile = errimgs.indexOf(",");
        List<String> images = new ArrayList<>();
        if (indexspile == -1) {
            images.add(NetComm.getImageHost() + errimgs);
        } else {
            String[] imgs = errimgs.split(",");
            for (String url : imgs) {
                images.add(NetComm.getImageHost() + url);
            }
        }
        int paddingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.image_padding_left);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int imgWidth = width / 3 - 2 * paddingLeft;
        for (int i = 0; i < images.size(); i++) {
            ImageView view = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = i <=0 ? 0 :paddingLeft;
            AppLogger.LOGD("demo","image url:"+images.get(i)+",leftMargin:"+params.leftMargin);
            GlideApp.with(mContext).asDrawable().override(imgWidth,height/5).load(images.get(i)).centerCrop().into(view);
            group.addView(view, params);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private int getPositionForGroup(int groupId) {
        for (int i = 0; i < mItemList.size(); i++) {
            Object obj = mItemList.get(i).getData();
            if (obj != null && obj instanceof TaskGroupInfo) {
                TaskGroupInfo info = (TaskGroupInfo) obj;
                if (info.getGroupId() == groupId) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void showOptionsWindow(View v) {
        PopupMenu menu = new PopupMenu(mContext, v);
        menu.getMenuInflater().inflate(R.menu.menu_task_options, menu.getMenu());
        menu.setOnMenuItemClickListener(onMenuItemClickListener);
        for (int i = 0; i < menu.getMenu().size(); i++) {
            menu.getMenu().getItem(i).setActionView(v);
        }
        menu.show();
    }

    public void setOnOptionsSelectListener(OptionSelectListener listener) {
        mOptionsListener = listener;
    }

    public void setOnAddClickListener(OnAddItemClickListener click) {
        mOnAddItemClicklistener = click;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public interface OptionSelectListener {
        void onOptionsNormal(TaskInfo info);

        void onOptionsError(TaskInfo info);
    }


    public interface OnAddItemClickListener {
        void onAddClick();
    }

    /**
     * 滚动监听接口
     */
    public interface OnScrollListener {
        void scrollTo(int pos);
    }

}
