package com.android.bsb.component;

import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.login.SplashActivity;
import com.android.bsb.ui.task.AddTaskGroupOrTaskActivity;
import com.android.bsb.ui.tasklist.TaskManagerListActivity;
import com.android.bsb.ui.user.ManagerUserListActivity;
import com.android.bsb.ui.user.UserManagerFragment;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(LoginActivity activity);

    void inject(SplashActivity activity);

    void inject(AddTaskGroupOrTaskActivity activity);

    void inject(UserManagerFragment fragment);

    void inject(ManagerUserListActivity activity);

    void inject(TaskManagerListActivity activity);

}
