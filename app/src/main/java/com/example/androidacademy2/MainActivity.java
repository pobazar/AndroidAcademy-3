package com.example.androidacademy2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.androidacademy2.news.NewsDetailsEditFragment;
import com.example.androidacademy2.news.NewsDetailsFragment;
import com.example.androidacademy2.news.NewsFragmentListener;
import com.example.androidacademy2.news.NewsListFragment;

import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements NewsFragmentListener {
    private static final String FRAGMENT_TAG_DETAILS = "details_fragment";
    private static final String FRAGMENT_TAG_LIST = "list_fragment";
    private static final String FRAGMENT_TAG_EDIT = "edit_fragment";
    private static final String LOG = "My_Log";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AppDatabase db;
    public static int f;
    public static boolean isTwoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getAppDatabase(this);
        final Disposable Disposable1 = deleteNews()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        compositeDisposable.add(Disposable1);


        isTwoPanel = findViewById(R.id.frame_detail) != null;

        if (savedInstanceState == null) {
            newsListFragmentStart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_button_menu:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.news_button_menu:
                newsListFragmentStart();
            case R.id.edit_button_menu:
                onNewsEditClicked(NewsDetailsFragment.url);
                return true;
           /* case R.id.delete_button_menu:
                final Disposable Disposable = deleteNews()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                compositeDisposable.add(Disposable);
                //this.finish();
            case R.id.save_button_menu:
                final Disposable  Disposable3 = save()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                compositeDisposable.add(Disposable3);
                this.finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();
        if (f == 0) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        if (f == 1) {
            getMenuInflater().inflate(R.menu.news_details_menu, menu);
        }
        if (f == 2) {
            getMenuInflater().inflate(R.menu.save_news_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void newsListFragmentStart() {
        NewsListFragment messageFragment = new NewsListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame, messageFragment, FRAGMENT_TAG_LIST)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onNewsDetailsClicked(String s) {
        NewsDetailsFragment fragment = NewsDetailsFragment.newInstance(s);

        int frameId = isTwoPanel? R.id.frame_detail : R.id.activity_main_frame;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameId, fragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onNewsEditClicked(String s) {
        NewsDetailsEditFragment fragment = NewsDetailsEditFragment.newInstance(s);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame, fragment, FRAGMENT_TAG_EDIT)
                .addToBackStack(null)
                .commit();
    }

    public Completable deleteNews() {
        return Completable.fromCallable((Callable<Void>) () -> {
            db.newsDao().deleteAll();
            Log.d(LOG, "rows delete");
            return null;
        });
    }

}
