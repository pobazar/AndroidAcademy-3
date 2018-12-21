package com.example.androidacademy2;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface AboutView extends MvpView {
    void setupTitle1(@NonNull String title);
    void setupTitle2(@NonNull String title);
    void setupTitle3(@NonNull String title);

    void setupPhoto(@Nullable int photoUrl);
    void setupImageTitle1(@Nullable int imageTitleUrl);
    void setupImageTitle2(@Nullable int imageTitleUrl);
    void setupImageTitle3(@Nullable int imageTitleUrl);

    void setupDescription(@NonNull String description);

    @StateStrategyType(SkipStrategy.class)
    void openBrowserActivity(@NonNull String url);
    @StateStrategyType(SkipStrategy.class)
    void openEmailActivity(@NonNull String mail, @NonNull String message);
}
