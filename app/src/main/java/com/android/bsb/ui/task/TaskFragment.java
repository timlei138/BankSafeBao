package com.android.bsb.ui.task;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.service.LocationService;
import com.android.bsb.service.StepService;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.adapter.TaskListItemDecoration;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;
import com.android.bsb.util.Utils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class TaskFragment extends BaseFragment<TaskPresenter> implements TaskView{


    @BindView(R.id.tasklist_recycle)
    RecyclerView mTaskListRecycler;
    @BindView(R.id.submit_btn)
    AppCompatButton mAllsubmatBtn;

    private TaskGroupAdapter mAdapter;

    private boolean isMutiSelect = false;

    private String TAG = getClass().getSimpleName();

    private LocationService mLocationService;

    private StringBuffer locBuff = new StringBuffer();


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_tasklist_page;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().
                applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        mAdapter = new TaskGroupAdapter(getContext());
        mAdapter.setOnOptionsSelectListener(mOptionSelectListener);
        mTaskListRecycler.setAdapter(mAdapter);
        mTaskListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mTaskListRecycler.addItemDecoration(new TaskListItemDecoration(getContext()));


        mLocationService = AppApplication.getLocationService();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            RxPermissions permissions = new RxPermissions(this);
            permissions.requestEach(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    mLocationService.registerListener(mLocationListener);
                    mLocationService.setLocationOption(mLocationService.getDefaultClientOption());
                    mLocationService.start();
                }
            });
        }else{
            mLocationService.registerListener(mLocationListener);
            mLocationService.setLocationOption(mLocationService.getDefaultClientOption());
            mLocationService.start();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationService.unregisterListener(mLocationListener);
        mLocationService.stop();

    }

    @Override
    protected void updateView(boolean isRefresh) {
        mPresenter.getSecurityProcessList();
    }

    @Override
    public User getLoginUser() {
        return super.getLoginUser();
    }


    @Override
    public void actionBarItemClick(MenuItem item) {
        super.actionBarItemClick(item);
        if(item.getItemId() == R.id.action_sync){
            AppLogger.LOGD("demo","syncData");
            updateView(false);
        }else if(item.getItemId() == R.id.action_edit){
            isMutiSelect = !isMutiSelect;
            mAdapter.setMultiiSelect(isMutiSelect);
            updateSubmitButton();

        }
    }



    @OnClick(R.id.submit_btn)
    public void submatAllTask(View view){
        Map<Integer,List<TaskInfo>> mSelectMap = mAdapter.getmSelectedList();
        if(mSelectMap.size() <=0){
            Toast.makeText(getContext(),"请选择要提交的任务",Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> pendingList = new ArrayList<>();
        for (List<TaskInfo> list : mSelectMap.values()){
            for (TaskInfo id : list){
                pendingList.add(id.getProcessId());
            }
        }
        List<String> geos = new ArrayList<>();
        for (int i =0;i<pendingList.size();i++){
            geos.add(locBuff.toString());
        }
        mPresenter.feedbackNormalTaskList(pendingList,geos);
    }


    @Override
    public void showTaskGroupList(List<TaskGroupInfo> list) {

        List<TaskAdapterItem> items = new ArrayList<>(list.size());
        AppLogger.LOGD(TAG,"size:"+items.size());
        for (TaskGroupInfo group : list){
            TaskAdapterItem groupItem = TaskAdapterItem.asGroup(group);
            items.add(groupItem);
            AppLogger.LOGD(TAG,"groupId:"+groupItem.getGroupId());
        }
        mAdapter.setItemList(items);
    }

    @Override
    public void submitTaskResult(boolean success) {
        if(success){
            isMutiSelect = false;
            mAdapter.setMultiiSelect(isMutiSelect);
            updateView(true);
        }
        updateSubmitButton();
    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {

    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {
        updateView(false);
    }

    private void updateSubmitButton(){
        if(isMutiSelect){
            mAllsubmatBtn.setVisibility(View.VISIBLE);
        }else{
            mAllsubmatBtn.setVisibility(View.GONE);
        }
    }

    private TaskGroupAdapter.OptionSelectListener mOptionSelectListener = new TaskGroupAdapter.OptionSelectListener() {
        @Override
        public void onOptionsNormal(TaskInfo info) {
            List<Integer> ids = new ArrayList<>();
            ids.add(info.getProcessId());
            List<String> geos = new ArrayList<>();
            geos.add(locBuff.toString());
            mPresenter.feedbackNormalTaskList(ids,geos);
        }

        @Override
        public void onOptionsError(TaskInfo info) {
            Intent intent = new Intent();
            intent.setClass(getContext(), ErrorTaskEditorActivity.class);
            intent.putExtra("processId",info.getProcessId());
            intent.putExtra("geo",locBuff.toString());
            startActivityForResult(intent,1000);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isMutiSelect){
            isMutiSelect = false;
            mAdapter.setMultiiSelect(isMutiSelect);
        }
        updateView(true);
        updateSubmitButton();
    }


    private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息

                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                AppLogger.LOGD(TAG,"location:"+sb.toString());
                StringBuffer json = new StringBuffer();

                json.append("latitude:"+location.getLatitude()+"#");
                json.append("lontitude:"+location.getLongitude()+"#");
                json.append("Country:"+location.getCountry()+"#");
                json.append("citycode:"+location.getCityCode()+"#");
                json.append("City:"+location.getCity()+"#");
                json.append("District:"+location.getDistrict()+"#");
                json.append("Street:"+location.getStreet()+"#");
                json.append("addr:"+location.getAddrStr()+"#");
                json.append("locType:"+location.getLocType());

                locBuff = json;

            }

        }
    };

}
