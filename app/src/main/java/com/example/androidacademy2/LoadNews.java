package com.example.androidacademy2;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoadNews extends AsyncTask<Long, Void, List<NewsItem>> {
    @NonNull
    private final WeakReference<Activity> activityRef;

    LoadNews(Activity activity) {
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
            ProgressBar progressBar = activity.findViewById(R.id.progressBar_news);
            progressBar.setVisibility(View.GONE);

           /* TextView textView = activity.findViewById(R.id.text_complete);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.load_complete);*/
            super.onPostExecute(news);
        }
    }
}
