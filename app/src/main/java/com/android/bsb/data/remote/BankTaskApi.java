package com.android.bsb.data.remote;


import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.util.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class BankTaskApi {

    private BankServer mService;

    private static BankTaskApi mInstance;


    public static BankTaskApi getInstance(BankServer server){
        if(mInstance == null){
            mInstance = new BankTaskApi(server);
        }
        return mInstance;
    }


    private BankTaskApi(BankServer server){
        mService = server;
    }


    /**
     * 用户登录接口
     * @param name 用户名
     * @param password 密码
     * @return 用户信息
     */

    public Observable<User> userLogin(String name, String password){
        return mService.userLogin(name,password)
                .map(new ServerResultFunc<User>())
                .onErrorResumeNext(new HttpResultFunc<User>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> createDept(String deptname,String username,String phone,int parentcode,int userid){
        return mService.createDeptment(deptname,username,phone,parentcode,userid)
                .map(new ServerResultFunc<String>())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> createUser(String name,String phone,int deptCode,String loginname,int role,int uid){
        return mService.createUser(name,phone,deptCode,loginname,role,uid)
                .map(new ServerResultFunc<String>())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<User>> queryAllUser(int uid,int roleId){
        return mService.queryAllUser(uid,roleId)
                .map(new ServerResultFunc<List<User>>())
                .onErrorResumeNext(new HttpResultFunc<List<User>>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<TaskGroupInfo>> queryUserTaskGroup(int uid){
        return mService.queryUserTaskGroup(uid)
                .map(new ServerResultFunc<List<TaskGroupInfo>>())
                .onErrorResumeNext(new HttpResultFunc<List<TaskGroupInfo>>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> createTaskGroupAndTask(User user,TaskGroupInfo groupInfo){
        List<String> tasks = new ArrayList<>();
        for (TaskInfo info : groupInfo.getTaskList()){
            tasks.add(info.getTaskName());
        }
        return mService.createTaskGroupAndTask(1,groupInfo.getGroupDesc(),groupInfo.getGroupName(),user.getUid(),tasks)
                .map(new ServerResultFunc<String>())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }

    public Observable updateOrDelTaskGroup(int groupId,int type,int uid,List<Integer> ids,List<String> addTaskList){
        return mService.updateOrDelTaskGroup(groupId,type,uid,ids,addTaskList)
                .map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<User>> querySecuityList(int deptCode){
        return mService.querySecurityList(deptCode)
                .map(new ServerResultFunc<List<User>>())
                .onErrorResumeNext(new HttpResultFunc<List<User>>())
                .subscribeOn(Schedulers.io());
    }


    public Observable publishTask(List<Integer> processIds,List<String> taskIds,int uid,long start,long end,List<Integer> weeks){
        return mService.publishTask(processIds,taskIds,uid,start,end,weeks).map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<TaskGroupInfo>> querySecurityTaskGroup(int uid){
        return mService.querySecurityTaskList(uid)
                .map(new ServerResultFunc<List<TaskGroupInfo>>())
                .onErrorResumeNext(new HttpResultFunc<List<TaskGroupInfo>>())
                .subscribeOn(Schedulers.io());
    }


    public Observable<String> taskErrorPrcessResult(int uid, int processId, List<File> files,int errorRank,String errorMsg,String geo){
        Map<String,RequestBody> map = new HashMap<>();
        map.put("loginId",toRequestBody(String.valueOf(uid)));
        map.put("processId",toRequestBody(String.valueOf(processId)));
        map.put("errormsg",toRequestBody(errorMsg));
        map.put("errorrank",toRequestBody(String.valueOf(errorRank)));
        map.put("geographic",toRequestBody(geo));
        return mService.taskErrorResult(map,filesToMultipartBodyParts(files))
                .map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> taskProcessResult(int uid,List<Integer> processIds,List<String> geos){
        return mService.taskProcessResult(uid,processIds,geos).map(new ServerResultFunc())
                .onErrorResumeNext(new HttpResultFunc())
                .subscribeOn(Schedulers.io());
    }


    public Observable<List<CheckTaskInfo>> queryProcessResult(long start, long end, int deptId){
        return mService.queryTaskProcessResult(start,end,deptId)
                .map(new ServerResultFunc<List<CheckTaskInfo>>())
                .onErrorResumeNext(new HttpResultFunc<List<CheckTaskInfo>>())
                .subscribeOn(Schedulers.io());
    }


    public Observable<String> uploadStep(int uid,int steps,long time){
        return mService.uploadStep(uid,steps,time)
                .map(new ServerResultFunc<String>())
                .onErrorResumeNext(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io());
    }


    public static RequestBody toRequestBody(String value){
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"),value);
        return body;
    }


    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        if(files == null || files.size() <=0){
            return null;
        }
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photoPath", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }



    private class ServerResultFunc<T> implements Function<BaseResultEntity<T>, T> {
        @Override
        public T apply(BaseResultEntity<T> httpResult) throws Exception {
            AppLogger.LOGD("BankTaskApi","httpResult:"+httpResult.toString());
            if (httpResult.getCode() != 200) {
                throw new ServerException(httpResult.getCode(),httpResult.getMsg());

            }
            if(httpResult.getCode() == 200 && httpResult.getData() == null){
                return (T)httpResult.getMsg();
            }
            return httpResult.getData();
        }
    }

    private class HttpResultFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

}
