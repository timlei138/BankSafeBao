package com.android.bsb.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class CheckTaskInfo {


    @SerializedName("executeName")
    private String securityName;

    @SerializedName("executeuserid")
    private String securityId;

    @SerializedName("deptname")
    private String deptName;

    @SerializedName("taskResult")
    private List<TaskInfo> execList;

    private Map<Long,List<TaskInfo>> mRentList;

    public String getSecurityName() {
        return securityName;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public List<TaskInfo> getExecList() {
        return execList;
    }

    public void setExecList(List<TaskInfo> execList) {
        this.execList = execList;
    }



    public void setRecentTaskList(Map<Long,List<TaskInfo>> list){
        mRentList = list;
    }

    public Map<Long,List<TaskInfo>> getRecentTaskList(){

        if(getExecList() == null){
            return null;
        }

        List<TaskInfo> list = getExecList();


        return mRentList;
    }
}
