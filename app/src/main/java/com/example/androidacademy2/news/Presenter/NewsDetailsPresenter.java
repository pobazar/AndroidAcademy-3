package com.example.androidacademy2.news.Presenter;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.Glide;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.news.view.NewsDetailsView;


import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class NewsDetailsPresenter extends MvpPresenter<NewsDetailsView> {
    private NewsDetailsView view;
    private NewsEntity news;
    private String url;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String LOG = "My_Log";

    @ProvidePresenter
    NewsDetailsPresenter provideNewsDetailsPresenter() {
        return new NewsDetailsPresenter();
    }

    @Override
    public void attachView(NewsDetailsView view) {
        super.attachView(view);
        this.view = view;


    }

    public void setNews(@NonNull String url) {
        this.url = url;

        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        Disposable disposable2 = MainActivity.db.newsDao().findById(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::ShowNews, this::logError);
        compositeDisposable.add(disposable2);
    }

    private void ShowNews(NewsEntity news) {
        this.news = news;
        setupDate(news.getPublishDate());
        setupFull(news.getFullText());
        setupTitle(news.getTitle());
        setupPhoto(news.getImageUrl());
    }

    private void logError(Throwable th) {
        Log.d(LOG, "" + th);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @Override
    public void detachView(NewsDetailsView view) {
        super.detachView(view);
        //  compositeDisposable.dispose();
    }

    private void setupDate(@NonNull String date) {
        view.setupDate(date);
    }

    private void setupTitle(@NonNull String text) {
        view.setupTitle(text);
    }

    private void setupFull(@NonNull String text) {
        view.setupFull(text);
    }

    private void setupPhoto(@Nullable String photoUrl) {
        view.setupPhoto(photoUrl);
    }

    public void editNews(String url) {
        view.editNews(url);
    }

    public void deleteNews(String url) {
        Disposable disposable1 = deleteNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(disposable1);
        view.deleteNews();
    }

    private Completable deleteNews() {
        return Completable.fromCallable((Callable<Void>) () -> {
            MainActivity.db.newsDao().deleteById(url);
            Log.d(LOG, "1 news delete");
            return null;
        });
    }
}
