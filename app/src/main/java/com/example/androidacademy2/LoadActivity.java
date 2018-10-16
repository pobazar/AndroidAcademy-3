package com.example.androidacademy2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoadActivity extends AppCompatActivity {

    @Nullable
    private AsyncTask<Long, Void, List<NewsItem>> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ProgressBar progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        asyncTask = new LoadingAsyncTask(this);
        asyncTask.execute(1000L);
        try {
            List<NewsItem> news = LoadingAsyncTask.get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }


    public void OpenActivityNews(List<NewsItem> news) {
        Intent newsListActivityIntent = new Intent(this, NewsListActivity.class);
        //newsListActivityIntent.putExtra("newsList", news);
        startActivity(newsListActivityIntent);
    }

//---------------------------------------------------------------------------------------------------------------------------------------

    private static class LoadingAsyncTask extends AsyncTask<Long, Void, List<NewsItem>> {
        @NonNull
        private final WeakReference<Activity> activityRef;

        LoadingAsyncTask(Activity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected List<NewsItem> doInBackground(Long... waitTime) {
            List<NewsItem> news;
            news = DataUtils.generateNews();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return news;
        }

        @Override
        protected void onPostExecute(List<NewsItem> news) {
            Activity activity = activityRef.get();
            if (activity != null) {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                TextView textView = activity.findViewById(R.id.text_load);
                textView.setText("Загрузка завершена");
                super.onPostExecute(news);
            }
        }
    }
}





