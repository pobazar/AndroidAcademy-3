package com.example.androidacademy2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class LoadNews extends AsyncTask<Long, Void, List<NewsItem>> {
    @NonNull
    private final WeakReference<Activity> activityRef;

    private final NewsRecyclerAdapter.OnItemClickListener clickListener;


    LoadNews(Activity activity, NewsRecyclerAdapter.OnItemClickListener clk) {
        activityRef = new WeakReference<>(activity);
        clickListener = clk;
    }

    @Override
    protected List<NewsItem> doInBackground(Long... waitTime) {
        List<NewsItem> news;
        news = DataUtils.generateNews();
        try {
            Thread.sleep(waitTime[0]);
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


            RecyclerView recyclerView = activity.findViewById(R.id.recycler_news);
            recyclerView.setVisibility(View.VISIBLE);
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(activity, news, clickListener));
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);
            } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(activity, news, clickListener));
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);

                super.onPostExecute(news);
            }
        }
    }
}
