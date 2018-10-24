package com.example.androidacademy2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;



public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>
{
    private final List<NewsItem> newsItemList;
    private final Context context;
    private final LayoutInflater inflater;
    private final OnItemClickListener clickListener;
    public static final String LOG="My_Log";

    public NewsRecyclerAdapter(Context context, List<NewsItem> news,  OnItemClickListener clickListener)
    {
        this.newsItemList = news;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.clickListener=clickListener;
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
        return new ViewHolder(inflater.inflate(R.layout.item_news, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        NewsItem news = newsItemList.get(position);

        // Fill views with our data
        holder.nameView.setText(news.getCategory());
        holder.titleView.setText(news.getTitle());
        holder.previewView.setText(news.getPreviewText());
        holder.dateView.setText(news.getPublishDate()+"");

        Glide.with(context).load(news.getImageUrl()).into(holder.imageView);
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView imageView;
        private final TextView nameView;
        private final TextView titleView;
        private final TextView previewView;
        private final TextView dateView;


        private ViewHolder(View itemView, @Nullable OnItemClickListener listener)
        {
            super(itemView);

            itemView.setOnClickListener(view ->
            {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(newsItemList.get(position));
                }
            });

            imageView = itemView.findViewById(R.id.image_news);
            nameView = itemView.findViewById(R.id.name_news);
            titleView = itemView.findViewById(R.id.title_news);
            previewView = itemView.findViewById(R.id.preview_news);
            dateView = itemView.findViewById(R.id.date_news);
        }
    }

}
