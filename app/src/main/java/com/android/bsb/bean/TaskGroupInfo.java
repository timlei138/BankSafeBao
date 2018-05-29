package com.android.bsb.bean;

import java.util.List;

public class TaskGroupInfo {

    private int groupId;

    private String groupName;

    private String groupDesc;

    private int groupDegree = 0;

    private boolean isExpand;

    private List<TaskInfo> taskList;

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
}
