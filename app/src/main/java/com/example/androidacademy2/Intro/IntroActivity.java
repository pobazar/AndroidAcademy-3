package com.example.androidacademy2.Intro;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.androidacademy2.R;
import com.example.androidacademy2.news.NewsListFragment;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

public class IntroActivity extends FragmentActivity {
    private Button butNews;
    private static final String LOG = "My_Log";
    public static final String SHARED_PREF_NAME = "MY_SHARED_PREF2";
    public static final String COUNT_START_KEY = "START_KEY";
    private int count;
    SharedPreferences sharedPref;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SharepPref count run app
        count = loadCounter();
        count++;
        // Log.d(LOG, "Count = "+count);
        saveCounter(count);
        // if (count % 2 == 1) {
        if (true) {
            setContentView(R.layout.intro_activity);
            butNews = findViewById(R.id.button_news);
            butNews.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, NewsListFragment.class)));
        } else {
            startActivity(new Intent(IntroActivity.this, NewsListFragment.class));
        }

        //viewPager
        pager = findViewById(R.id.viewPager_intro);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    private void saveCounter(int x) {
        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(COUNT_START_KEY, x);
        editor.apply();
    }

    private int loadCounter() {
        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPref.getInt(COUNT_START_KEY, 0);
    }

}
