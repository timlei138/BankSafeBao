package com.android.bsb.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.bsb.AppComm;
import com.android.bsb.R;
import com.android.bsb.ui.base.BaseActivity;
import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.util.SharedProvider;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedProvider provider = SharedProvider.getInstance(getApplicationContext());

        Intent intent = new Intent();
        if(provider.getBoolValue(AppComm.KEY_FIRST_USED,true)){
            intent.setClass(this, LoginActivity.class);
        }else{

            intent.setClass(this,MainActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

    }
}
