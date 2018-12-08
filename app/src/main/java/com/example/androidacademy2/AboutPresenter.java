package com.example.androidacademy2;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@InjectViewState
public class AboutPresenter extends MvpPresenter<AboutView> {
    private AboutView view;

    @ProvidePresenter
    AboutPresenter provideAboutPresenter() {
        return new AboutPresenter();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    @Override
    public void attachView(AboutView view) {
        super.attachView(view);
        this.view = view;
        setupTitle1("Student of Android Academy");
        setupTitle2("Student of MPEI");
        setupTitle3("Cat\'s paw");
        setupPhoto(R.drawable.avatarka);
        setupImageTitle1(R.drawable.baseline_android_black_18dp);
        setupImageTitle2(R.drawable.baseline_school_black_18dp);
        setupImageTitle3(R.drawable.baseline_pets_black_18dp);
        setupDescription("Artem has been studying programming for 7 years. He\'s a 6th year student at MPEI. Goes to lectures in The Android Academy and studying development for Android.");

    }

    @Override
    public void detachView(AboutView view) {
        super.detachView(view);
    }

    private void setupTitle1(String title) {
        view.setupTitle1(title);
    }

    private void setupTitle2(String title) {
        view.setupTitle2(title);
    }

    private void setupTitle3(String title) {
        view.setupTitle3(title);
    }

    private void setupPhoto(int url) {
        view.setupPhoto(url);
    }

    private void setupImageTitle1(int url) {
        view.setupImageTitle1(url);
    }

    private void setupImageTitle2(int url) {
        view.setupImageTitle2(url);
    }

    private void setupImageTitle3(int url) {
        view.setupImageTitle3(url);
    }

    private void setupDescription(String description) {
        view.setupDescription(description);
    }

    public void openBrowserActivity(String url) {
        view.openBrowserActivity(url);
    }

    public void openEmailActivity(String mail, String message) {
        view.openEmailActivity(mail,message);
    }

}
