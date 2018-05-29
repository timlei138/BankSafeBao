package com.android.bsb.bean;

public class TaskInfo {

    private int taskId;

    private String taskName;

    private int taskGroupId;

    private int result;


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(int taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
