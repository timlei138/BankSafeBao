package com.android.bsb.component;

import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.login.SplashActivity;
import com.android.bsb.ui.task.AddTaskGroupOrTaskActivity;
import com.android.bsb.ui.task.ErrorTaskEditorActivity;
import com.android.bsb.ui.task.PublishTaskActivity;
import com.android.bsb.ui.tasklist.TaskFragment;
import com.android.bsb.ui.tasklist.TaskListFragment;
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

    void inject(TaskFragment fragment);

    void inject(ManagerUserListActivity activity);

    void inject(TaskManagerListActivity activity);

    void inject(PublishTaskActivity activity);

    void inject(ErrorTaskEditorActivity activity);

}
