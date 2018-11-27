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

import com.bumptech.glide.Glide;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.R;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsEditFragment extends Fragment {

    private String url;
    private ImageView image;
    private EditText titleText, fullText, publisheDate;
    private static final String LOG = "My_Log";
    private static final String ARGS_URL = "url";
    private AppDatabase db;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable Disposable2, Disposable3;
    private Context context;
    private Button but_save;
    private NewsFragmentListener listener;

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
        View view = inflater.inflate(R.layout.activity_news_details_edit, container, false);
        context = getContext();

        if (getArguments() != null) {
            url = getArguments().getString(ARGS_URL);
        }
        Log.d(LOG, "edit: " + url);

        db = AppDatabase.getAppDatabase(context);
        titleText = view.findViewById(R.id.title_news_details_edit);
        fullText = view.findViewById(R.id.full_news_details_edit);
        publisheDate = view.findViewById(R.id.date_news_details_edit);
        image = view.findViewById(R.id.image_news_details_edit);
        but_save = view.findViewById(R.id.button_save_edit);
        MainActivity.f = 2;

        but_save.setOnClickListener(v -> {
                final Disposable  Disposable3 = save()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                compositeDisposable.add(Disposable3);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                getFragmentManager().popBackStack();
            } else {
                listener.deleteFragmentEdit();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        Disposable2 = db.newsDao().findById(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNewsDetails, this::logError);
        compositeDisposable.add(Disposable2);
    }

    @Override
    public void onStop() {
        super.onStop();
        // compositeDisposable.dispose();
    }


    private void logError(Throwable th) {
        Log.d(LOG, "" + th);
    }

    private void showNewsDetails(NewsEntity news) {
        titleText.setText(news.getTitle());
        fullText.setText(news.getPreviewText());
        publisheDate.setText(news.getPublishDate());
        Glide.with(context).load(news.getImageUrl()).into(image);
    }

    public Completable save() {
        return Completable.fromCallable((Callable<Void>) () -> {
            db.newsDao().updateById(url, titleText.getText().toString(), fullText.getText().toString(), publisheDate.getText().toString());
            Log.d(LOG, "rows update");
            return null;
        });
    }
}
