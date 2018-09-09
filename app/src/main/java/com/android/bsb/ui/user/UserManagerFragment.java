package com.android.bsb.ui.user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.bsb.AppComm;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerAppComponent;
import com.android.bsb.ui.adapter.FunctinAdapter;
import com.android.bsb.ui.adapter.FunctionItem;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

public class UserManagerFragment extends BaseFragment<UserManagerPresenter> implements UserManagerView {

    private final int ACTION_ADD_DEPT = 1;
    private final int ACTION_ADD_USER = 2;
    private final int ACTION_LIST_DEPT = 3;
    private final int ACTION_LIST_USER = 4;


    private final int PERMISSION_SNED_MSG = 2000;

    @BindView(R.id.grid_view)
    GridView mTableLaout;

    private User pendingUser;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_gridmanager;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerAppComponent.builder().applicationComponent(applicationComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        User user = getLoginUser();
        List<FunctionItem> list = new ArrayList<>();
        if(user.isAdmin()){
            list.add(new FunctionItem(R.string.menu_neworg_title,R.drawable.ic_add,ACTION_ADD_DEPT));
            list.add(new FunctionItem(R.string.menu_querydept_title,R.drawable.ic_add,ACTION_LIST_DEPT));
        }else if(user.isManager()){
            list.add(new FunctionItem(R.string.menu_neworg_title,R.drawable.ic_add,ACTION_ADD_DEPT));
            list.add(new FunctionItem(R.string.menu_newuser_title,R.drawable.ic_add,ACTION_ADD_USER));
            list.add(new FunctionItem(R.string.menu_queryuser_title,R.drawable.ic_add,ACTION_LIST_USER));
        }
        FunctinAdapter adapter = new FunctinAdapter(getContext(),list);
        mTableLaout.setAdapter(adapter);

        mTableLaout.setOnItemClickListener(onItemClickListener);

    }

    private GridView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int action = (int) view.getTag();
            AppLogger.LOGD(null,"action:"+action);
            switch (action){
                case ACTION_ADD_DEPT:
                    createAddDialog(ACTION_ADD_DEPT);
                    break;
                case ACTION_ADD_USER:
                    createAddDialog(ACTION_ADD_USER);
                    break;
                case ACTION_LIST_DEPT:
                    Intent intentdept = new Intent();
                    intentdept.setClass(getContext(),UserListActivity.class);
                    intentdept.putExtra("show_user",false);
                    startActivity(intentdept);
                    break;
                case ACTION_LIST_USER:
                    Intent intentuser = new Intent();
                    intentuser.setClass(getContext(),UserListActivity.class);
                    intentuser.putExtra("show_user",true);
                    startActivity(intentuser);
                    break;
            }

        }
    };

    @Override
    protected void updateView(boolean isRefresh) {

    }

    @Override
    public void addUserDeptSuccess(String reslult) {
        Toast.makeText(getContext(),""+reslult,Toast.LENGTH_SHORT).show();
        sendDownloadMsg();
    }

    @Override
    public void addUserDeptFaild(int code, String e) {
        if(code == 203 || code == 204)
            Toast.makeText(getContext(),""+e,Toast.LENGTH_SHORT).show();
    }

    @Override
    public User getUserInfo() {
        return getLoginUser();
    }

    @Override
    public void showAllDeptInfo(List<User> list) {

    }


    private void createAddDialog(int action){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final boolean addDept = action == ACTION_ADD_DEPT;
        String title = addDept ? "新增机构":"新增人员";
        builder.setTitle(title);
        View rootView = getLayoutInflater().inflate(R.layout.layout_addorg,null);
        builder.setView(rootView);

        final EditText edDept = rootView.findViewById(R.id.ed_dept);
        final EditText eduserName = rootView.findViewById(R.id.ed_user);
        final EditText edPhone = rootView.findViewById(R.id.ed_phone);
        final RadioGroup roleGroup = rootView.findViewById(R.id.role_group);
        if(addDept){
            roleGroup.setVisibility(View.GONE);
        }else{
            edDept.setVisibility(View.GONE);
        }

        pendingUser = new User();
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  dept = edDept.getText().toString();
                String name = eduserName.getText().toString();
                String phone =edPhone.getText().toString();
                if((addDept && TextUtils.isEmpty(dept)) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)){
                    Toast.makeText(getContext(), "argment is null ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(addDept){
                    pendingUser.setRole(AppComm.ROLE_TYPE_MANAGER);
                    mPresenter.addNewDept(dept,phone,name);
                }else{
                    int id = roleGroup.getCheckedRadioButtonId();
                    int roleCode;
                    if(id == R.id.radio_manager){
                        roleCode = 12;
                    }else{
                        roleCode = 13;
                    }
                    pendingUser.setRole(id);
                    mPresenter.addUser(name,phone,roleCode);
                }
                pendingUser.setUname(name);
                pendingUser.setCellpahone(phone);

                dialog.dismiss();

            }
        });
        builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();

    }




    void sendDownloadMsg(){

        if(getContext().checkCallingOrSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},PERMISSION_SNED_MSG);
            return;
        }

        if(pendingUser != null){
            String message = "用户您好，您已经被授权使用本软件，软件下载：http://www.baidu.com "
                    +"您好登陆名为："+pendingUser.getCellpahone()+",您的登陆密码为：123456 "
                    +"。";

            SmsManager smsManager = android.telephony.SmsManager.getDefault();

            List<String> msgList = smsManager.divideMessage(message);


            String SENT_SMS_ACTION = "SENT_SMS_ACTION";
            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent sentPI= PendingIntent.getBroadcast(getContext(), 0, sentIntent,
                    0);
            // register the Broadcast Receivers
            getContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    switch (getResultCode()) {
                        case RESULT_OK:
                            Toast.makeText(getContext(),
                                    "短信发送成功", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
            }, new IntentFilter(SENT_SMS_ACTION));

            for (String msg : msgList){
                smsManager.sendTextMessage(pendingUser.getCellpahone(),null,msg,sentPI,null);
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            sendDownloadMsg();
        }
    }
}
