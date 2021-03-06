package com.android.bsb.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.android.bsb.util.AppLogger;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class TaskInfo implements Parcelable {

    @Id
    private long _id;

    @SerializedName("id")
    private int taskId;

    @SerializedName("taskname")
    private String taskName;

    @SerializedName("processid")
    private int processId;

    private int taskGroupId;

    // 01 发布  02 成功   03 失败
    @SerializedName(value = "status" ,alternate = {"taskstatus"})
    private String result;

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    @SerializedName("starttime")
    private long beginDate;

    @SerializedName("errormsg")
    private String errMsg;
    @SerializedName("errorphoto")
    String errImages;
    @SerializedName("errorrank")
    private String errorRank;
    @SerializedName("geographic")
    private String geographic;


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

    @Override
    public boolean equals(Object obj) {
        return this.getTaskId() == ((TaskInfo) obj).getTaskId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.taskId);
        dest.writeString(this.taskName);
        dest.writeInt(this.processId);
        dest.writeInt(this.taskGroupId);
        dest.writeString(this.result);
        dest.writeLong(this.beginDate);
        dest.writeString(this.errImages);
        dest.writeString(this.geographic);
    }

    public TaskInfo() {
    }

    protected TaskInfo(Parcel in) {
        this.taskId = in.readInt();
        this.taskName = in.readString();
        this.processId = in.readInt();
        this.taskGroupId = in.readInt();
        this.result = in.readString();
        this.beginDate = in.readLong();
        this.errImages = in.readString();
        this.geographic = in.readString();
    }

    public static final Parcelable.Creator<TaskInfo> CREATOR = new Parcelable.Creator<TaskInfo>() {
        @Override
        public TaskInfo createFromParcel(Parcel source) {
            return new TaskInfo(source);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrImages() {
        return errImages;
    }

    public void setErrImages(String errImages) {
        this.errImages = errImages;
    }

    public String getErrorRank() {
        return errorRank;
    }

    public void setErrorRank(String errorRank) {
        this.errorRank = errorRank;
    }

    public String getGeographic() {
        return geographic;
    }

    public void setGeographic(String geographic) {
        this.geographic = geographic;
    }

    public String getGeographicInfo(){
        String addr = "未知位置";
        if(TextUtils.isEmpty(this.geographic)){
            return addr;
        }
        String[] splits = geographic.split("#");
        for (String str : splits){
            if(str.contains("addr")){
                String[] loc = str.split(":");
                addr = loc[1];
                break;
            }
        }
        return addr;

    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", processId=" + processId +
                ", taskGroupId=" + taskGroupId +
                ", result='" + result + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", errImages=" + errImages +
                ", errorRank=" + errorRank +
                '}';
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
