package com.android.bsb.ui.user;

import com.android.bsb.bean.User;
import com.android.bsb.ui.base.IBaseView;

import java.util.List;

public interface UserManagerView extends IBaseView{

    void addUserDeptSuccess(String reslult);

    void addUserDeptFaild(int code,String e);

    User getUserInfo();

    void showAllDeptInfo(List<User> list);

}
