package com.unal.tuapp.recapp;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView recyclerView;
    private RecyclePlaceAdapter recyclePlaceAdapter;
    private View root;
    public static onPlaceListener mOnPlaceListener;
    public static final int PLACES_LOADER = 0;
    private ViewPager viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_places,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.places_recycle_view);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        //recyclerView.setHasFixedSize(true);

        addPlaces();
        final List<Place> places = new ArrayList<>();
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        recyclePlaceAdapter = new RecyclePlaceAdapter(places);
        recyclePlaceAdapter.setOnItemClickListener(new RecyclePlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                //TextView name = (TextView) view.findViewById(R.id.place_item);
                //Log.e("algo", "" + position + " " + name.getText().toString());
                if (mOnPlaceListener != null) {
                    mOnPlaceListener.onPlace(view, position);
                }

            }
        });
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclePlaceAdapter);
        alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(alphaAdapter));
        getLoaderManager().initLoader(PLACES_LOADER, null, this);

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = RecappContract.PlaceEntry.COLUMN_RATING + " DESC ";
        return new CursorLoader(getActivity(),
                RecappContract.PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.w("algo", "algo");
        List<Place> places =Place.allPlaces(cursor);
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.setPlaceCursor(cursor);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        List<Place> places = new ArrayList<>();
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.closeCursor();

    }

    public void addPlaces(){
        Vector<ContentValues> valuesVector = new Vector<ContentValues>();
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        ContentValues values3 = new ContentValues();
        ContentValues values4 = new ContentValues();

        values.put(RecappContract.PlaceEntry.COLUMN_NAME,"algo");
        values.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values.put(RecappContract.PlaceEntry.COLUMN_LOG,1.3242134);
        values.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "ALGO 1");
        values.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcionDSFS");

        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_favorites).
                compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream.toByteArray());

        values1.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo1");
        values1.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values1.put(RecappContract.PlaceEntry.COLUMN_LOG, 1.3242134);
        values1.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " ADD AS");
        values1.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcion DSFAS");
        ByteArrayOutputStream stream1 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background_material).
                compress(Bitmap.CompressFormat.PNG, 100, stream1);
        values1.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream1.toByteArray());

        values2.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo2");
        values2.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values2.put(RecappContract.PlaceEntry.COLUMN_LOG,1.3242134);
        values2.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " dfsdafaddress2");
        values2.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");

        ByteArrayOutputStream stream2 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_calendar).
                compress(Bitmap.CompressFormat.PNG, 100, stream2);
        values2.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream2.toByteArray());

        values3.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo3");
        values3.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values3.put(RecappContract.PlaceEntry.COLUMN_LOG,1.3242134);
        values3.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "nueva direcion");
        values3.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");

        ByteArrayOutputStream stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background_material).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        values3.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream3.toByteArray());

        values4.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo4");
        values4.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values4.put(RecappContract.PlaceEntry.COLUMN_LOG, 1.3242134);
        values4.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "234ad");
        values4.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");

        ByteArrayOutputStream stream4 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_calendar).
                compress(Bitmap.CompressFormat.PNG, 100, stream4);
        values4.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream4.toByteArray());

        valuesVector.add(values);
        valuesVector.add(values1);
        valuesVector.add(values2);
        valuesVector.add(values3);
        valuesVector.add(values4);

        ContentValues valuesArray [] = new ContentValues[valuesVector.size()];
        valuesVector.toArray(valuesArray);



        getActivity().getContentResolver().bulkInsert(
                RecappContract.PlaceEntry.CONTENT_URI,
                valuesArray
        );

        ContentValues image = new ContentValues();
        ByteArrayOutputStream stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background_material).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY,1); //This is only a example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

    }

}
