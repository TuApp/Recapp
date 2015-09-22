package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GalleryFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static RecyclePlaceImagesAdapter recyclePlaceImagesAdapter;
    private View root;
    public static onPlaceImagesListener mOnPlaceImagesListener;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_gallery,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.places_images_recycle_view);
        //setHasOptionsMenu(true);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        //recyclerView.setHasFixedSize(true);

        List<byte[]> placesImages = new ArrayList<>();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        recyclePlaceImagesAdapter = new RecyclePlaceImagesAdapter(placesImages);
        recyclePlaceImagesAdapter.setOnItemClickListener(new RecyclePlaceImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {

                if (mOnPlaceImagesListener != null) {
                    mOnPlaceImagesListener.onPlace(view, position);
                }

            }
        });
        //AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclePlaceImagesAdapter);
        //alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(recyclePlaceImagesAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    public interface onPlaceImagesListener {
        void onPlace(View view,long position);
    }


    public void setOnPlaceListener(final onPlaceImagesListener mOnPlaceImagesListener){
        this.mOnPlaceImagesListener = mOnPlaceImagesListener;
    }

    public void setData(List<byte[]> places,Cursor cursor){
        recyclePlaceImagesAdapter.swapData(places);
        recyclePlaceImagesAdapter.setPlaceImagesCursor(cursor);
    }
    public void closeData(){
        recyclePlaceImagesAdapter.closeCursor();
    }



}
