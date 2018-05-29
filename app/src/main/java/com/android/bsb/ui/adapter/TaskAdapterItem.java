package com.android.bsb.ui.adapter;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;

public class TaskAdapterItem {

    private int position;

    private int viewType;

    private Object data;


    public static TaskAdapterItem asGroup(int pos ,TaskGroupInfo groupInfo){
        TaskAdapterItem item = new TaskAdapterItem();
        item.viewType = TaskListAdapter.VIEW_TYPE_PARENT;
        item.position = pos;
        item.data = groupInfo;
        return item;
    }


    public static TaskAdapterItem asChild(int pos, TaskInfo info){
        TaskAdapterItem item = new TaskAdapterItem();
        item.viewType = TaskListAdapter.VIEW_TYPE_CHILD;
        item.position = pos;
        item.data = info;
        return item;
    }


    public int getViewType(){
        return viewType;
    }

    public Object getData(){
        return data;
    }

    public int getPosition(){
        return position;
    }
}
