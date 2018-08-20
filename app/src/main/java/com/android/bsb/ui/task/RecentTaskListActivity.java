package com.android.bsb.ui.task;

import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.EntryData;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.adapter.RecentItem;
import com.android.bsb.ui.adapter.RecentItemDecoration;
import com.android.bsb.ui.adapter.RecentTaskAdapter;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.TimeUtils;
import com.android.bsb.widget.ChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RecentTaskListActivity extends BaseActivity {

    @BindView(R.id.recentList)
    RecyclerView mRecentList;
    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.ChartView)
    ChartView mChartView;

    private RecentTaskAdapter mRecentAdapter;

    private List<TaskInfo> mRecents;

    private String TAG = getClass().getSimpleName();

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_recent_task;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        mToolsBar.setTitle("历史任务记录");
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecentAdapter = new RecentTaskAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecentList.setLayoutManager(linearLayoutManager);

        mRecentList.addItemDecoration(new RecentItemDecoration());

        mRecentList.setAdapter(mRecentAdapter);
        mRecents = getIntent().getParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST);
        if (mRecents == null) {
            Toast.makeText(this, "当前参数异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        AppLogger.LOGD(TAG,"size:"+mRecents.size());

    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {

        Observable.fromArray(mRecents).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                showProgress();
            }
        }).map(new Function<List<TaskInfo>, List<RecentItem>>() {
            @Override
            public List<RecentItem> apply(List<TaskInfo> taskInfos) throws Exception {
                return getRecentList();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecentItem>>() {
                    @Override
                    public void accept(List<RecentItem> recentItems) throws Exception {
                        hideProgress();
                        mRecentAdapter.setList(recentItems);
                    }
                });


        List<EntryData> list = new ArrayList<>();
        list.add(new EntryData("8/24",1290f));
        list.add(new EntryData("8/25",3200f));
        list.add(new EntryData("8/26",12000f));
        list.add(new EntryData("8/27",4089f));
        list.add(new EntryData("8/28",19008f));
        list.add(new EntryData("8/29",46f));
        list.add(new EntryData("8/30",100f));

        mChartView.setData(list);


    }


    private List<RecentItem> getRecentList(){
        //分类
        Map<String,List<RecentItem>> finalRecents = new HashMap<>();
        RecentItem addItem ;
        for(TaskInfo info : mRecents){
            long begin = info.getBeginDate();
            String date = TimeUtils.getTime(begin);
            if(finalRecents.containsKey(date)){
                addItem = new RecentItem(RecentItem.TYPE_TASK,null,info);
                finalRecents.get(date).add(addItem);
            }else{
                RecentItem item = new RecentItem(RecentItem.TYPE_STAMP,date,null);
                RecentItem task = new RecentItem(RecentItem.TYPE_TASK,null,info);
                List<RecentItem> addList  = new ArrayList<>();
                addList.add(item);
                addList.add(task);
                finalRecents.put(date,addList);
            }
        }

        List<RecentItem> items = new ArrayList<>();
        //排序分类
        for (List<RecentItem> item : finalRecents.values()){
            items.addAll(item);
        }
        return items;
    }

}
