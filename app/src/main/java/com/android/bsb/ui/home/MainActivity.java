package com.android.bsb.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.base.BaseFragment;
import com.android.bsb.ui.setting.SettingsActivity;
import com.android.bsb.ui.task.CheckUsersFragment;
import com.android.bsb.ui.task.TaskManagerFragment;
import com.android.bsb.ui.task.TaskFragment;
import com.android.bsb.ui.task.TaskListFragment;
import com.android.bsb.ui.user.UserManagerFragment;
import com.android.bsb.util.AppLogger;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainPersenter>
        implements NavigationView.OnNavigationItemSelectedListener,MainView {

    @BindView(R.id.contentPanel)
    FrameLayout mContentPanel;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    //header layout
    ImageView mHeadIcon;
    TextView mUserName;
    TextView mDeptName;

    private String TAG = getClass().getSimpleName();

    private SparseArray<String> mSparsesTags = new SparseArray<>();

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initInjector(ApplicationComponent applicationComponent) {

    }

    @Override
    protected void initView() {
        AppApplication.getAppActivityManager().addActivity(this);
        initMainView();
        initFragment();
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(mToolsMenuItemClickListener);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected Activity addActivityStack() {
        return this;
    }

    private Toolbar.OnMenuItemClickListener mToolsMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            AppLogger.LOGD("demo","fragmentSize:"+fragments.size());
            for (Fragment fragment : fragments){
                AppLogger.LOGD("demo","fragment:"+fragment);
                if(fragment instanceof BaseFragment){
                    ((BaseFragment) fragment).actionBarItemClick(item);
                }
            }
            return false;
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolsbar,menu);
        if(isAdmin() || isManager() || isPushlish()){
            menu.removeItem(R.id.action_sync);
            menu.removeItem(R.id.action_edit);
        }else if(isSecurity()){

        }
        return true;
    }

    @Override
    protected void updateView(boolean isRefresh) {

    }

    void initMainView(){
        mSparsesTags.put(R.id.nav_tasklist,"taskList");
        mSparsesTags.put(R.id.nav_manageruser,"userManager");
        mSparsesTags.put(R.id.nav_managerdept,"deptManager");
        mSparsesTags.put(R.id.nav_managertask,"taskManager");
        mSparsesTags.put(R.id.nav_taskcheck,"taskCheck");
        LinearLayout headLayout = (LinearLayout) mNavigationView.getHeaderView(0);
        mHeadIcon = headLayout.findViewById(R.id.head_icon);
        mUserName = headLayout.findViewById(R.id.username);
        mDeptName = headLayout.findViewById(R.id.dept_name);
        mNavigationView.setNavigationItemSelectedListener(this);

        for(int i=0 ; i < mSparsesTags.size();i++){
            AppLogger.LOGD(null,"1----key:"+mSparsesTags.keyAt(i)+",value:"+mSparsesTags.valueAt(i));
        }
        User user = getLoginUser();
        if(user == null){
            return;
        }

        if(user!=null){
            mUserName.setText(user.getUname());
            mDeptName.setText(user.getDeptName());
        }
        if(user.isAdmin()){
            mNavigationView.getMenu().removeItem(R.id.nav_tasklist);
            mNavigationView.getMenu().removeItem(R.id.nav_manageruser);
            mNavigationView.getMenu().removeItem(R.id.nav_managertask);
            mNavigationView.getMenu().removeItem(R.id.nav_taskcheck);
            mSparsesTags.remove(R.id.nav_tasklist);
            mSparsesTags.remove(R.id.nav_managertask);
            mSparsesTags.remove(R.id.nav_manageruser);
            mSparsesTags.remove(R.id.nav_taskcheck);
        }else if(user.isManager()){
            mNavigationView.getMenu().removeItem(R.id.nav_tasklist);
            mNavigationView.getMenu().removeItem(R.id.nav_managerdept);
            mNavigationView.getMenu().removeItem(R.id.nav_managertask);
            mSparsesTags.remove(R.id.nav_tasklist);
            mSparsesTags.remove(R.id.nav_managertask);
            mSparsesTags.remove(R.id.nav_managerdept);
        }else if(user.isPostManager()){
            mNavigationView.getMenu().removeItem(R.id.nav_tasklist);
            mNavigationView.getMenu().removeItem(R.id.nav_manageruser);
            mNavigationView.getMenu().removeItem(R.id.nav_managerdept);
            mSparsesTags.remove(R.id.nav_tasklist);
            mSparsesTags.remove(R.id.nav_manageruser);
            mSparsesTags.remove(R.id.nav_managerdept);
        }else if(user.isSecurity()){
            mNavigationView.getMenu().removeItem(R.id.nav_manageruser);
            mNavigationView.getMenu().removeItem(R.id.nav_managerdept);
            mNavigationView.getMenu().removeItem(R.id.nav_taskcheck);
            mNavigationView.getMenu().removeItem(R.id.nav_managertask);
            mSparsesTags.remove(R.id.nav_managerdept);
            mSparsesTags.remove(R.id.nav_manageruser);
            mSparsesTags.remove(R.id.nav_taskcheck);
            mSparsesTags.remove(R.id.nav_managertask);
        }
    }

    void initFragment(){
        int firstFragmentKey = mSparsesTags.keyAt(0);
        int titleRes = R.string.app_name;
        BaseFragment fragment = null;
        AppLogger.LOGD(TAG,"first key:"+firstFragmentKey+"value:"+mSparsesTags.valueAt(0));
        switch (firstFragmentKey){
            case R.id.nav_tasklist:
                fragment = isSecurity() ? new TaskFragment() : new TaskListFragment();
                titleRes = R.string.nav_menu_tasklist;
                break;
            case R.id.nav_manageruser:
            case R.id.nav_managerdept:
                fragment = new UserManagerFragment();
                titleRes = isAdmin() ? R.string.nav_menu_managerdept_title
                        :R.string.nav_menu_manageruser_title;
                break;
            case R.id.nav_managertask:
                fragment = new TaskManagerFragment();
                titleRes = R.string.nav_menu_managertask_title;
                break;

        }
        mNavigationView.setCheckedItem(mSparsesTags.keyAt(0));
        addFragment(R.id.contentPanel,fragment,mSparsesTags.valueAt(0));
        updateToolsBar(titleRes);

    }

    protected void updateToolsBar(int res) {
        String title = getString(res);
        mToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        int stackEntryCount  = getSupportFragmentManager().getBackStackEntryCount();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else if(stackEntryCount ==1){
            exitApp();
        } else {
            String tagName = getSupportFragmentManager().getBackStackEntryAt(stackEntryCount -2).getName();
            int index = mSparsesTags.indexOfValue(tagName);
            mNavigationView.setCheckedItem(mSparsesTags.keyAt(index));
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if(item.isChecked()){
            return true;
        }
        updateHomePage(item.getItemId());
        return true;
    }

    void updateHomePage(int itemId){
        switch (itemId){
            case R.id.nav_tasklist:
                updateToolsBar(R.string.nav_menu_tasklist);
                replaceFragment(R.id.contentPanel,isSecurity() ? new TaskFragment() :  new TaskListFragment(),mSparsesTags.get(R.id.nav_tasklist));
                break;
            case R.id.nav_managertask:
                updateToolsBar(R.string.nav_menu_managertask_title);
                replaceFragment(R.id.contentPanel,new TaskManagerFragment(),mSparsesTags.get(R.id.nav_managertask));
                break;
            case R.id.nav_managerdept:
            case R.id.nav_manageruser:
                updateToolsBar(isAdmin() ? R.string.nav_menu_managerdept_title : R.string.nav_menu_manageruser_title);
                replaceFragment(R.id.contentPanel,new UserManagerFragment(),mSparsesTags.get(R.id.nav_manageruser));
                break;
            case R.id.nav_setting:
                updateToolsBar(R.string.nav_menu_setting_title);
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_taskcheck:
                updateToolsBar(R.string.nav_menu_taskcheck_title);
                replaceFragment(R.id.contentPanel,new CheckUsersFragment(),mSparsesTags.get(R.id.nav_taskcheck));
                break;
        }
    }

    private long mExitTimeStamp  = 0;
    private void exitApp(){
        if(System.currentTimeMillis() - mExitTimeStamp > 2000){
            Toast.makeText(this,"在按一次退出",Toast.LENGTH_SHORT).show();
            mExitTimeStamp = System.currentTimeMillis();
        }else{
            finish();
        }
    }

}
