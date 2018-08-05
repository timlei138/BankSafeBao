package com.android.bsb.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.bsb.util.AppLogger;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupInfo implements Parcelable {

    public static final int TYPE_NONE= - 1;
    public static final int TYPE_CREATE = -2;

    @SerializedName("id")
    private int groupId;

    @SerializedName("title")
    private String groupName;

    @SerializedName("summary")
    private String groupDesc;

    private int groupDegree = 0;

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    @SerializedName("name")
    private String groupCreator;

    private boolean isExpand;

    @SerializedName("tasklist")
    private List<TaskInfo> taskList = new ArrayList<>();

    public TaskGroupInfo(){

    }

    public TaskGroupInfo(int groupId,String content){
        this.groupId = groupId;
        groupName = content;
    }



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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupId);
        dest.writeString(this.groupName);
        dest.writeString(this.groupDesc);
        dest.writeInt(this.groupDegree);
        dest.writeString(this.groupCreator);
        dest.writeByte(this.isExpand ? (byte) 1 : (byte) 0);
        dest.writeList(this.taskList);
    }

    protected TaskGroupInfo(Parcel in) {
        this.groupId = in.readInt();
        this.groupName = in.readString();
        this.groupDesc = in.readString();
        this.groupDegree = in.readInt();
        this.groupCreator = in.readString();
        this.isExpand = in.readByte() != 0;
        this.taskList = new ArrayList<TaskInfo>();
        in.readList(this.taskList, TaskInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<TaskGroupInfo> CREATOR = new Parcelable.Creator<TaskGroupInfo>() {
        @Override
        public TaskGroupInfo createFromParcel(Parcel source) {
            return new TaskGroupInfo(source);
        }

        @Override
        public TaskGroupInfo[] newArray(int size) {
            return new TaskGroupInfo[size];
        }
    };
}
