package com.example.androidacademy2;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class NewsListActivity extends AppCompatActivity {
    @Nullable
    private AsyncTask<Long, Void, List<NewsItem>> asyncTask;

    public List<NewsItem> news;

    private final NewsRecyclerAdapter.OnItemClickListener clickListener = news ->
    {
        Intent newsDetailsActivityIntent = new Intent(this, NewsDetailsActivity.class);
        newsDetailsActivityIntent.putExtra("name", news.getCategory().getName());
        newsDetailsActivityIntent.putExtra("title", news.getTitle());
        newsDetailsActivityIntent.putExtra("full", news.getFullText());
        newsDetailsActivityIntent.putExtra("date", news.getPublishDate() + "");
        newsDetailsActivityIntent.putExtra("image", news.getImageUrl());
        startActivity(newsDetailsActivityIntent);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView recyclerView = findViewById(R.id.recycler_news);

        asyncTask = new LoadNews(this, clickListener);
        asyncTask.execute(5000L);
       /* try {
            news = asyncTask.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_button_menu:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.news_button_menu:
                startActivity(new Intent(this, NewsListActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
