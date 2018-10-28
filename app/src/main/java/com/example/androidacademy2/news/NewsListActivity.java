package com.example.androidacademy2.news;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidacademy2.AboutActivity;
import com.example.androidacademy2.LoadNews;
import com.example.androidacademy2.R;
import com.example.androidacademy2.data_news.NewsItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class NewsListActivity extends AppCompatActivity {
    @Nullable
    private AsyncTask<Long, Void, List<NewsItem>> asyncTask;
    public static final String LOG = "My_Log";
    Button tryButton, categoryButton;
    TextView text;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String category;

    public List<NewsItem> news;

    private final NewsRecyclerAdapter.OnItemClickListener clickListener = news ->
    {
        Intent newsDetailsActivityIntent = new Intent(this, NewsDetailsActivity.class);
        newsDetailsActivityIntent.putExtra("url", news.getUrl());
        newsDetailsActivityIntent.putExtra("name", news.getTitle());
        startActivity(newsDetailsActivityIntent);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        tryButton = findViewById(R.id.button_try_again);
        categoryButton = findViewById(R.id.button_category);
        tryButton.setOnClickListener(v -> {
            Log.d(LOG, "Try connect");
            visibleProgress();
            abc(clickListener);

        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
                        "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};
                builder = new AlertDialog.Builder(NewsListActivity.this);
                builder.setTitle("Choose category").setCancelable(false)
                        // добавляем переключатели
                        .setSingleChoiceItems(categories, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int item) {
                                        category = categories[item];
                                        categoryButton.setText(category);
                                        dialog.cancel();
                                        Log.d(LOG, "Change category");
                                        visibleProgress();
                                        abc(clickListener);
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        text = findViewById(R.id.text_complete);
        recyclerView = findViewById(R.id.recycler_news);
        progressBar = findViewById(R.id.progressBar_news);
        category = "food";
        categoryButton.setText(category);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG, "Application start");
        asyncTask = new LoadNews(this, clickListener, category);
        asyncTask.execute(2000L);
       /* try {
            news = asyncTask.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }*/
    }

    public void abc(NewsRecyclerAdapter.OnItemClickListener click) {
        asyncTask = new LoadNews(this, click, category);
        asyncTask.execute(2000L);
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

    private void visibleProgress() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tryButton.setVisibility(View.GONE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(false);
    }
}
