package com.android.bsb.ui.setting;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.android.bsb.AppApplication;
import com.android.bsb.R;
import com.android.bsb.ui.home.MainActivity;
import com.android.bsb.ui.login.LoginActivity;
import com.android.bsb.ui.login.SplashActivity;
import com.android.bsb.util.SharedProvider;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    Preference mClearDataPref;
    Preference mLoginOutPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        mClearDataPref = findPreference("pref_clear");
        mLoginOutPref = findPreference("pref_loginout");

        mClearDataPref.setOnPreferenceClickListener(this);
        mLoginOutPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if(preference.getKey().equals("pref_clear")){

        }else if(preference.getKey().equals("pref_loginout")){
            showExitUserDialog();
        }
        return false;
    }


    private void showExitUserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("警告");
        builder.setMessage("请确保数据已经同步，退出当前帐号可能会造成数据丢失情况！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new clearUserData().execute();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false).create().show();
    }


    private class clearUserData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            SharedProvider sp = SharedProvider.getInstance(getActivity().getBaseContext());
            sp.clearLoginInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(getActivity().getBaseContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(getActivity().getBaseContext(), 1000,
                                        intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getActivity().getBaseContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, pi);
            // Kill process
            AppApplication.getAppActivityManager().finishAllActivity();
        }


    }

}
