package com.example.androidacademy2.news;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;

import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.MvpAppCompatFragment;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.R;
import com.example.androidacademy2.data_news.NewsItem;
import com.example.androidacademy2.news.Presenter.NewsDetailsPresenter;
import com.example.androidacademy2.news.Presenter.NewsListPresenter;
import com.example.androidacademy2.news.view.NewsListView;
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


public class NewsListFragment extends MvpAppCompatFragment implements NewsListView {
    private static final int LAYOUT = R.layout.activity_news_list;
    @InjectPresenter
    NewsListPresenter presenter;

    private static final String LOG = "My_Log";
    private Button tryButton, categoryButton;
    private FloatingActionButton loadButton;
    private TextView text;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    static private final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
            "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};
    private Context context;
    private NewsFragmentListener listener;
    private View view;

    private final NewsRecyclerAdapter.OnItemClickListener clickListener = news ->
    {
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

        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        Log.d(LOG, "OnCreate");
        initViews();
        categoryButton.setText(MainActivity.category);
        //updateNews();

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
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG, "OnStop");
        //  compositeDisposable.dispose();
    }

    private int checkitem(String s) {
        int i = 0;
        for (String x : categories) {
            if (x == s) return i;
            else i++;
        }
        return -1;
    }


    private void initViews() {
        tryButton = view.findViewById(R.id.button_try_again);
        categoryButton = view.findViewById(R.id.button_category);
        loadButton = view.findViewById(R.id.button_load_news);
        text = view.findViewById(R.id.text_complete);
        recyclerView = view.findViewById(R.id.recycler_news);
        progressBar = view.findViewById(R.id.progressBar_news);

        tryButton.setOnClickListener(v -> {
            Log.d(LOG, "Try connect");
            presenter.loadItems();
        });

        categoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose category").setCancelable(false)
                    // добавляем переключатели
                    .setSingleChoiceItems(categories, checkitem(MainActivity.category),
                            (dialog, item) -> {
                                MainActivity.category = categories[item];
                                categoryButton.setText(MainActivity.category);
                                dialog.cancel();
                                Log.d(LOG, "Change category");
                                presenter.loadItems();

                            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        loadButton.setOnClickListener(v -> {
            Log.d(LOG, "float button onclick");
            presenter.loadItems();
        });
    }

    public void visibleProgress() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tryButton.setVisibility(View.GONE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(false);
    }

    public void visibleRecycler() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.GONE);
        tryButton.setEnabled(false);
        categoryButton.setEnabled(true);
    }

    public void visibleError(Throwable th) {
        text.setVisibility(View.VISIBLE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.VISIBLE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(true);
    }

    public void showNews(List<NewsItem> news) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setAdapter(new NewsRecyclerAdapter(context, news, clickListener));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setAdapter(new NewsRecyclerAdapter(getContext(), news, clickListener));
            if (MainActivity.isTwoPanel) {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            } else {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

}
