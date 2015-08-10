package com.unal.tuapp.recapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;

/**
 * Created by andresgutierrez on 8/8/15.
 */
public class PlacesFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView recyclerView;
    private RecyclePlaceAdapter recyclePlaceAdapter;
    private View root;
    public static onPlaceListener mOnPlaceListener;
    private static final int PLACE = 11;
    private User user;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_places,container,false);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
        recyclerView = (RecyclerView) root.findViewById(R.id.places_recycle_view);

        final List<Place> places = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclePlaceAdapter = new RecyclePlaceAdapter(places);
        recyclePlaceAdapter.setOnItemClickListener(new RecyclePlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                if (mOnPlaceListener != null) {
                    mOnPlaceListener.onPlace(view, position);
                }
            }
        });
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclePlaceAdapter);
        alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(alphaAdapter));

        return root;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(PLACE)==null){
            getLoaderManager().initLoader(PLACE,null,this);
        }else{
            getLoaderManager().restartLoader(PLACE, null, this);
        }
        //getLoaderManager().initLoader(PLACE,null,this);



    }

    public interface onPlaceListener{
        void onPlace(View view,long position);
    }
    public void setOnPlaceListener(final onPlaceListener mOnPlaceListener){
        this.mOnPlaceListener = mOnPlaceListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecappContract.PlaceEntry.COLUMN_RATING + " DESC ";
        return new CursorLoader(
                getActivity(),
                RecappContract.UserByPlaceEntry.buildUserByPlaceUserUri(user.getId()),
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Place> places = Place.allPlaces(data);
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.setPlaceCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //List<Place> places = new ArrayList<>();
        //recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.closeCursor();
    }
}
