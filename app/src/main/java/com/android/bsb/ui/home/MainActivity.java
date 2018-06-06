package com.android.bsb.ui.home;

import android.util.SparseArray;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bsb.R;
import com.android.bsb.bean.User;
import com.android.bsb.component.ApplicationComponent;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.task.TaskManagerFragment;
import com.android.bsb.ui.tasklist.TaskListFragment;
import com.android.bsb.ui.user.UserManagerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        LinearLayout headLayout = (LinearLayout) mNavigationView.getHeaderView(0);

        mHeadIcon = headLayout.findViewById(R.id.head_icon);
        mUserName = headLayout.findViewById(R.id.username);

        mDeptName = headLayout.findViewById(R.id.dept_name);

        mNavigationView.setNavigationItemSelectedListener(this);


        mSparsesTags.put(R.id.nav_tasklist,"taskList");
        mSparsesTags.put(R.id.nav_manageruser,"userManager");
        mSparsesTags.put(R.id.nav_managertask,"taskManager");
        mSparsesTags.put(R.id.nav_setting,"setting");


    }

    @Override
    protected void updateView(boolean isRefresh) {
        mNavigationView.setCheckedItem(R.id.nav_tasklist);
        addFragment(R.id.contentPanel,new TaskListFragment(),"taskList");
        updateToolsBar(R.string.nav_menu_tasklist);
        User user = getLoginUser();
        if(user!=null){
            mUserName.setText(user.getUname());
            mDeptName.setText(user.getDeptName());
        }
        if(user.isAdmin()){
            mNavigationView.getMenu().removeItem(R.id.nav_tasklist);
        }else if(user.isManager()){
            mNavigationView.getMenu().removeItem(R.id.nav_tasklist);
        }else if(user.isPostManager()){

        }else if(user.isSecurity()){

        }


    }

    @Override
    protected void updateToolsBar(int res) {
        String title = getString(res);
        mToolbar.setTitle(title);
    }

    @Override
    protected void updateToolsBar(String title) {
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
                replaceFragment(R.id.contentPanel,new TaskListFragment(),mSparsesTags.get(R.id.nav_tasklist));
                break;
            case R.id.nav_managertask:
                updateToolsBar(R.string.nav_menu_managertask_title);
                replaceFragment(R.id.contentPanel,new TaskManagerFragment(),mSparsesTags.get(R.id.nav_managertask));
                break;
            case R.id.nav_manageruser:
                updateToolsBar(R.string.nav_menu_manageruser_title);
                replaceFragment(R.id.contentPanel,new UserManagerFragment(),mSparsesTags.get(R.id.nav_manageruser));
                break;
            case R.id.nav_setting:
                updateToolsBar(R.string.nav_menu_setting_title);
                //replaceFragment(R.id.contentPanel,new S(),mSparsesTags.get(R.id.nav_manageruser));
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
