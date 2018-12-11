package com.example.androidacademy2.news.view;

import com.arellomobile.mvp.MvpView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface NewsDetailsView extends MvpView {
    void setupDate(@NonNull  String date);
    void setupTitle(@NonNull  String date);
    void setupFull(@NonNull  String date);

    void setupPhoto(@Nullable String photoUrl);

    void editNews(String url);
    void deleteNews();
}
