package com.example.androidacademy2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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
    List<NewsItem> news;

    //private Call<List<NewsDTO>> searchRequest;


    LoadNews(Activity activity, NewsRecyclerAdapter.OnItemClickListener clk) {
        activityRef = new WeakReference<>(activity);
        clickListener = clk;
    }

    @Override
    protected List<NewsItem> doInBackground(Long... waitTime) {

        loadNews("food");
       // news = DataUtils.generateNews();
        try {
            Thread.sleep(waitTime[0]);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return news;
    }

    @Override
    protected void onPostExecute(List<NewsItem> news) {

        Activity activity = activityRef.get();

        if (activity != null) {

            ProgressBar progressBar = activity.findViewById(R.id.progressBar_news);
            progressBar.setVisibility(View.GONE);

           /* TextView textView = activity.findViewById(R.id.text_complete);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.load_complete);*/


            RecyclerView recyclerView = activity.findViewById(R.id.recycler_news);
            recyclerView.setVisibility(View.VISIBLE);
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(activity, news, clickListener));
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);
            } else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setAdapter(new NewsRecyclerAdapter(activity, news, clickListener));
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
                recyclerView.addItemDecoration(dividerItemDecoration);

                super.onPostExecute(news);
            }
        }
    }

    private void loadNews(@NonNull String category) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("http://api.nytimes.com/svc/topstories/v2/" + category + ".json?api-key=" + API_KEY)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG, "request fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                String gsonResponse = response.body().string();
                Log.d(LOG, "request complete");
                NewsResponse newsResponse = gson.fromJson(gsonResponse, NewsResponse.class);
                List<NewsDTO> newsdto = newsResponse.getData();
                news=dtotonews(newsdto);
            }
        });


    }

    private List<NewsItem> dtotonews(List<NewsDTO> listdto) {
        List<NewsItem> news = new ArrayList<>();

        for (NewsDTO x : listdto) {
            String image="";
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

    private void handleError(Throwable throwable) {
        Activity activity = activityRef.get();
        TextView text = activity.findViewById(R.id.text_complete);
        if (throwable instanceof IOException) {
            text.setText("Network Error");
            return;
        }
        text.setText("Server Error");
    }
}
