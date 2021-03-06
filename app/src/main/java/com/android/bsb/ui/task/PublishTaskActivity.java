package com.android.bsb.ui.task;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.CheckTaskInfo;
import com.android.bsb.bean.TaskGroupInfo;
import com.android.bsb.bean.TaskInfo;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.PendingUserAdapter;
import com.android.bsb.ui.adapter.TaskAdapterItem;
import com.android.bsb.ui.adapter.TaskGroupAdapter;
import com.android.bsb.ui.adapter.TaskListItemDecoration;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.user.UserListActivity;
import com.android.bsb.util.AppLogger;
import com.android.bsb.widget.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class PublishTaskActivity extends BaseActivity<TaskPresenter> implements TaskView {

    @BindView(R.id.people_select_list)
    GridView mPeopleGridView;
    @BindView(R.id.task_group_list)
    RecyclerView mTaskGroupRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolsBar;
    @BindView(R.id.publish_btn)
    AppCompatButton mPublishBtn;
    @BindView(R.id.repeat)
    TextView mRepeatDesc;

    private long startDate = System.currentTimeMillis();

    private long endDate = 1912150415000l;

    private List<Integer> weeks = new ArrayList<>();


    private String TAG  = getClass().getSimpleName();

    private ArrayList<User> mSelectedPeoples;
    private ArrayList<TaskGroupInfo> mSelectedTasks;

    private int REQUESTCODE_SELECT_PEOPLE = 10;

    private int REQUESTCODE_SELECT_TASK = 11;

    private TaskGroupAdapter mTaskAdapter;

    private PendingUserAdapter mUserAdapter;

    private PopupWindow mSelectPopupWindow;

    private int publishType = TaskCommon.PUBLISH_TYPE_ALWAYS;


    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_publish;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent)
                .build().inject(this);

    }

    @Override
    protected void initView() {
        mSelectedPeoples = new ArrayList<>();
        mSelectedTasks = new ArrayList<>();
        mToolsBar.setTitle(R.string.menu_pushtask_title);
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTaskAdapter = new TaskGroupAdapter(this);
        mUserAdapter = new PendingUserAdapter(this,addUserClick);
        mPeopleGridView.setAdapter(mUserAdapter);
        mTaskGroupRecyclerView.setAdapter(mTaskAdapter);
        mTaskGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskGroupRecyclerView.addItemDecoration(new TaskListItemDecoration(this));
        mTaskAdapter.setOnAddClickListener(addTaskClick);
        TaskAdapterItem addItem = TaskAdapterItem.asAddItem();
        List<TaskAdapterItem> items = new ArrayList<>();
        items.add(addItem);
        mTaskAdapter.setItemList(items);

    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {
        User addItem = new User();
        addItem.setUid(-1);
        addItem.setUname("点击添加");
        mUserAdapter.addPendItem(addItem);
        if(publishType == TaskCommon.PUBLISH_TYPE_ALWAYS){
            mRepeatDesc.setText("每周都执行");
        }else{
            mRepeatDesc.setText("按时间段执行");
        }

    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showTaskGroupList(List<TaskGroupInfo> groups) {

    }

    @Override
    public void submitTaskResult(boolean success, Map ids, TaskInfo info) {
        if(success){
            Toast.makeText(getContext(),"发布成功！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(getContext(),"发布失败，请稍后再试",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showAllProcessTaskResult(List<CheckTaskInfo> recents) {

    }

    @Override
    public void onFaildCodeMsg(int code, String msg) {

    }


    private PendingUserAdapter.OnAddUserClick addUserClick = new
            PendingUserAdapter.OnAddUserClick() {
        @Override
        public void onAddUser() {
            startPeopleSelected();
        }
    };

    private TaskGroupAdapter.OnAddItemClickListener addTaskClick = new
            TaskGroupAdapter.OnAddItemClickListener() {
        @Override
        public void onAddClick() {
            startTaskSelected();
        }
    };

    @OnClick(R.id.publish_btn)
    public void onPublishTask(View v){
        publishTask();
    }

    private void publishTask(){
        if(mSelectedPeoples== null || mSelectedPeoples.size() <= 0){
            Toast.makeText(this,"请选择执行人员",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mSelectedTasks == null || mSelectedTasks.size() <= 0){
            Toast.makeText(this,"请选择要执行的任务列表",Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> ids = new ArrayList<>(mSelectedPeoples.size());
        for (User execUser : mSelectedPeoples){
            if(execUser.getUid() != -1){
                ids.add(execUser.getUid());
            }
        }
        List<String> taskIds = new ArrayList<>();
        for (TaskGroupInfo info : mSelectedTasks){
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            for (TaskInfo task : info.getTaskList()){
                if(i == 0){
                    buffer.append(""+info.getGroupId()+":"+task.getTaskId());
                }else{
                    buffer.append("-"+task.getTaskId());
                }
                i++;
            }
            taskIds.add(buffer.toString());
        }
        mPresenter.publishTaskToSecurity(ids,taskIds,startDate,endDate,weeks);
    }


    private void startTaskSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), TaskGroupListActivity.class);
        intent.putExtra(BaseActivity.EXTRA_TITLE,"请选择任务组");
        intent.putExtra(BaseActivity.EXTRA_PICK_TASK,true);
        intent.putParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST,mSelectedTasks);
        startActivityForResult(intent,REQUESTCODE_SELECT_TASK);
    }

    private void startPeopleSelected(){
        Intent intent = new Intent();
        intent.setClass(getContext(), UserListActivity.class);
        intent.putExtra(BaseActivity.EXTRA_PICK_USER,true);
        intent.putExtra(BaseActivity.EXTRA_TITLE,"请选择发布人员");
        intent.putParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST,mUserAdapter.getPendingUserList());
        startActivityForResult(intent,REQUESTCODE_SELECT_PEOPLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLogger.LOGD("demo","requestCode:"+requestCode+",resultCode:"+resultCode+",data:"+data);
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == REQUESTCODE_SELECT_PEOPLE){
                mSelectedPeoples = data.getParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST);
                updateUserView();
            }else if(requestCode == REQUESTCODE_SELECT_TASK){
                mSelectedTasks = data.getParcelableArrayListExtra(BaseActivity.EXTRA_DATALIST);
                updateTaskView();
            }

        }
    }


    private void updateUserView(){
        if(mSelectedPeoples!= null){
            mUserAdapter.setPendingList(mSelectedPeoples);
        }
    }

    private void updateTaskView(){
        if(mSelectedTasks != null && mSelectedTasks.size() > 0){
            List<TaskAdapterItem> items = new ArrayList<>();
            for (TaskGroupInfo info : mSelectedTasks){
                AppLogger.LOGD("demo","groupInfo->"+info.toString());
                items.add(TaskAdapterItem.asGroup(info));
            }
            items.add(TaskAdapterItem.asAddItem());
            mTaskAdapter.setItemList(items);
        }
    }


    @OnClick(R.id.repeat)
    public void frequencyClick(View view){
        showPublishTypleView(view);
    }



    private void showPublishTypleView(View parent){
        int padding = getResources().getDimensionPixelOffset(R.dimen.date_picker_dialog_padding);
        View view = getLayoutInflater().inflate(R.layout.layout_frequence,null);
        int heightPixels = LinearLayout.LayoutParams.WRAP_CONTENT;
        int widthPixels = getResources().getDisplayMetrics().widthPixels - padding;
        mSelectPopupWindow = new PopupWindow(view,widthPixels,heightPixels,false);
        mSelectPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioButton always = view.findViewById(R.id.radio_always);
        RadioButton spectal = view.findViewById(R.id.radio_spectial);
        LinearLayout weekGroup = view.findViewById(R.id.week_layout);
        final LinearLayout starLayout = view.findViewById(R.id.start_time);
        final LinearLayout endLayout  =  view.findViewById(R.id.end_time);

        if(publishType == TaskCommon.PUBLISH_TYPE_ALWAYS){
            starLayout.setVisibility(View.GONE);
            endLayout.setVisibility(View.GONE);
            always.setChecked(true);
            spectal.setChecked(false);
        }else{
            starLayout.setVisibility(View.VISIBLE);
            endLayout.setVisibility(View.VISIBLE);
            always.setChecked(false);
            spectal.setChecked(true);
        }
        updateWeeksView(weekGroup);
        always.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    starLayout.setVisibility(View.GONE);
                    endLayout.setVisibility(View.GONE);
                    mRepeatDesc.setText(buttonView.getText().toString());
                    publishType = TaskCommon.PUBLISH_TYPE_ALWAYS;
                }
            }
        });
        spectal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    starLayout.setVisibility(View.VISIBLE);
                    endLayout.setVisibility(View.VISIBLE);
                    mRepeatDesc.setText(buttonView.getText().toString());
                    publishType = TaskCommon.PUBLISH_TYPE_TIME;
                }
            }
        });

        mSelectPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSelectPopupWindow.setOutsideTouchable(true);
        mSelectPopupWindow.setContentView(view);
        mSelectPopupWindow.showAsDropDown(parent);


    }


    private void updateWeeksView(LinearLayout group){
        for (Integer integer : weeks){
            group.getChildAt(integer).setSelected(true);
        }

    }


    public void weekClick(View v){
        AppLogger.LOGD(TAG,"onClick"+v);
        switch (v.getId()){
            case R.id.week_monday:
                updateWeek(v,1);
                break;
            case R.id.week_tuesday:
                updateWeek(v,2);
                break;
            case R.id.week_wednesday:
                updateWeek(v,3);
                break;
            case R.id.week_thursday:
                updateWeek(v,4);
                break;
            case R.id.week_friday:
                updateWeek(v,5);
                break;
            case R.id.week_saturday:
                updateWeek(v,6);
                break;
            case R.id.week_sunday:
                updateWeek(v,0);
                break;
            case R.id.date_start_picker:
                showDatePickerDialog(true);
                break;
            case R.id.time_start_picker:
                showTimePickerDialog();
                break;
            case R.id.date_end_picker:
                showDatePickerDialog(false);
                break;
            case R.id.time_end_picker:
                showTimePickerDialog();
                break;
            case R.id.date_close:
                if(mSelectPopupWindow!= null){
                    mSelectPopupWindow.dismiss();
                }
                break;

        }
    }


    private void updateWeek(View v,int week){
        if(weeks.contains(Integer.valueOf(week))){
            weeks.remove(weeks.indexOf(Integer.valueOf(week)));
            v.setSelected(false);
        }else{
            weeks.add(Integer.valueOf(Integer.valueOf(week)));
            v.setSelected(true);
        }
    }


    private void showDatePickerDialog(final boolean isstart){
        DatePickerDialog dialog = new DatePickerDialog(this);
        dialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                AppLogger.LOGD(TAG,"year:"+year+",month:"+month+",dayOfMonth:"+dayOfMonth);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth);
                long start = calendar.getTimeInMillis();
                if(isstart){
                    startDate = start;
                }else{
                    endDate = start;
                }
            }
        });
        dialog.show();
    }

    private void showTimePickerDialog(){
        TimePickerDialog.OnTimeSetListener timeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                AppLogger.LOGD(TAG,"hourofDay:"+hourOfDay+",minute:"+minute);
                //Calendar.getInstance().setTimeInMillis();
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this,timeSetListener,0,0,true);
        dialog.show();

    }

}
