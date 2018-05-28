package com.android.bsb.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.bsb.R;
import com.android.bsb.ui.tasklist.DoneTaskFragment;
import com.android.bsb.ui.tasklist.ErrorTaskFragment;
import com.android.bsb.ui.tasklist.UnDoneTaskFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskListPageAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragmentList = new ArrayList<>(3);


    private String[] titles;

    public TaskListPageAdapter(FragmentManager fm,String[] titles) {
        super(fm);
        mFragmentList.add(new UnDoneTaskFragment());
        mFragmentList.add(new DoneTaskFragment());
        mFragmentList.add(new ErrorTaskFragment());
        this.titles = titles;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
