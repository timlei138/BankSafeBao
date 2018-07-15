package com.android.bsb.data.remote;

import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BankServer {


    @POST("login/")
    @FormUrlEncoded
    Observable<BaseResultEntity<User>> userLogin(@Field("loginname") String username,
                                                 @Field("password")String pwd);

    @POST("createDept/")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> createDeptment(@Field("depname")String deptname,@Field("username")String uname,
                                                @Field("userphone") String phone,@Field("parentcode") int parentcod,
                                                @Field("userid") int userid);

    @POST("createUser/")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> createUser(@Field("name")String name,@Field("cellphone")String phone,
                                                @Field("deptcode") int deptcode,@Field("loginname") String loginname,
                                                @Field("roleid") int roleid,@Field("userid") int userid);

    @POST("queryUser/")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<User>>> queryAllUser(@Field("userid")int uid);

    @POST("queryTaskList")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<TaskGroupInfo>>> queryUserTaskGroup(@Field("")int uid,@Field("roleId")int roleId);


    @POST("createTaskGroupAndTask")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> createTaskGroupAndTask(@Field("type") int type,@Field("summary") String summary,@Field("title")String title
            ,@Field("createcode")int createcode,@Field("taskList") List<String> taskList);
}
