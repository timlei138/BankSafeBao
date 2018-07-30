package com.android.bsb.ui.adapter;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;

public class TaskAdapterItem {

    private int groupId;

    private int viewType;

    private Object data;


    public static TaskAdapterItem asGroup(TaskGroupInfo groupInfo){
        TaskAdapterItem item = new TaskAdapterItem();
        item.viewType = TaskGroupAdapter.VIEW_TYPE_PARENT;
        item.data = groupInfo;
        return item;
    }


    public static TaskAdapterItem asChild(TaskInfo info){
        TaskAdapterItem item = new TaskAdapterItem();
        item.groupId = info.getTaskGroupId();
        item.viewType = TaskGroupAdapter.VIEW_TYPE_CHILD;
        item.data = info;
        return item;
    }

    public static TaskAdapterItem asAddItem(){
        TaskAdapterItem item = new TaskAdapterItem();
        item.groupId = -1;
        item.viewType = TaskGroupAdapter.VIEW_TYPE_ADD;
        return item;
    }


    public int getViewType(){
        return viewType;
    }

    @Override
    public String toString() {
        return "TaskAdapterItem{" +
                "viewType=" + viewType +
                ", data=" + data +
                '}';
    }

    public Object getData(){
        return data;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
