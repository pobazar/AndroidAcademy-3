package com.example.androidacademy2;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.androidacademy2.news.NewsListFragment;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static androidx.work.ListenableWorker.Result.SUCCESS;


public class UploadWork extends Worker {
    private static final String LOG = "My_Log";

    Context context;

    public UploadWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
       // Log.d(LOG, "constructor worker");
    }


    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(context, DownloadService.class);

        if (Build.VERSION.SDK_INT > 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        return Result.SUCCESS; // FAILURE,
        //	RETRY
    }

}
