package com.unal.tuapp.recapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;

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
        addPlaces();
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

    public void addPlaces(){
        Vector<ContentValues> valuesVector = new Vector<ContentValues>();
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        ContentValues values3 = new ContentValues();
        ContentValues values4 = new ContentValues();

        values.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo");
        values.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.34234);
        values.put(RecappContract.PlaceEntry.COLUMN_LOG, 1.3242134);
        values.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "ALGO 1");
        values.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcionDSFS");

        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_favorites).
                compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream.toByteArray());
        values.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

        values1.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo1");
        values1.put(RecappContract.PlaceEntry.COLUMN_LAT, 1.34234);
        values1.put(RecappContract.PlaceEntry.COLUMN_LOG, -1.3242134);
        values1.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " ADD AS");
        values1.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcion DSFAS");
        ByteArrayOutputStream stream1 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background_material).
                compress(Bitmap.CompressFormat.PNG, 100, stream1);
        values1.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream1.toByteArray());
        values1.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

        values2.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo2");
        values2.put(RecappContract.PlaceEntry.COLUMN_LAT, 0.34234);
        values2.put(RecappContract.PlaceEntry.COLUMN_LOG, 0.3242134);
        values2.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " dfsdafaddress2");
        values2.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");
        values2.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

        ByteArrayOutputStream stream2 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_calendar).
                compress(Bitmap.CompressFormat.PNG, 100, stream2);
        values2.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream2.toByteArray());

        values3.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo3");
        values3.put(RecappContract.PlaceEntry.COLUMN_LAT, 2.14234);
        values3.put(RecappContract.PlaceEntry.COLUMN_LOG, 1.9242134);
        values3.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "nueva direcion");
        values3.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");

        ByteArrayOutputStream stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background_material).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        values3.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream3.toByteArray());
        values3.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

        values4.put(RecappContract.PlaceEntry.COLUMN_NAME, "algo4");
        values4.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.405637);
        values4.put(RecappContract.PlaceEntry.COLUMN_LOG, -73.946686);
        values4.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "234ad");
        values4.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");

        ByteArrayOutputStream stream4 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_calendar).
                compress(Bitmap.CompressFormat.PNG, 100, stream4);
        values4.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream4.toByteArray());
        values4.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

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

        ContentValues category = new ContentValues();
        category.put(RecappContract.CategoryEntry.COLUMN_NAME, "pilas");
        category.put(RecappContract.CategoryEntry.COLUMN_IMAGE, stream3.toByteArray());

        ContentValues category1 = new ContentValues();
        category1.put(RecappContract.CategoryEntry.COLUMN_NAME, "llantas");
        category1.put(RecappContract.CategoryEntry.COLUMN_IMAGE, stream4.toByteArray());

        Vector<ContentValues> categories = new Vector<>();
        categories.add(category);
        categories.add(category1);
        ContentValues categoryArray [] = new ContentValues[2];
        categories.toArray(categoryArray);
        getActivity().getContentResolver().bulkInsert(
                RecappContract.CategoryEntry.CONTENT_URI,
                categoryArray
        );

        ContentValues subCategory = new ContentValues(); //Id 1
        subCategory.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "pilas");
        subCategory.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 1);

        ContentValues subCategory1 = new ContentValues(); //Id 2
        subCategory1.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "aa");
        subCategory1.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 1);


        ContentValues subCategory2 = new ContentValues(); //Id 3
        subCategory2.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "llantas");
        subCategory2.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 2);

        Vector<ContentValues> subCategories = new Vector<>();
        subCategories.add(subCategory);
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);
        ContentValues subCategoryArray [] = new ContentValues[3];

        subCategories.toArray(subCategoryArray);


        getActivity().getContentResolver().bulkInsert(
                RecappContract.SubCategoryEntry.CONTENT_URI,
                subCategoryArray
        );

        ContentValues subCategoryPlace = new ContentValues();
        subCategoryPlace.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 1);
        subCategoryPlace.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 1);

        ContentValues subCategoryPlace1 = new ContentValues();
        subCategoryPlace1.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 3);
        subCategoryPlace1.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 1);

        ContentValues subCategoryPlace2 = new ContentValues();
        subCategoryPlace2.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 4);
        subCategoryPlace2.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 2);

        ContentValues subCategoryPlace3 = new ContentValues();
        subCategoryPlace3.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 3);
        subCategoryPlace3.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 3);

        Vector<ContentValues> subCategoriesPlaces = new Vector<>();
        subCategoriesPlaces.add(subCategoryPlace);
        subCategoriesPlaces.add(subCategoryPlace1);
        subCategoriesPlaces.add(subCategoryPlace2);
        subCategoriesPlaces.add(subCategoryPlace3);

        ContentValues []arraySubCategoriesPlaces = new ContentValues[4];
        subCategoriesPlaces.toArray(arraySubCategoriesPlaces);

        getActivity().getContentResolver().bulkInsert(
                RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                arraySubCategoriesPlaces
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


    public void setData(List<Place> places,Cursor cursor){
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.setPlaceCursor(cursor);
    }
    public void closeData(){
        recyclePlaceAdapter.closeCursor();
    }




}
