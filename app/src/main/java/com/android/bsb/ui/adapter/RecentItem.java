package com.android.bsb.ui.adapter;

import com.android.bsb.bean.TaskInfo;

public class RecentItem {

    public static final int TYPE_STAMP = 0;

    public static final int TYPE_TASK = 1;


    public RecentItem(int type,String timeStamp,TaskInfo task){
        this.type = type;
        this.timeStamp = timeStamp;
        this.recentTask = task;
    }

    private int type;

    private String timeStamp;

    private TaskInfo recentTask;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public TaskInfo getRecentTask() {
        return recentTask;
    }

    public void setRecentTask(TaskInfo recentTask) {
        this.recentTask = recentTask;
    }
}
