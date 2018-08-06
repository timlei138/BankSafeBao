package com.android.bsb.bean;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ErrorTaskResult {

    @Id
    private long id;

    private int procssId;

    private int errorRank;

    private String errorMsg;

    private String files;

    private boolean flag;


    public ErrorTaskResult(){}


    public ErrorTaskResult(int procssId,int errorRank,String errorMsg,String files){
        this.procssId = procssId;
        this.errorMsg = errorMsg;
        this.errorRank = errorRank;
        this.files = files;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProcssId() {
        return procssId;
    }

    public void setProcssId(int procssId) {
        this.procssId = procssId;
    }

    public int getErrorRank() {
        return errorRank;
    }

    public void setErrorRank(int errorRank) {
        this.errorRank = errorRank;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
