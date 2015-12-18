package com.unal.tuapp.recapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.NavigationDrawer;
import com.unal.tuapp.recapp.adapters.RecycleNewsAdapter;
import com.unal.tuapp.recapp.data.News;
import com.unal.tuapp.recapp.others.Utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/30/15.
 */
public class NewsFragment extends Fragment{
    private View root;
    private static RecyclerView recyclerNews;
    private static RecycleNewsAdapter recycleNewsAdapter;
    private static List<News> news;
    public static SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_news,container,false);
        recyclerNews = (RecyclerView) root.findViewById(R.id.news_recycler);
        news =  new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.news_refresh);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((NavigationDrawer)getActivity()).callNewsRefresh("https://ajax.googleapis.com/ajax/services/search/news?v=2.0&q=reciclaje&rsz=8&start="+recycleNewsAdapter.getItemCount()+1);

            }
        });
        RecyclerView.LayoutManager layoutManager = Utility.getLayoutManager(getActivity(), getResources().getConfiguration().screenWidthDp);
        recyclerNews.setLayoutManager(layoutManager);
        recycleNewsAdapter.setOnNewsListener(new RecycleNewsAdapter.OnNewsListener() {
            @Override
            public void onNews(String link) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });
        recycleNewsAdapter = new RecycleNewsAdapter(news);
        recyclerNews.setAdapter(recycleNewsAdapter);
        return root;
    }

    public void setNews(List<News>news,boolean swipe){
        this.news = news;
        recycleNewsAdapter.swapData(news,swipe);
    }


}
