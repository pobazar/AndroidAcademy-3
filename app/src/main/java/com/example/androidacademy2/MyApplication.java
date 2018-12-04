package com.example.androidacademy2;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.androidacademy2.Service.NetworkUtils;
import com.example.androidacademy2.Service.UploadWork;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class MyApplication extends Application {
    private static MyApplication sMyApplication;
    private static final String LOG = "My_Log";
    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;

        Constraints myConstraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();
        WorkRequest workRequest = new PeriodicWorkRequest.Builder(UploadWork.class,3, TimeUnit.HOURS)
                .setConstraints(myConstraints)
                .build();
        WorkManager.getInstance().enqueue(workRequest);


        registerReceiver(NetworkUtils.sNetworkUtils.getReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    public static Context getContext() {
        return sMyApplication;
    }

}
