package com.android.bsb.ui.task;

import android.util.Log;

import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.ErrorTaskResult;
import com.android.bsb.bean.ErrorTaskResult_;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.User;
import com.android.bsb.data.local.LocalDataManager;
import com.android.bsb.data.remote.BankTaskApi;
import com.android.bsb.data.remote.CommObserver;
import com.android.bsb.ui.base.IBasePresent;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.ImageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter extends IBasePresent<TaskView> {

    private BankTaskApi mApis;
    private LocalDataManager mDbManager;

    @Inject
    public TaskPresenter(BankTaskApi api, LocalDataManager manager) {
        mApis = api;
        mDbManager = manager;
    }



    public void getTaskGroupList() {
        User login = mView.getLoginUser();
        mApis.queryUserTaskGroup(login.getUid())
                .compose(mView.<List<TaskGroupInfo>>bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<TaskGroupInfo>>() {
                    @Override
                    public void onRequestNext(List<TaskGroupInfo> groupInfos) {
                        mView.showTaskGroupList(groupInfos);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD(null, "code:" + code + ",msg:" + msg);
                    }
                });
    }


    public void addNewTaskToGroup(int groupId, int type, List<Integer> delIds, List<String> addTasks) {
        User login = mView.getLoginUser();
        mApis.updateOrDelTaskGroup(groupId, type, login.getUid(), delIds, addTasks)
                .compose(mView.bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {
                        AppLogger.LOGD("demo", "" + o);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo", "code:" + code + ",msg:" + msg);
                    }
                });
    }


    public void createNewTaskGroup(TaskGroupInfo info) {
        User login = mView.getLoginUser();
        mApis.createTaskGroupAndTask(login, info)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {
                        //Toast.makeText(mView.getContext(),s,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        //Toast.makeText(mView.getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void publishTaskToSecurity(List<Integer> securityIds, List<String> taskIds, long start, long end, List weeks) {
        User login = mView.getLoginUser();
        mApis.publishTask(securityIds, taskIds, login.getUid(), start, end, weeks)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .compose(mView.bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver() {
                    @Override
                    public void onRequestNext(Object o) {
                        Log.d("demo", "");
                        mView.submitTaskResult(true,null,null);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Log.d("demo", "code:" + code + ",msg:" + msg);
                    }
                });
    }


    public void getSecurityProcessList() {
        User login = mView.getLoginUser();
        mApis.querySecurityTaskGroup(login.getUid())
                .compose(mView.<List<TaskGroupInfo>>bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<TaskGroupInfo>>() {
                    @Override
                    public void onRequestNext(List<TaskGroupInfo> list) {
                        mView.showTaskGroupList(list);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                        if(code == 217){
                            mView.showEmptyData();
                        }
                    }
                });

    }


    public void feedbackErrorTaskResult(final int processId, final List<String> files, final int errorRank,
                                        final String errorMsg, final String geographic) {
        final User login = mView.getLoginUser();
        Observable.fromArray(files)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(List<String> strings) throws Exception {
                        File root = new File(mView.getContext().getCacheDir(),"images");
                        return ImageUtils.compressImage(root,strings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        Box<ErrorTaskResult> box = mDbManager.getBoxStore().boxFor(ErrorTaskResult.class);
                        StringBuffer buffer = new StringBuffer();
                        for (int i = 0 ;i < files.size() ;i++){
                            buffer.append(files.get(i).getAbsolutePath());
                            if(i < files.size()-1){
                                buffer.append(",");
                            }
                        }
                        ErrorTaskResult result = new ErrorTaskResult(processId,errorRank,errorMsg,buffer.toString());
                        box.put(result);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<File>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<File> files) throws Exception {
                        return mApis.taskErrorPrcessResult(login.getUid(),processId,files,errorRank,errorMsg,geographic);
                    }})
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mView.<String>bindToLife())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {
                        mView.hideProgress();
                        Box<ErrorTaskResult> box = mDbManager.getBoxStore().boxFor(ErrorTaskResult.class);
                        List<ErrorTaskResult> list = box.find(ErrorTaskResult_.procssId,processId);
                        if(list.size() > 0){
                            ErrorTaskResult result = list.get(0);
                            result.setFlag(true);
                            box.put(result);
                        }
                        mView.hideProgress();
                        List<Integer> ids = new ArrayList<>();
                        ids.add(processId);
                        mView.submitTaskResult(true,null,null);

                    }

                    @Override
                    public void onError(int code, String msg) {
                        mView.hideProgress();
                        mView.onFaildCodeMsg(code,msg);
                    }
                });
    }


    public void feedbackNormalTaskList(final List<Integer> ids, final List<String> geos) {
        User login = mView.getLoginUser();
        mApis.taskProcessResult(login.getUid(), ids, geos)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mView.<String>bindToLife())
                .subscribe(new CommObserver<String>() {
                    @Override
                    public void onRequestNext(String s) {
                        Map<Integer,String> maps = new HashMap<>();
                        for (int i = 0;i< ids.size() ;i++){
                            maps.put(ids.get(i),geos.get(i));
                        }
                        mView.submitTaskResult(true,maps,null);
                    }

                    @Override
                    public void onError(int code, String msg) {

                    }
                });

    }

    public void getProcessResultForDept(){
        User user = mView.getLoginUser();
        long[] times = getWeekDay();
        mApis.queryProcessResult(times[0],times[1],user.getDeptCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommObserver<List<CheckTaskInfo>>() {
                    @Override
                    public void onRequestNext(List<CheckTaskInfo> checkTaskInfos) {
                        AppLogger.LOGD("demo",""+checkTaskInfos.size());
                        mView.showAllProcessTaskResult(checkTaskInfos);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        AppLogger.LOGD("demo","code:"+code+",msg:"+msg);
                        mView.showNetError();
                    }
                });
    }


    @Override
    public void getData() {

    }


    private long[] getWeekDay(){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTime().getTime();
        long start = now - (1000 * 60 * 60  *24 * 3 );
        return new long[]{start,now};

    }

}
