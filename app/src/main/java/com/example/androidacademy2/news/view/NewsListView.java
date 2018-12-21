package com.example.androidacademy2.news.view;

import com.arellomobile.mvp.MvpView;
import com.example.androidacademy2.data_news.NewsItem;

import java.util.List;

public interface NewsListView extends MvpView {
    void visibleRecycler();

    void visibleProgress();

    void visibleError(Throwable th);

    void showNews(List<NewsItem> news);
}
