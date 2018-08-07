package com.android.bsb.component;

import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.login.SplashActivity;
import com.android.bsb.ui.task.AddTaskGroupOrTaskActivity;
import com.android.bsb.ui.task.CheckUsersFragment;
import com.android.bsb.ui.task.ErrorTaskEditorActivity;
import com.android.bsb.ui.task.PublishTaskActivity;
import com.android.bsb.ui.task.TaskFragment;
import com.android.bsb.ui.task.TaskGroupListActivity;
import com.android.bsb.ui.user.UserListActivity;
import com.android.bsb.ui.user.UserManagerFragment;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface AppComponent {

    void inject(LoginActivity activity);

    void inject(SplashActivity activity);

    void inject(AddTaskGroupOrTaskActivity activity);

    void inject(UserManagerFragment fragment);

    void inject(TaskFragment fragment);

    void inject(UserListActivity activity);

    void inject(TaskGroupListActivity activity);

    void inject(PublishTaskActivity activity);

    void inject(ErrorTaskEditorActivity activity);

    void inject(CheckUsersFragment fragment);

}
