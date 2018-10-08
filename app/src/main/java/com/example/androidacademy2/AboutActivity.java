package com.example.androidacademy2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {

    private TextView editMes, butMes ;
    private ImageButton imb1, imb2;
    public String message;
    public static final String LOG="My_Log";
    public String addresses="xulikto@gmail.com";
    public String subject="Feedback";
    public String vks="https://vk.com/amikhaylov20";
    public String teleg="https://web.telegram.org/#/im?p=u455490756_16869440757002662194";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        editMes=findViewById(R.id.edit_message);
        butMes=findViewById(R.id.send_message);
        imb1=findViewById(R.id.imageButton1);
        imb2=findViewById(R.id.imageButton2);


        butMes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(LOG,"Try open second activity");
                openEmailActivity();
            }
        });

        imb1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(LOG,"Try open vk");
                openBrowserActivity(vks);
            }
        });

        imb2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(LOG,"Try open telegram");
                openBrowserActivity(teleg);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.about_button_menu:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.news_button_menu:
                startActivity(new Intent(this, NewsListActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openEmailActivity()
    {
        message=editMes.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(getString(R.string.email_adr, addresses)));

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "No Email app found", Toast.LENGTH_LONG).show();
        }
    }

    public void openBrowserActivity(String url)
    {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "No Browser app found", Toast.LENGTH_LONG).show();
        }
    }
}
