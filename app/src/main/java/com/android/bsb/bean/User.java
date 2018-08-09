package com.android.bsb.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.bsb.AppComm;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.Utils;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName(value = "userid",alternate = {"id"})
    private int uid = 0;

    @SerializedName("loginname")
    private String loginName;

    private String loginPwd;

    @SerializedName(value = "username",alternate = {"name"})
    private String  uname;

    @SerializedName("rolecode")
    private int role;

    @SerializedName("rolename")
    private String roleName;

    @SerializedName("deptname")
    private String deptName;

    @SerializedName("deptcode")
    private int deptCode;

    @SerializedName("cellphone")
    private String cellpahone;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLoginName() {
        return loginName;
    }

    public User(){}

    public User(User user){
        this.uid = user.uid;
        this.loginName = user.loginName;
        this.cellpahone = user.cellpahone;
        this.uname = user.uname;
        this.role = user.role;
        this.roleName = user.roleName;
        this.deptCode = user.deptCode;
        this.deptName = user.deptName;
    }

    public User(String accountStr){
        String[] args = accountStr.split(":");

        for (int i = 0;i < args.length ;i++){
            AppLogger.LOGD("demo","args:"+args[i]);
        }

        this.uid = Integer.parseInt(Utils.decrypt(args[0]));
        this.loginName =Utils.decrypt(args[1]);
        this.uname = Utils.decrypt(args[2]);
        this.role = Integer.parseInt(Utils.decrypt(args[3]));
        this.roleName = Utils.decrypt(args[4]);
        this.deptName = Utils.decrypt(args[5]);
        this.loginPwd = Utils.decrypt(args[6]);
        this.deptCode = Integer.parseInt(Utils.decrypt(args[7]));
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getRole() {
        return role;
    }


    public String getCellpahone() {
        return cellpahone;
    }

    public void setCellpahone(String cellpahone) {
        this.cellpahone = cellpahone;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }


    public boolean isAdmin(){
        return role == AppComm.ROLE_TYPE_ADMIN;
    }
    public boolean isManager(){
        return role == AppComm.ROLE_TYPE_MANAGER;
    }
    public boolean isPostManager(){
        return role == AppComm.ROLE_TYPE_POSTMAN;
    }
    public boolean isSecurity(){
        return role == AppComm.ROLE_TYPE_SECURITY;
    }


    public int getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(int deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", loginName='" + loginName + '\'' +
                ", loginPwd='" + loginPwd + '\'' +
                ", uname='" + uname + '\'' +
                ", role=" + role +
                ", roleName='" + roleName + '\'' +
                ", deptName='" + deptName + '\'' +
                ", deptCode=" + deptCode +
                ", cellpahone='" + cellpahone + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.loginName);
        dest.writeString(this.loginPwd);
        dest.writeString(this.uname);
        dest.writeInt(this.role);
        dest.writeString(this.roleName);
        dest.writeString(this.deptName);
        dest.writeInt(this.deptCode);
        dest.writeString(this.cellpahone);
    }

    protected User(Parcel in) {
        this.uid = in.readInt();
        this.loginName = in.readString();
        this.loginPwd = in.readString();
        this.uname = in.readString();
        this.role = in.readInt();
        this.roleName = in.readString();
        this.deptName = in.readString();
        this.deptCode = in.readInt();
        this.cellpahone = in.readString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getUid() == ((User) obj).getUid();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
