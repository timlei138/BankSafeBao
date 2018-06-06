package com.android.bsb.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.AppComm;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.component.DaggerHttpComponent;
import com.android.bsb.ui.adapter.FunctinAdapter;
import com.android.bsb.ui.adapter.FunctionItem;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.util.AppLogger;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class UserManagerFragment extends BaseFragment<UserManagerPresenter> implements UserManagerView {

    private final int ACTION_ADDDEPT = 1;
    private final int ACTION_ADDUSER = 2;
    private final int ACTION_LISTDEPT = 3;
    private final int ACTION_LISTUSER = 4;


    @BindView(R.id.grid_view)
    GridView mTableLaout;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_usermanager;
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
            list.add(new FunctionItem(R.string.menu_neworg_title,R.drawable.ic_add,ACTION_ADDDEPT));
        }else if(user.isManager()){
            list.add(new FunctionItem(R.string.menu_neworg_title,R.drawable.ic_add,ACTION_ADDDEPT));
            list.add(new FunctionItem(R.string.menu_newuser_title,R.drawable.ic_add,ACTION_ADDUSER));
        }else if(user.isPostManager()){

        }else if(user.isSecurity()){

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
                case ACTION_ADDDEPT:
                    createAddDialog(ACTION_ADDDEPT);
                    break;
                case ACTION_ADDUSER:
                    createAddDialog(ACTION_ADDUSER);
                    break;
            }

        }
    };

    @Override
    protected void updateView(boolean isRefresh) {

    }

    @Override
    public User getUserInfo() {
        return getLoginUser();
    }


    private void createAddDialog(int action){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final boolean addDept = action == ACTION_ADDDEPT;
        String title = addDept ? "新增机构":"新增人员";
        builder.setTitle(title);
        View rootView = getLayoutInflater().inflate(R.layout.layout_addorg,null);
        builder.setView(rootView);

        final EditText edDept = rootView.findViewById(R.id.ed_dept);
        final EditText eduserName = rootView.findViewById(R.id.ed_user);
        final EditText edPhone = rootView.findViewById(R.id.ed_phone);
        final RadioGroup roleGroup = rootView.findViewById(R.id.role_group);
        final RadioButton radioManager = rootView.findViewById(R.id.radio_manager);
        final RadioButton radioSecurity = rootView.findViewById(R.id.radio_secuity);

        if(addDept){
            roleGroup.setVisibility(View.GONE);
        }else{
            edDept.setVisibility(View.GONE);
        }

        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
                        roleCode = AppComm.ROLE_TYPE_POSTMAN;
                    }else{
                        roleCode = AppComm.ROLE_TYPE_SECURITY;
                    }
                    mPresenter.addUser(name,phone,roleCode);
                }

                dialog.dismiss();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();

    }

}
