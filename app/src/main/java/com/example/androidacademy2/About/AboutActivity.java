package com.example.androidacademy2.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.example.androidacademy2.MainActivity;
import com.example.androidacademy2.R;
import com.example.androidacademy2.news.NewsListFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity implements AboutView {

    private static final int LAYOUT = R.layout.about_activity;
    private AboutPresenter aboutPresenter;
    private TextView editMes, butMes, title1, title2, title3, description;
    private ImageButton imb1, imb2;
    private ImageView photo, imageTitle1, imageTitle2, imageTitle3;
    public String message;
    public static final String LOG = "My_Log";
    public String addresses = "xulikto@gmail.com";
    public String subject = "Feedback";
    public String vks = "https://vk.com/amikhaylov20";
    public String teleg = "https://web.telegram.org/#/im?p=u455490756_16869440757002662194";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initViews();
        aboutPresenter = new AboutPresenter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        aboutPresenter.onAttach(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        aboutPresenter.onDetach();
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
                startActivity(new Intent(this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openEmailActivity() {
        message = editMes.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(getString(R.string.email_adr, addresses)));

        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Email app found", Toast.LENGTH_LONG).show();
        }
    }

    public void openBrowserActivity(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Browser app found", Toast.LENGTH_LONG).show();
        }
    }

    public void initViews() {
        editMes = findViewById(R.id.edit_message);
        butMes = findViewById(R.id.send_message);
        imb1 = findViewById(R.id.imageButton1);
        imb2 = findViewById(R.id.imageButton2);
        title1 = findViewById(R.id.title1);
        title2 = findViewById(R.id.title2);
        title3 = findViewById(R.id.title3);
        description = findViewById(R.id.description);
        photo = findViewById(R.id.photo);
        imageTitle1 = findViewById(R.id.imageTitle1);
        imageTitle2 = findViewById(R.id.imageTitle2);
        imageTitle3 = findViewById(R.id.imageTitle3);
        butMes.setOnClickListener(v -> {
            Log.d(LOG, "Try open second activity");
            openEmailActivity();
        });

        imb1.setOnClickListener(v -> {
            Log.d(LOG, "Try open vk");
            openBrowserActivity(vks);
        });

        imb2.setOnClickListener(v -> {
            Log.d(LOG, "Try open telegram");
            openBrowserActivity(teleg);
        });
    }

    @Override
    public void setupTitle1(@NonNull String title) {
        title1.setText(title);
    }

    @Override
    public void setupTitle2(@NonNull String title) {
        title2.setText(title);
    }

    @Override
    public void setupTitle3(@NonNull String title) {
        title3.setText(title);
    }

    @Override
    public void setupDescription(@NonNull String description) {
        this.description.setText(description);
    }

    @Override
    public void setupPhoto(@Nullable int photoUrl) {
        photo.setImageResource(photoUrl);
    }

    @Override
    public void setupImageTitle1(@Nullable int photoUrl) {
        imageTitle1.setImageResource(photoUrl);
    }

    @Override
    public void setupImageTitle2(@Nullable int photoUrl) {
        imageTitle2.setImageResource(photoUrl);
    }

    @Override
    public void setupImageTitle3(@Nullable int photoUrl) {
        imageTitle3.setImageResource(photoUrl);
    }

}
