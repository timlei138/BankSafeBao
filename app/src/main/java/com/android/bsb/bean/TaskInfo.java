package com.android.bsb.bean;

import com.google.gson.annotations.SerializedName;

public class TaskInfo {

    @SerializedName("id")
    private int taskId;

    @SerializedName("taskname")
    private String taskName;

    @SerializedName("processid")
    private int processId;

    private int taskGroupId;

    // 01 发布  02 成功   03 失败
    @SerializedName("status")
    private String result;


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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }


    public boolean isInitTask(){
        return "01".equals(result);
    }

    public boolean isFailTask(){
        return "03".equals(result);
    }

    public boolean isDoneTask(){
        return "02".equals(result);
    }
}
