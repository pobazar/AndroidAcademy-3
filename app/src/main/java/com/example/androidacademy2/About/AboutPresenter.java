package com.example.androidacademy2.About;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class AboutPresenter  {
    private AboutView view;



    public void onAttach(@NonNull AboutView view) {
        this.view = view;
        setupTitle1();
        setupTitle2();
        setupTitle3();
        setupPhoto();
        setupImageTitle1();
        setupImageTitle2();
        setupImageTitle3();
        setupDescription();
    }

    public void onDetach() {

    }



    private void setupTitle1() {
        view.setupTitle1("adasd");
    }

    private void setupTitle2() {
    }

    private void setupTitle3() {
    }

    private void setupPhoto() {
    }

    private void setupImageTitle1() {
    }

    private void setupImageTitle2() {
    }

    private void setupImageTitle3() {
    }

    private void setupDescription() {
    }
}
