package com.example.androidacademy2;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NewsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_news);
        recyclerView.setAdapter(new NewsRecyclerAdapter(this, DataUtils.generateNews()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
