package com.android.bsb.bean;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupInfo {

    public static final int TYPE_NONE= - 1;
    public static final int TYPE_CREATE = -2;

    private int groupId;

    private String groupName;

    private String groupDesc;

    private int groupDegree = 0;

    private boolean isExpand;

    public TaskGroupInfo(){

    }

    public TaskGroupInfo(int groupId,String content){
        this.groupId = groupId;
        groupName = content;
    }

    private List<TaskInfo> taskList = new ArrayList<>();

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public void setTaskList(List<TaskInfo> list){
        taskList = list;
    }

    public void addTask(TaskInfo info){
        taskList.add(info);
    }


    public List<TaskInfo> getTaskList(){
        return taskList;
    }

    public void setGroupDegree(int level){
        this.groupDegree = level;
    }

    public int getGroupDegree(){
        return groupDegree;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    @Override
    public String toString() {
        return groupName;
    }
}
