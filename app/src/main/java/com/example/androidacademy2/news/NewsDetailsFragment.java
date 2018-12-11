package com.example.androidacademy2.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;

import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.MvpAppCompatFragment;
import com.example.androidacademy2.R;
import com.example.androidacademy2.news.Presenter.NewsDetailsPresenter;
import com.example.androidacademy2.news.view.NewsDetailsView;


import java.util.concurrent.Callable;

public class NewsDetailsFragment extends MvpAppCompatFragment implements NewsDetailsView {

    @InjectPresenter
    NewsDetailsPresenter presenter;
    private static final int LAYOUT = R.layout.activity_news_details;

    static public String url;
    WebView webView;
    private ImageView image;
    private TextView titleText, fullText, publisheDate;
    private Button butEdit;
    private Button butDel;
    View view;

    private static final String LOG = "My_Log";
    private static final String ARGS_URL = "url";
    private AppDatabase db;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context context;
    private NewsFragmentListener listener;


    static public NewsDetailsFragment newInstance(String url) {
        NewsDetailsFragment pageFragment = new NewsDetailsFragment();
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
        // listener = null;
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
        Log.d(LOG, url);

     //   db = AppDatabase.getAppDatabase(context);
        initViews();


        /*butEdit.setOnClickListener(v -> {
            listener.onNewsEditClicked(url);
        });*/


     /*   butDel.setOnClickListener(v -> {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                getFragmentManager().popBackStack();
            } else {
                listener.deleteFragmentDetails();
            }
        });*/
        /*webView = findViewById (R.id.web_news);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);*/
        MainActivity.f = 1;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.setNews(url);
      /*  if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        Disposable disposable2 = db.newsDao().findById(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNewsDetails, this::logError);
        compositeDisposable.add(disposable2);*/
    }

   /* private void showNewsDetails(NewsEntity news) {


        titleText.setText(news.getTitle());
        fullText.setText(news.getPreviewText());
        publisheDate.setText(news.getPublishDate());
        Glide.with(context).load(news.getImageUrl()).into(image);
        //setTitle(news.getCategory());
    }*/

    /*private void logError(Throwable th) {
        Log.d(LOG, "" + th);
    }*/

    @Override
    public void onStop() {
        super.onStop();
        // compositeDisposable.dispose();
    }

   /* public Completable deleteNews() {
        return Completable.fromCallable((Callable<Void>) () -> {
            db.newsDao().deleteById(url);
            Log.d(LOG, "1 news delete");
            return null;
        });
    }*/

    public void initViews() {
        titleText = view.findViewById(R.id.title_news_details);
        fullText = view.findViewById(R.id.full_news_details);
        publisheDate = view.findViewById(R.id.date_news_details);
        butEdit = view.findViewById(R.id.button_edit);
        butDel = view.findViewById(R.id.button_delete);
        image = view.findViewById(R.id.image_news_details);

        butEdit.setOnClickListener(v -> {
            Log.d(LOG, "Try open edit fragment");
            presenter.editNews(url);
        });

        butDel.setOnClickListener(v -> {
            Log.d(LOG, "Try delete news");
            presenter.deleteNews(url);
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

    public void deleteNews() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getFragmentManager().popBackStack();
        } else {
            listener.deleteFragmentDetails();
        }
    }

    public void editNews(String url) {
        listener.onNewsEditClicked(url);
    }
}
