package com.example.androidacademy2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;



public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>
{
    private final List<NewsItem> newsItemList;
    private final Context context;
    private final LayoutInflater inflater;
    public static final String LOG="My_Log";

    public NewsRecyclerAdapter(Context context, List<NewsItem> news)
    {
        this.newsItemList = news;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Log.d(LOG,"Constructor recycler adapter");
    }

    @Override
    public int getItemCount()
    {
        return newsItemList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(
                inflater.inflate(R.layout.item_news, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        NewsItem news = newsItemList.get(position);

        // Fill views with our data
        holder.titleView.setText(news.getTitle());
        holder.previewView.setText(news.getPreviewText());
        holder.fulltextView.setText(news.getFullText());
        holder.dateView.setText(news.getPublishDate()+"");

        Glide.with(context).load(news.getImageUrl()).into(holder.imageView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView previewView;
        public final TextView fulltextView;
        public final TextView dateView;


        public ViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_news);
            titleView = itemView.findViewById(R.id.title_news);
            previewView = itemView.findViewById(R.id.preview_news);
            fulltextView = itemView.findViewById(R.id.full_text_news);
            dateView = itemView.findViewById(R.id.date_news);
        }
    }

}
