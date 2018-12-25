package com.example.androidacademy2.news.Presenter;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.news.view.NewsDetailsEditView;
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
public class NewsDetailsEditPresenter extends MvpPresenter<NewsDetailsEditView> {
    private NewsDetailsEditView view;
    private String url;
    private NewsEntity news;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String LOG = "My_Log";

    @ProvidePresenter
    NewsDetailsEditPresenter provideNewsDetailsEditPresenter() {
        return new NewsDetailsEditPresenter();
    }

    @Override
    public void attachView(NewsDetailsEditView view) {
        super.attachView(view);
        this.view = view;
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
    }

    @StateStrategyType(SkipStrategy.class)
    public void setNews(@NonNull String url) {
        this.url = url;

        Disposable disposable2 = MainActivity.db.newsDao().findById(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::ShowNews, this::logError);
        compositeDisposable.add(disposable2);
    }
    private void logError(Throwable th) {
        Log.d(LOG, "" + th);
    }

    @StateStrategyType(SkipStrategy.class)
    private void ShowNews(NewsEntity news) {
        this.news = news;
        setupDate(news.getPublishDate());
        setupFull(news.getPreviewText());
        setupTitle(news.getTitle());
        setupPhoto(news.getImageUrl());
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @Override
    public void detachView(NewsDetailsEditView view) {
        super.detachView(view);
        // compositeDisposable.dispose();
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

    public void saveNews(String url, String title, String full, String date) {
        final Disposable Disposable3 = save(url, title, full, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        compositeDisposable.add(Disposable3);
        view.saveNews();
    }

    private Completable save(String url, String title, String full, String date) {
        return Completable.fromCallable((Callable<Void>) () -> {
            MainActivity.db.newsDao().updateById(url, title, full, date);
            Log.d(LOG, "rows update");
            return null;
        });
    }
}
