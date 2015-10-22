package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Event;
import com.unal.tuapp.recapp.data.Tutorial;
import com.unal.tuapp.recapp.data.RecappContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class TutorialFragment  extends Fragment {
    private static  RecyclerView recyclerView;
    private static RecycleTutorialAdapter adapter;
    public static onTutorialListener mOnTutorialListener;


    public TutorialFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container,false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.tutorial_recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Tutorial> tutorials = new ArrayList<>();
        adapter = new RecycleTutorialAdapter(tutorials);
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

    public void closeData(){
        adapter.closeCursor();
    }
}
