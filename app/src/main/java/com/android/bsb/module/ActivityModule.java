package com.android.bsb.module;

import android.app.Activity;

import com.android.bsb.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final Activity mActivity ;

    public ActivityModule(Activity activity){
        mActivity = activity;
    }


    @PerActivity
    @Provides
    Activity getActivity(){
        return mActivity;
    }

}
