package com.unal.tuapp.recapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.News;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by andresgutierrez on 11/30/15.
 */
public class RecycleNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<News> news;
    private static OnNewsListener onNewsListener;

    public interface OnNewsListener{
        void onNews(String link);
    }
    public RecycleNewsAdapter(List<News> news) {
        this.news = news;
    }

    public static class NewsViewHolderAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageNews;
        private TextView titleNews;
        private TextView descriptionNews;
        private TextView editorNews;
        private TextView dateNews;

        public NewsViewHolderAdapter(View itemView) {
            super(itemView);
            imageNews = (ImageView) itemView.findViewById(R.id.image_news);
            titleNews = (TextView) itemView.findViewById(R.id.title_news);
            descriptionNews = (TextView) itemView.findViewById(R.id.description_news);
            editorNews = (TextView) itemView.findViewById(R.id.editor_news);
            dateNews = (TextView) itemView.findViewById(R.id.date_news);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onNewsListener!=null){
                onNewsListener.onNews(news.get(getAdapterPosition()).getUrl());
            }
        }
    }

    public static void setOnNewsListener(OnNewsListener onNewsListener) {
        RecycleNewsAdapter.onNewsListener = onNewsListener;
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_item,parent,false);
        viewHolder = new NewsViewHolderAdapter(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewsViewHolderAdapter newsViewHolderAdapter = (NewsViewHolderAdapter) holder;
        News mNew = news.get(position);
        if(mNew.getImage()!=null){
            newsViewHolderAdapter.imageNews.setImageBitmap(mNew.getImage());
            newsViewHolderAdapter.imageNews.setVisibility(View.VISIBLE);
        }
        newsViewHolderAdapter.titleNews.setText(mNew.getTitle());
        newsViewHolderAdapter.descriptionNews.setText(mNew.getDescription());
        newsViewHolderAdapter.editorNews.setText(mNew.getEditor());
        Date date= new Date(mNew.getDate());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

        newsViewHolderAdapter.dateNews.setText(format.format(date));
    }
    public void swapData(List<News> news,boolean swipe){
        if(!swipe){
            this.news.clear();
        }
        this.news.addAll(news);
        notifyDataSetChanged();
    }
}
