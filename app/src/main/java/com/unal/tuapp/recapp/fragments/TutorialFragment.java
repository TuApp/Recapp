package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleTutorialAdapter;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.data.Tutorial;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByTutorialEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.TutorialEndPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class TutorialFragment  extends Fragment {
    private static  RecyclerView recyclerView;
    private static RecycleTutorialAdapter adapter;
    public static onTutorialListener mOnTutorialListener;
    public static SwipeRefreshLayout mySwipeRefresh;


    public TutorialFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container,false);
        RecyclerView.LayoutManager layoutManager = Utility.getLayoutManager(getActivity(), getResources().getConfiguration().screenWidthDp);
        recyclerView = (RecyclerView) view.findViewById(R.id.tutorial_recycler);
        mySwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.tutorial_refresh);
        mySwipeRefresh.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial tutorial = new com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial();
                Pair<Context, Pair<com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial, String>> pairTutorial = new Pair<>(getContext(), new Pair<>(tutorial, "getTutorials"));
                new TutorialEndPoint(true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairTutorial);

            }
        });
        recyclerView.setLayoutManager(layoutManager);
        List<Tutorial> tutorials = new ArrayList<>();
        adapter = new RecycleTutorialAdapter(tutorials,getContext());
        adapter.setOnItemClickListener(new RecycleTutorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position, String link) {
                if (mOnTutorialListener != null) {
                    mOnTutorialListener.onTutorial(view, position, link);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public interface onTutorialListener {
        void onTutorial(View view, long position, String link);
    }

    public void setOnTutorialListener(final onTutorialListener mOnTutorialListener){
        this.mOnTutorialListener = mOnTutorialListener;
    }

    public void setDataTutorials(List<Tutorial> tutorials,Cursor cursor){
        adapter.swapData(tutorials);
        adapter.setTutorialCursor(cursor);
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public void closeData(){
        adapter.closeCursor();
    }
}
