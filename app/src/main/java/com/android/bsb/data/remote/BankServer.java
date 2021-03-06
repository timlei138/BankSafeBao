package com.android.bsb.data.remote;

import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.StepData;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

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
    Observable<BaseResultEntity<List<User>>> queryAllUser(@Field("userid")int uid,@Field("rolecode") int roldId);

    @POST("queryTaskGroupList")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<TaskGroupInfo>>> queryUserTaskGroup(@Field("loginId")int uid);


    @POST("createTaskGroupAndTask")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> createTaskGroupAndTask(@Field("type") int type,@Field("summary") String summary,@Field("title")String title
            ,@Field("createcode")int createcode,@Field("taskList") List<String> taskList);

    /**
     * 对任务组及任务列表内容进行擦操作
     * type 1 删除 | 2 修改  | 3 新增
     *
     * @return
     */
    @POST("updateOrDelTaskData")
    @FormUrlEncoded
    Observable<BaseResultEntity> updateOrDelTaskGroup(@Field("groupId") int groupId,@Field("type") int type,@Field("loginId") int ui,
                                                 @Field("taskIdList") List<Integer> ids,@Field("taskNames") List<String> task);

    /**
     *  发布任务接口
     * @param processIds  执行人员id集合
     * @param taskIds   任务id集合
     * @param uid  发布人员uid
     * @return
     */
    @POST("publishTask")
    @FormUrlEncoded
    Observable<BaseResultEntity> publishTask(@Field("executeuserid") List<Integer> processIds,@Field("tasks") List<String> taskIds,
                                             @Field("postuserid") int uid,@Field("startDate") long start,@Field("endDate") long end,@Field("weekday") List<Integer> weeks);

    /**
     * 查询安保人员列表
     * @param deptcode
     * @return
     */
    @POST("querySecurityList")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<User>>> querySecurityList(@Field("deptcode") int deptcode);


    /**
     * 查询安保人员执行任务
     * @param uid
     * @return
     */
    @POST("querySecTaskList")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<TaskGroupInfo>>> querySecurityTaskList(@Field("executeuserid")int uid);


    /**
     * 安保人员提交 异常任务 检查结果
     * @param loginId
     * @param processId
     * @param errormsg
     * @param errorRank
     * @param geographic
     * @return
     */
    @POST("taskErrorResult")
    @Multipart
    Observable<BaseResultEntity<String>> taskErrorResult(@Part("loginId") int loginId, @Part("processId")int processId,
                                                 @Part("errormsg")String errormsg, @Part("errorrank") int errorRank,
                                                 @Part("geographic")String geographic,@Part List<MultipartBody.Part> parts);

    @POST("taskErrorResult")
    @Multipart
    Observable<BaseResultEntity<String>> taskErrorResult(@PartMap Map<String,RequestBody> map,@Part List<MultipartBody.Part> parts);

    /**
     * 安保人员提交 正常任务 检查结果
     * @param loginId
     * @param proess  执行任务id 列表
     * @param geos    对应任务地理位置 列表
     * @return
     */
    @POST("taskProcessResult")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> taskProcessResult(@Field("loginId")int loginId,@Field("processIds")List<Integer> proess,
                                                   @Field("geographics" )List<String> geos);

    /**
     * 获取到当前部门到安保人员所有执行的任务
     * @param start
     * @param end
     * @param deptId
     * @return
     */
    @POST("queryProcessResult")
    @FormUrlEncoded
    Observable<BaseResultEntity<List<CheckTaskInfo>>> queryTaskProcessResult(@Field("startTime") long start, @Field("endTime") long end , @Field("deptId") int deptId);

    /**
     * 获取步数信息
     * @param uid
     * @param time
     * @param endtime
     * @return
     */
    Observable<BaseResultEntity<List<StepData>>> querySetupList(@Field("uid")int uid, @Field("starttime")long time, @Field("endtime")long endtime);

    /**
     * 上传当天步数
     * @param uid
     * @param step
     * @param time
     * @return
     */
    @POST("saveSteps")
    @FormUrlEncoded
    Observable<BaseResultEntity<String>> uploadStep(@Field("userid") int uid,@Field("steps") int step,@Field("time") long time);


}
