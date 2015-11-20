package com.unal.tuapp.recapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecyclePlaceAdapter;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.Category;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.SubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CategoryEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.DummyData;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByPlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByTutorialEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.TutorialEndPoint;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * Created by andresgutierrez on 7/13/15.
 */
public class PlacesFragment extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclePlaceAdapter recyclePlaceAdapter;
    private View root;
    public static onPlaceListener mOnPlaceListener;


    public List<String> filters;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_places,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.places_recycle_view);
        //setHasOptionsMenu(true);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        //recyclerView.setHasFixedSize(true);
        filters = new ArrayList<>();
        List<Place> places = new ArrayList<>();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        recyclePlaceAdapter = new RecyclePlaceAdapter(places);
        recyclePlaceAdapter.setOnItemClickListener(new RecyclePlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                //TextView name = (TextView) view.findViewById(R.id.place_item);

                if (mOnPlaceListener != null) {
                    mOnPlaceListener.onPlace(view, position);
                }

            }
        });
        //AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclePlaceAdapter);
        //alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(recyclePlaceAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    public interface onPlaceListener{
        void onPlace(View view,long position);
    }


    public void setOnPlaceListener(final onPlaceListener mOnPlaceListener){
        this.mOnPlaceListener= mOnPlaceListener;
    }




    public void setData(List<Place> places,Cursor cursor){
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.setPlaceCursor(cursor);
    }
    public void closeData(){
        recyclePlaceAdapter.closeCursor();
    }




}
