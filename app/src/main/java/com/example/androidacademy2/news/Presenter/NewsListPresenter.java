package com.example.androidacademy2.news.Presenter;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.Net.Network;
import com.example.androidacademy2.data_news.NewsItem;
import com.example.androidacademy2.news.view.NewsDetailsView;
import com.example.androidacademy2.news.view.NewsListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class NewsListPresenter extends MvpPresenter<NewsListView> {
    private NewsListView view;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String LOG = "My_Log";
    static private final String[] categories = {"home", "world", "opinion", "national", "politics", "upshot", "nyregion", "business", "technology", "science", "health", "sports", "arts", "books", "movies",
            "theater", "sundayreview", "fashion", "tmagazine", "food", "travel", "magazine", "realestate", "automobiles", "obituaries", "insider"};

    @ProvidePresenter
    NewsListPresenter provideNewsDetailsPresenter() {
        return new NewsListPresenter();
    }

    @Override
    public void attachView(NewsListView view) {
        super.attachView(view);
        this.view = view;
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        updateNews();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @Override
    public void detachView(NewsListView view) {
        super.detachView(view);
        // compositeDisposable.dispose();
    }

    public void loadItems() {
        stateProgress();
        Log.d(LOG, "start rx load news");
        final Disposable downloadDisposable = Network.getInstance()
                .news()
                .search(MainActivity.category)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::dtoResponseToDao)
                .doOnSuccess(this::saveNews)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::completeLoad, this::stateError);
        compositeDisposable.add(downloadDisposable);
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

    private void saveNews(NewsEntity[] newsEntities) {
        MainActivity.db.newsDao().deleteAll();
        MainActivity.db.newsDao().insertAll(newsEntities);
        Log.d(LOG, "save " + newsEntities.length + " news to DB");
    }

    private void completeLoad(NewsEntity[] newsEntities) {
        Log.d(LOG, "download " + newsEntities.length + " news");
    }


    private void updateNews() {
        final Disposable newsRoomDisposable = getNews()
                .map(this::daoToNews)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNews, this::stateError);
        compositeDisposable.add(newsRoomDisposable);
    }

    private Observable<List<NewsEntity>> getNews() {
        return MainActivity.db.newsDao().getAll();
    }

    public Observable<List<NewsEntity>> getNews(String cat) {
        return MainActivity.db.newsDao().loadAllByCategory(cat);
    }

    private Completable deleteNews() {
        return Completable.fromCallable((Callable<Void>) () -> {
            MainActivity.db.newsDao().deleteAll();
            Log.d(LOG, "rows delete");
            return null;
        });
    }

    private List<NewsItem> daoToNews(List<NewsEntity> newsEntities) {
        Log.d(LOG, "get " + newsEntities.size() + " news");
        List<NewsItem> news = new ArrayList<>();
        for (NewsEntity x : newsEntities) {
            news.add(new NewsItem(x.getTitle(), x.getImageUrl(), x.getCategory(), x.getPublishDate(), x.getPreviewText(), x.getFullText(), x.getUrl()));
        }
        return news;
    }

    private void stateError(Throwable th) {
        Log.d(LOG, "download news failes: " + th);
        view.visibleError(th);
    }

    private void stateProgress() {
        view.visibleProgress();
    }

    private void stateRecycler() {
        view.visibleRecycler();
    }

    private void showNews(List<NewsItem> news) {
        stateRecycler();
        view.showNews(news);
    }
}
