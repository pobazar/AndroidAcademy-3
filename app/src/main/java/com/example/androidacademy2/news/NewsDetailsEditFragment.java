package com.example.androidacademy2.news;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.MvpAppCompatFragment;
import com.example.androidacademy2.R;
import com.example.androidacademy2.news.Presenter.NewsDetailsEditPresenter;
import com.example.androidacademy2.news.Presenter.NewsDetailsPresenter;
import com.example.androidacademy2.news.view.NewsDetailsEditView;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsEditFragment extends MvpAppCompatFragment implements NewsDetailsEditView {
    @InjectPresenter
    NewsDetailsEditPresenter presenter;
    private static final int LAYOUT = R.layout.activity_news_details_edit;

    private String url;
    private ImageView image;
    private EditText titleText, fullText, publisheDate;
    private Button but_save;
    private static final String LOG = "My_Log";
    private static final String ARGS_URL = "url";

    private Context context;
    private NewsFragmentListener listener;
    private View view;

    static public NewsDetailsEditFragment newInstance(String url) {
        NewsDetailsEditFragment pageFragment = new NewsDetailsEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGS_URL, url);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

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

        if (getArguments() != null) {
            url = getArguments().getString(ARGS_URL);
        }
        Log.d(LOG, "edit: " + url);
        initViews();
        MainActivity.f = 2;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.setNews(url);
    }

    @Override
    public void onStop() {
        super.onStop();
        // compositeDisposable.dispose();
    }


    private void initViews() {
        titleText = view.findViewById(R.id.title_news_details_edit);
        fullText = view.findViewById(R.id.full_news_details_edit);
        publisheDate = view.findViewById(R.id.date_news_details_edit);
        image = view.findViewById(R.id.image_news_details_edit);
        but_save = view.findViewById(R.id.button_save_edit);

        but_save.setOnClickListener(v -> {
            Log.d(LOG, "Try save edit news");
            presenter.saveNews(url, titleText.getText().toString(), fullText.getText().toString(), publisheDate.getText().toString());
            saveNews();
        });

    }

    public void setupDate(@NonNull String date) {
        publisheDate.setText(date);
    }

    public void setupTitle(@NonNull String text) {
        titleText.setText(text);
    }

    public void setupFull(@NonNull String text) {
        fullText.setText(text);
    }

    public void setupPhoto(@Nullable String photoUrl) {
        Glide.with(context).load(photoUrl).into(image);
    }

    public void saveNews() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getFragmentManager().popBackStack();
        } else {
            listener.deleteFragmentDetails();
        }
    }

}
