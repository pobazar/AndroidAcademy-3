package com.example.androidacademy2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidacademy2.DTO.MultimediaDTO;
import com.example.androidacademy2.DTO.NewsDTO;
import com.example.androidacademy2.DTO.NewsResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.VectorEnabledTintResources;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadNews extends AsyncTask<Long, Void, List<NewsItem>> {
    @NonNull
    private final WeakReference<Activity> activityRef;
    private static final String LOG = "My_Log";
    private static final String API_KEY = "fde4278ddd2f418d8b1565308d552669";

    private final NewsRecyclerAdapter.OnItemClickListener clickListener;
    private List<NewsItem> news;
    private Boolean f;
    TextView text;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button tryButton, categoryButton;
    Exception err;
    Activity act;
    String cat;

    //private Call<List<NewsDTO>> searchRequest;


    LoadNews(Activity activity, NewsRecyclerAdapter.OnItemClickListener clk, String category) {
        Log.d(LOG, "async start");
        activityRef = new WeakReference<>(activity);
        clickListener = clk;
        news = new ArrayList<>();
        text = activity.findViewById(R.id.text_complete);
        recyclerView = activity.findViewById(R.id.recycler_news);
        progressBar = activity.findViewById(R.id.progressBar_news);
        tryButton = activity.findViewById(R.id.button_try_again);
        categoryButton = activity.findViewById(R.id.button_category);
        act = activity;
        f = false;
        cat=category;
    }

    @Override
    protected List<NewsItem> doInBackground(Long... waitTime) {
        downloadNews(cat);
        //news = DataUtils.generateNews();
        try {
            Thread.sleep(waitTime[0]);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        Log.d(LOG, "doInBackgroud all");
        return news;
    }

    @Override
    protected void onPostExecute(List<NewsItem> news) {

        if (f.equals(true)) {
            visibleRecycler();
            if (act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(act, news, clickListener));
                recyclerView.setLayoutManager(new LinearLayoutManager(act));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);
            } else if (act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(act, news, clickListener));
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);
            }
            super.onPostExecute(news);
        } else {
            error(err);
        }
        Log.d(LOG, "async all");
    }

    private void downloadNews(@NonNull String category) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("http://api.nytimes.com/svc/topstories/v2/" + category + ".json?api-key=" + API_KEY)
                .build();

        Call call = client.newCall(request);
        Log.d(LOG, "try request");

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG, "request fail");
                err = e;
                f = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(LOG, "request complete");
                Gson gson = new Gson();
                String gsonResponse = response.body().string();
                NewsResponse newsResponse = gson.fromJson(gsonResponse, NewsResponse.class);
                List<NewsDTO> newsdto = newsResponse.getData();
                news = dtotonews(newsdto);
                f = true;
            }
        });
    }

    private List<NewsItem> dtotonews(List<NewsDTO> listdto) {
        List<NewsItem> news = new ArrayList<>();

        for (NewsDTO x : listdto) {
            String image = "";
            for (MultimediaDTO y : x.getMultimedia()) {
                if (y.getFormat().equals("Standard Thumbnail")) {
                    image = y.getUrl();
                    break;
                }
            }

            news.add(new NewsItem(x.getTitle(), image, x.getSection(), x.getPublishedDate().replace('T', ' '), x.getAbstract1(), ""));
        }

        return news;
    }

    private void error(Exception e) {
        text.setVisibility(View.VISIBLE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.VISIBLE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(true);
        //text.setText("Error: "+e);
    }

    private void visibleProgress() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tryButton.setVisibility(View.GONE);
        categoryButton.setEnabled(false);
        tryButton.setEnabled(false);
    }

    private void visibleRecycler() {
        text.setVisibility(View.GONE);
        categoryButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tryButton.setVisibility(View.GONE);
        tryButton.setEnabled(false);
        categoryButton.setEnabled(true);
    }
}
