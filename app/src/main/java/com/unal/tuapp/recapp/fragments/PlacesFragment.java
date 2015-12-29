package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecyclePlaceAdapter;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.Category;
import com.unal.tuapp.recapp.backend.model.commentApi.model.Comment;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CategoryEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CommentEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByPlaceEndPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by andresgutierrez on 7/13/15.
 */
public class PlacesFragment extends Fragment {
    private static RecyclerView recyclerView;
    private static RecyclePlaceAdapter recyclePlaceAdapter;
    private View root;
    public static onPlaceListener mOnPlaceListener;
    public static SwipeRefreshLayout mySwipeRefresh;


    public List<String> filters;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_places,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.places_recycle_view);
        mySwipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.place_refresh);
        mySwipeRefresh.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Utility.isNetworkAvailable(getContext())) {
                    com.unal.tuapp.recapp.backend.model.placeApi.model.Place place = new com.unal.tuapp.recapp.backend.model.placeApi.model.Place();
                    Pair<Context, Pair<com.unal.tuapp.recapp.backend.model.placeApi.model.Place, String>> pair = new Pair<>(getContext(), new Pair<>(place, "getPlaces"));
                    new PlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);

                    Comment comment = new Comment();
                    Pair<Pair<Context,Pair<Long,Long>>,Pair<Comment,String>> pairComment = new Pair<>(
                            new Pair<>(getContext(),new Pair<>(-1L,-1L)),new Pair<>(comment,"getComments")
                    );
                    new CommentEndPoint(true).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairComment);
                }else{
                    mySwipeRefresh.setRefreshing(false);
                }



            }
        });
        //setHasOptionsMenu(true);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        //recyclerView.setHasFixedSize(true);
        filters = new ArrayList<>();
        List<Place> places = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = Utility.getLayoutManager(getActivity(), getResources().getConfiguration().screenWidthDp);
        recyclerView.setLayoutManager(layoutManager);
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
