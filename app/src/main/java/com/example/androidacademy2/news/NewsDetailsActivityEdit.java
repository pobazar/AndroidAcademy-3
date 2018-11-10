package com.example.androidacademy2.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.R;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsActivityEdit extends AppCompatActivity {

    String url;
    ImageView image;
    EditText titleText, fullText, publisheDate;
    private static final String LOG = "My_Log";
    private AppDatabase db;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Disposable Disposable2, Disposable3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details_edit);
        url = getIntent().getStringExtra("url");
        Log.d(LOG, "edit: " + url);

        db = AppDatabase.getAppDatabase(this);
        titleText = findViewById(R.id.title_news_details_edit);
        fullText = findViewById(R.id.full_news_details_edit);
        publisheDate = findViewById(R.id.date_news_details_edit);
        image = findViewById(R.id.image_news_details_edit);
    }

    @Override
    protected void onStart() {
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
    protected void onStop() {
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
        Glide.with(this).load(news.getImageUrl()).into(image);
        setTitle(news.getCategory());
    }

    public Completable save() {
        return Completable.fromCallable((Callable<Void>) () -> {
            db.newsDao().updateById(url, titleText.getText().toString(), fullText.getText().toString(), publisheDate.getText().toString());
            Log.d(LOG, "rows update");
            return null;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.save_news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button_menu:
                Disposable3 = save()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                compositeDisposable.add(Disposable3);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
