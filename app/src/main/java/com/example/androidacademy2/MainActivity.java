package com.example.androidacademy2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.androidacademy2.news.NewsDetailsActivityEdit;
import com.example.androidacademy2.news.NewsDetailsFragment;
import com.example.androidacademy2.news.NewsFragmentListener;
import com.example.androidacademy2.news.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewsListFragment messageFragment = new NewsListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_frame, messageFragment)
                .commit();
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
                startActivity(new Intent(this, NewsListFragment.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


  /*  @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.news_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_button_menu:
                Intent newsDetailsActivityIntent = new Intent(this, NewsDetailsActivityEdit.class);
                newsDetailsActivityIntent.putExtra("url", url);
                startActivity(newsDetailsActivityIntent);
                return true;
            case R.id.delete_button_menu:
                final Disposable Disposable = deleteNews()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                compositeDisposable.add(Disposable);
                //this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    @Override
    public void onNewsDetailsClicked(String s) {
        NewsDetailsFragment fragment =NewsDetailsFragment.newInstance(s);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame, fragment)
                .commit();
    }
}
