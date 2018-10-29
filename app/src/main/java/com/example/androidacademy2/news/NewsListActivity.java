package com.example.androidacademy2.news;


import android.content.Intent;
import android.content.res.Configuration;
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
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.R;
import com.example.androidacademy2.data_news.NewsItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class NewsListActivity extends AppCompatActivity {
    @Nullable
    private AsyncTask<Long, Void, List<NewsItem>> asyncTask;
    public static final String LOG = "My_Log";
    Button tryButton, categoryButton;
    TextView text;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String category;
    Disposable disposable;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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
            loadItems();
        });

        categoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
                    "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};
            builder = new AlertDialog.Builder(NewsListActivity.this);
            builder.setTitle("Choose category").setCancelable(false)
                    // добавляем переключатели
                    .setSingleChoiceItems(categories, -1,
                            (dialog, item) -> {
                                category = categories[item];
                                categoryButton.setText(category);
                                dialog.cancel();
                                Log.d(LOG, "Change category");
                                visibleProgress();
                                loadItems();
                            });
            AlertDialog alert = builder.create();
            alert.show();
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
        loadItems();
    }

    public void loadItems() {
        Log.d(LOG, "start rx load news");
        visibleProgress();
      /*  disposable = io.reactivex.Observable.just(LoadNews.downloadNews(category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNews, this::visibleError);*/
        final Disposable searchDisposable = Network.getInstance()
                .news()
                // .search(category)
                .search()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNews, this::visibleError);
        Log.d(LOG, "rx load news canel");
        compositeDisposable.add(searchDisposable);
    }

    public void showNews(@NonNull Response<List<NewsDTO>> response) {
        visibleRecycler();
        Gson gson = new Gson();
        String gsonResponse = response.body() + "";
        NewsResponse newsResponse = gson.fromJson(gsonResponse, NewsResponse.class);
        List<NewsDTO> newsdto = newsResponse.getData();
        news = dtotonews(newsdto);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setAdapter(new NewsRecyclerAdapter(this, news, clickListener));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setAdapter(new NewsRecyclerAdapter(this, news, clickListener));
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable = null;
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

    private void visibleRecycler() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.GONE);
        tryButton.setEnabled(false);
        categoryButton.setEnabled(true);
    }

    private void visibleError(Throwable th) {
        text.setVisibility(View.VISIBLE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.VISIBLE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(true);
        Log.e(LOG, th.getMessage(), th);
    }

    static private List<NewsItem> dtotonews(List<NewsDTO> listdto) {
        List<NewsItem> news = new ArrayList<>();
        for (NewsDTO x : listdto) {
            String image = "";
            for (MultimediaDTO y : x.getMultimedia()) {
                if (y.getFormat().equals("Standard Thumbnail")) {
                    image = y.getUrl();
                    break;
                }
            }
            news.add(new NewsItem(x.getTitle(), image, x.getSection(), x.getPublishedDate().replace('T', ' '), x.getAbstract1(), "", x.getUrl()));
        }
        return news;
    }
}
