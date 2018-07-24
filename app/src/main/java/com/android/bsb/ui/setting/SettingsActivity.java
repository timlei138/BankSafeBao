package com.android.bsb.ui.setting;

import android.app.Activity;
import android.support.v7.widget.Toolbar;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.base.BaseActivity;

import butterknife.BindView;


public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolsBar;

    @Override
    protected int attachLayoutRes() {
        return R.layout.layout_settings;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {
        AppApplication.getAppActivityManager().addActivity(this);
    }

    @Override
    protected void initView() {
        mToolsBar.setTitle("设置");
        setSupportActionBar(mToolsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.setting_content,new SettingsFragment()).commit();

    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }
}
