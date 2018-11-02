package com.example.androidacademy2.news;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidacademy2.AppDatabase;
import com.example.androidacademy2.DB.NewsEntity;
import com.example.androidacademy2.R;

public class NewsDetailsActivity extends AppCompatActivity {


    String url;
    WebView webView;
    ImageView image;
    TextView titleText, fullText, publisheDate;
    private static final String LOG = "My_Log";
    private AppDatabase db;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    Disposable Disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        url = getIntent().getStringExtra("url");
        Log.d(LOG,url);

        db = AppDatabase.getAppDatabase(this);

        Disposable = db.newsDao().findById(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showNewsDetails, this::logError);
        compositeDisposable.add(Disposable);

        /*webView = findViewById (R.id.web_news);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);*/
    }

    private void showNewsDetails(NewsEntity news)
    {
        titleText = findViewById(R.id.title_news_details);
        fullText = findViewById(R.id.full_news_details);
        publisheDate = findViewById(R.id.date_news_details);
        image = findViewById(R.id.image_news_details);

        titleText.setText(news.getTitle());
        fullText.setText(news.getFullText());
        publisheDate.setText(news.getPublishDate());
        Glide.with(this).load(news.getImageUrl()).into(image);
        setTitle(news.getTitle());
    }

    private void logError(Throwable th)
    {
        Log.d(LOG,""+th);
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }
}
