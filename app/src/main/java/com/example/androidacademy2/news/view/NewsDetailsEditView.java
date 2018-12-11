package com.example.androidacademy2.news.view;

import com.arellomobile.mvp.MvpView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface NewsDetailsEditView extends MvpView {
    void setupDate(@NonNull String date);

    void setupTitle(@NonNull String text);

    void setupFull(@NonNull String text);

    void setupPhoto(@Nullable String photoUrl);

    void saveNews();
}
