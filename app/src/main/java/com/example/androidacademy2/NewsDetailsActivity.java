package com.example.androidacademy2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class NewsDetailsActivity extends AppCompatActivity
{

    TextView title, full, date;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);


        title=findViewById(R.id.title_news_details);
        full=findViewById(R.id.full_news_details);
        date=findViewById(R.id.date_news_details);
        image=findViewById(R.id.image_news_details);


        setTitle(getIntent().getStringExtra("name"));
        title.setText(getIntent().getStringExtra("title"));
        full.setText(getIntent().getStringExtra("full"));
        date.setText(getIntent().getStringExtra("date"));
        Glide.with(this).load(getIntent().getStringExtra("image")).into(image);
    }
}
