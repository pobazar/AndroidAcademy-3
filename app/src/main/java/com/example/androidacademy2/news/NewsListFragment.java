package com.example.androidacademy2.news;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidacademy2.AboutActivity;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.R;
import com.example.androidacademy2.data_news.NewsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class NewsListFragment extends Fragment {
    @Nullable
    private AsyncTask<Long, Void, List<NewsItem>> asyncTask;
    private static final String LOG = "My_Log";
    private Button tryButton, categoryButton;
    private TextView text;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String category;
    //private NewsRepository newsRepository;
    private AppDatabase db;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    static private final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
            "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};

    public List<NewsItem> news;
    private Context context;
    private NewsFragmentListener listener;

    private final NewsRecyclerAdapter.OnItemClickListener clickListener = news ->
    {
      /* Intent newsDetailsActivityIntent = new Intent(context, NewsDetailsFragment.class);
        newsDetailsActivityIntent.putExtra("url", news.getUrl());
        startActivity(newsDetailsActivityIntent);*/

        listener.onNewsDetailsClicked(news.getUrl());
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NewsFragmentListener) {
            listener = (NewsFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_news_list, container, false);
        context = getContext();

        Log.d(LOG, "OnCreate");
        tryButton = view.findViewById(R.id.button_try_again);
        categoryButton = view.findViewById(R.id.button_category);
        FloatingActionButton loadButton = view.findViewById(R.id.button_load_news);
        tryButton.setOnClickListener(v -> {
            Log.d(LOG, "Try connect");
            visibleProgress();
            loadItems();
        });

        categoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
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
                                final Disposable Disposable1 = deleteNews()
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();

                                compositeDisposable.add(Disposable1);

                            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        loadButton.setOnClickListener(v -> {
            Log.d(LOG, "float button onclick");
            loadItems();
        });

        text = view.findViewById(R.id.text_complete);
        recyclerView = view.findViewById(R.id.recycler_news);
        progressBar = view.findViewById(R.id.progressBar_news);
        category = "food";
        categoryButton.setText(category);

        db = AppDatabase.getAppDatabase(context);

        updateNews();

        MainActivity.f = 0;

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        Log.d(LOG, "Application start");
        //loadItems();
    }


    private void loadItems() {
        Log.d(LOG, "start rx load news");
        visibleProgress();
        final Disposable searchDisposable = Network.getInstance()
                .news()
                .search(category)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::dtoResponseToDao)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::completeLoad, this::visibleError);
        compositeDisposable.add(searchDisposable);
    }

    private void updateNews() {
        final Disposable newsRoomDisposable = getNews()
                .map(this::daoToNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNews, this::visibleError);
        compositeDisposable.add(newsRoomDisposable);
    }

    private void showNews(List<NewsItem> news) {
        visibleRecycler();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setAdapter(new NewsRecyclerAdapter(context, news, clickListener));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setAdapter(new NewsRecyclerAdapter(getContext(), news, clickListener));
            if (MainActivity.isTwoPanel) {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            }
            else
            {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    private void completeLoad(NewsEntity[] newsEntities) {
        Log.d(LOG, "download " + newsEntities.length + " news");
    }

    private void saveNews(NewsEntity[] newsEntities) {
        db.newsDao().deleteAll();
        db.newsDao().insertAll(newsEntities);
        Log.d(LOG, "save " + newsEntities.length + " news to DB");
    }

    public Observable<List<NewsEntity>> getNews() {
        db = AppDatabase.getAppDatabase(context);
        return db.newsDao().getAll();
    }

    public Observable<List<NewsEntity>> getNews(String cat) {
        db = AppDatabase.getAppDatabase(context);
        return db.newsDao().loadAllByCategory(cat);
    }

    private Completable deleteNews() {
        return Completable.fromCallable((Callable<Void>) () -> {
            db.newsDao().deleteAll();
            Log.d(LOG, "rows delete");
            return null;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG, "OnStop");
        //  compositeDisposable.dispose();
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
        //Gson gson = new Gson();
        // String gsonResponse = response.body()+"";
        //NewsResponse newsResponse = gson.fromJson(gsonResponse, NewsResponse.class);
        //List<NewsDTO> newsdto = response.getData();

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
        saveNews(news);
        return news;
    }

    private List<NewsItem> daoToNews(List<NewsEntity> newsEntities) {
        Log.d(LOG, "get " + newsEntities.size() + " news");
        List<NewsItem> news = new ArrayList<>();
        for (NewsEntity x : newsEntities) {
            news.add(new NewsItem(x.getTitle(), x.getImageUrl(), x.getCategory(), x.getPublishDate(), x.getPreviewText(), x.getFullText(), x.getUrl()));
        }
        return news;
    }

    private int checkitem(String s) {
        int i = 0;
        for (String x : categories) {
            if (x == s) return i;
            else i++;
        }
        return -1;
    }
}
