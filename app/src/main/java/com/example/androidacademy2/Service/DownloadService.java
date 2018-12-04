package com.example.androidacademy2.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadService extends Service {

    private Disposable downloadDisposable;
    private static final String LOG = "My_Log";
    public static final String CHANNEL_ID = "UPDATE_NEWS_CHANNEL";
    public static final int ID1 = 12345;
    public static final int ID2 = 12331245;
    AppDatabase db;
    String category;
    PendingIntent resultPendingIntent;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "Service create");
        //Создание интента
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        createChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.download_news))
                .setContentText(getString(R.string.download_progress))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        startForeground(ID1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG, "Service onStart");
        if (MainActivity.category == null) {
            category = "food";
        } else {
            category = MainActivity.category;
        }
        downloadDisposable = NetworkUtils.sNetworkUtils.getOnlineNetwork()
                .timeout(1, TimeUnit.MINUTES).flatMap(aLong ->
                        Network.getInstance()
                                .news()
                                .search(category))
                .map(this::dtoResponseToDao)
                .doOnSuccess(this::saveNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::completeLoad, this::failedLoad);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG, "Service destroy");
        if (downloadDisposable != null && !downloadDisposable.isDisposed()) {
            downloadDisposable.dispose();
        }
    }

    private NewsEntity[] dtoResponseToDao(@NonNull NewsResponse response) {
        List<NewsDTO> listdto = response.getData();
        NewsEntity[] news = new NewsEntity[listdto.size()];
        int i = 0;
        for (NewsDTO x : listdto) {
            NewsEntity nn = new NewsEntity();
            String image = "";
            for (MultimediaDTO y : x.getMultimedia()) {
                if (y.getFormat().equals("Standard Thumbnail")) {
                    image = y.getUrl();
                    break;
                }
            }
            nn.setCategory(x.getSection());
            nn.setFullText("");
            nn.setImageUrl(image);
            nn.setPreviewText(x.getAbstract1());
            nn.setPublishDate(x.getPublishedDate().replace('T', ' '));
            nn.setTitle(x.getTitle());
            nn.setUrl(x.getUrl());
            news[i] = nn;
            i++;
        }
        return news;
    }

    private void saveNews(NewsEntity[] newsEntities) {
        db = AppDatabase.getAppDatabase(getBaseContext());
        db.newsDao().deleteAll();
        db.newsDao().insertAll(newsEntities);
        Log.d(LOG, "save " + newsEntities.length + " news to DB");
    }

    private void completeLoad(NewsEntity[] newsEntities) {
        Log.d(LOG, "download " + newsEntities.length + " news in service");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID2, showNotification(getString(R.string.download_complete)));
          stopSelf();
    }

    private void failedLoad(Throwable th) {
        Log.d(LOG, "download news failed in service: " + th);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID2, showNotification(getString(R.string.download_failed)));
          stopSelf();
    }

    public Notification showNotification(String s) {
        //Создание уведомления
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.download_news))
                .setContentText(s)
                .setPriority(NotificationCompat.FLAG_AUTO_CANCEL)
                .setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }

    public void createChannel() {
        //Создание канала
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
