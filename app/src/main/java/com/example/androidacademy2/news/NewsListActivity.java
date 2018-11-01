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
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsDao;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.R;
import com.example.androidacademy2.data_news.NewsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton loadButton;
    TextView text;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String category;
    //private NewsRepository newsRepository;
    private AppDatabase db;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    static private final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
            "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};

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
        loadButton = findViewById(R.id.button_load_news);
        tryButton.setOnClickListener(v -> {
            Log.d(LOG, "Try connect");
            visibleProgress();
            loadItems();
        });

        categoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(NewsListActivity.this);
            builder.setTitle("Choose category").setCancelable(false)
                    // добавляем переключатели
                    .setSingleChoiceItems(categories, checkitem(category),
                            (dialog, item) -> {
                                category = categories[item];
                                categoryButton.setText(category);
                                dialog.cancel();
                                Log.d(LOG, "Change category");
                                //visibleProgress();
                                //loadItems();
                            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        loadButton.setOnClickListener(v -> {
            Log.d(LOG, "float button onclick");
            loadItems();
        });

        text = findViewById(R.id.text_complete);
        recyclerView = findViewById(R.id.recycler_news);
        progressBar = findViewById(R.id.progressBar_news);
        category = "food";
        categoryButton.setText(category);

        db = AppDatabase.getAppDatabase(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG, "Application start");
        //loadItems();
    }

    public void loadItems() {
        Log.d(LOG, "start rx load news");
        visibleProgress();
        final Disposable searchDisposable = Network.getInstance()
                .news()
                .search(category)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::dtoResponseToDao)
               // .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveNews, this::visibleError);
        Log.d(LOG, "rx load news canel");
        compositeDisposable.add(searchDisposable);
    }

    public void showNews(List<NewsItem> news) {
        visibleRecycler();
        //Gson gson = new Gson();
        // String gsonResponse = response.body()+"";
        //NewsResponse newsResponse = gson.fromJson(gsonResponse, NewsResponse.class);
        //List<NewsDTO> newsdto = response.getData();

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

    public void saveNews(NewsEntity[] newsEntities)
    {
            db.newsDao().deleteAll();
            db.newsDao().insertAll(newsEntities);
        Log.d(LOG, "save news to DB complete");
    }

    public List<NewsEntity> getNews()
    {
        db = AppDatabase.getAppDatabase(this);
        return db.newsDao().getAll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
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

    private List<NewsItem> dtoToNews(List<NewsDTO> listdto) {
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

    private List<NewsItem> daoToNews(List<NewsEntity> newsEntities) {
        List<NewsItem> news = new ArrayList<>();
        for (NewsEntity x : newsEntities) {
            news.add(new NewsItem(x.getTitle(), x.getImageUrl(), x.getCategory(), x.getPublishDate(), x.getPreviewText(), x.getFullText(), x.getUrl()));
        }
        return news;
    }

    private int checkitem(String s) {
        int i=0;
        for (String x : categories) {
            if (x==s) return i;
            else i++;
        }
        return -1;
    }
}
