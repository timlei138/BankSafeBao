package com.android.bsb.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.FunctinAdapter;
import com.android.bsb.ui.adapter.FunctionItem;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class UserManagerFragment extends BaseFragment<UserManagerPresenter> implements UserManagerView {

    private final int ACTION_ADD_DEPT = 1;
    private final int ACTION_ADD_USER = 2;
    private final int ACTION_LIST_DEPT = 3;
    private final int ACTION_LIST_USER = 4;



    @BindView(R.id.grid_view)
    GridView mTableLaout;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_gridmanager;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        DaggerHttpComponent.builder().applicationComponent(applicationComponent).build().inject(this);
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
                    mPresenter.addNewDept(dept,phone,name);
                }else{
                    int id = roleGroup.getCheckedRadioButtonId();
                    int roleCode;
                    if(id == R.id.radio_manager){
                        roleCode = 12;
                    }else{
                        roleCode = 13;
                    }
                    mPresenter.addUser(name,phone,roleCode);
                }

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

}
