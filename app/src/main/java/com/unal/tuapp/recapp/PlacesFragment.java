package com.unal.tuapp.recapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

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

        values.put(RecappContract.PlaceEntry.COLUMN_NAME, "CC Unicentro");
        values.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.723904099999999);
        values.put(RecappContract.PlaceEntry.COLUMN_LOG,  -74.11411179999999);
        values.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " Kr 103 # 1 - 89");
        values.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "Shopping center in Bogota");

        ByteArrayOutputStream stream =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.unicentrologo).
                compress(Bitmap.CompressFormat.PNG, 100, stream);
        values.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream.toByteArray());
        values.put(RecappContract.PlaceEntry.COLUMN_WEB, "http://www.pilascolombia.com/");

        values1.put(RecappContract.PlaceEntry.COLUMN_NAME, "CC Gran Estacion");
        values1.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.648128);
        values1.put(RecappContract.PlaceEntry.COLUMN_LOG, -74.101848);
        values1.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " ADD AS");
        values1.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "Shopping center in Bogota");
        ByteArrayOutputStream stream1 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.gelogo).
                compress(Bitmap.CompressFormat.PNG, 100, stream1);
        values1.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream1.toByteArray());
        values1.put(RecappContract.PlaceEntry.COLUMN_WEB, "http://www.pilascolombia.com/");

        values2.put(RecappContract.PlaceEntry.COLUMN_NAME, "TM Station Ricaurte");
        values2.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.611586);
        values2.put(RecappContract.PlaceEntry.COLUMN_LOG, -74.094059);
        values2.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, " dfsdafaddress2");
        values2.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "alguna descripcioDSFn");
        values2.put(RecappContract.PlaceEntry.COLUMN_WEB,"http://www.pilascolombia.com/");

        ByteArrayOutputStream stream2 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.tmricaurtelogo).
                compress(Bitmap.CompressFormat.PNG, 100, stream2);
        values2.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream2.toByteArray());

        values3.put(RecappContract.PlaceEntry.COLUMN_NAME, "CC Salitre Plaza");
        values3.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.6529149);
        values3.put(RecappContract.PlaceEntry.COLUMN_LOG, -74.1102505);
        values3.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "nueva direcion");
        values3.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "Shopping center in Bogota");

        ByteArrayOutputStream stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.salitreplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        values3.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream3.toByteArray());
        values3.put(RecappContract.PlaceEntry.COLUMN_WEB, "http://www.pilascolombia.com/");

        values4.put(RecappContract.PlaceEntry.COLUMN_NAME, "UN C. y T.");
        values4.put(RecappContract.PlaceEntry.COLUMN_LAT, 4.638242);
        values4.put(RecappContract.PlaceEntry.COLUMN_LOG, -74.083961);
        values4.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, "234ad");
        values4.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, "Building at National University of Colombia, Bogota");

        ByteArrayOutputStream stream4 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.cytplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream4);
        values4.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, stream4.toByteArray());
        values4.put(RecappContract.PlaceEntry.COLUMN_WEB, "http://www.pilascolombia.com/");

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
        stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.battery_circle).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        category.put(RecappContract.CategoryEntry.COLUMN_IMAGE, stream3.toByteArray());

        ContentValues category1 = new ContentValues();
        category1.put(RecappContract.CategoryEntry.COLUMN_NAME, "llantas");
        stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.tire_circle).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        category1.put(RecappContract.CategoryEntry.COLUMN_IMAGE, stream3.toByteArray());

        ContentValues category2 = new ContentValues();
        category2.put(RecappContract.CategoryEntry.COLUMN_NAME, "equipos");
        stream3 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.electronic_circle).
                compress(Bitmap.CompressFormat.PNG, 100, stream3);
        category2.put(RecappContract.CategoryEntry.COLUMN_IMAGE, stream3.toByteArray());

        Vector<ContentValues> categories = new Vector<>();
        categories.add(category);
        categories.add(category1);
        categories.add(category2);
        ContentValues categoryArray [] = new ContentValues[3];
        categories.toArray(categoryArray);
        getActivity().getContentResolver().bulkInsert(
                RecappContract.CategoryEntry.CONTENT_URI,
                categoryArray
        );

        ContentValues subCategory = new ContentValues(); //Id 1
        subCategory.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "aaa");
        subCategory.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 1);

        ContentValues subCategory1 = new ContentValues(); //Id 2
        subCategory1.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "aa");
        subCategory1.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 1);


        ContentValues subCategory2 = new ContentValues(); //Id 3
        subCategory2.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "Llantas");
        subCategory2.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 2);

        ContentValues subCategory3 = new ContentValues(); //Id 3
        subCategory3.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "TV");
        subCategory3.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 3);

        ContentValues subCategory4 = new ContentValues(); //Id 3
        subCategory4.put(RecappContract.SubCategoryEntry.COLUMN_NAME, "Telefonos");
        subCategory4.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, 3);

        Vector<ContentValues> subCategories = new Vector<>();
        subCategories.add(subCategory);
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);
        subCategories.add(subCategory3);
        subCategories.add(subCategory4);
        ContentValues subCategoryArray [] = new ContentValues[5];

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

        ContentValues subCategoryPlace4 = new ContentValues();
        subCategoryPlace4.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 1);
        subCategoryPlace4.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 4);

        ContentValues subCategoryPlace5 = new ContentValues();
        subCategoryPlace5.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, 2);
        subCategoryPlace5.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, 5);

        Vector<ContentValues> subCategoriesPlaces = new Vector<>();
        subCategoriesPlaces.add(subCategoryPlace);
        subCategoriesPlaces.add(subCategoryPlace1);
        subCategoriesPlaces.add(subCategoryPlace2);
        subCategoriesPlaces.add(subCategoryPlace3);
        subCategoriesPlaces.add(subCategoryPlace4);
        subCategoriesPlaces.add(subCategoryPlace5);

        ContentValues []arraySubCategoriesPlaces = new ContentValues[6];
        subCategoriesPlaces.toArray(arraySubCategoriesPlaces);

        getActivity().getContentResolver().bulkInsert(
                RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                arraySubCategoriesPlaces
        );



        //Unicentro's pictures
        ContentValues image = new ContentValues();
        ByteArrayOutputStream stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.unicentrologo).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 1); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.unicentroplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 1); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.unicentroplace2).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 1); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.unicentroplace3).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 1); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );


        //GE's pictures
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.gelogo).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 2); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.geplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 2); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.geplace2).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 2); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.geplace3).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 2); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );


        //TM Ricaurte pictures
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.tmricaurtelogo).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 3); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.tmricaurteplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 3); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        //Salitre Plaza pictures
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.salitreplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 4); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        //UN CyT pictures
        stream5 =  new ByteArrayOutputStream();
        BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.cytplace1).
                compress(Bitmap.CompressFormat.PNG, 100, stream5);
        image.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, stream5.toByteArray());
        image.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,10);
        image.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, 5); //This is only an example
        getActivity().getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                image
        );

        ContentValues values6 = new ContentValues();
        values6.put(RecappContract.TutorialEntry.COLUMN_NAME, "Bottles");
        values6.put(RecappContract.TutorialEntry.COLUMN_DESCRIPTION, "How to recycle bottles?");
        values6.put(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO, "https://www.youtube.com/watch?v=aLkoaiC48j0");
        getActivity().getContentResolver().insert(RecappContract.TutorialEntry.CONTENT_URI, values6);

    }


    public void setData(List<Place> places,Cursor cursor){
        recyclePlaceAdapter.swapData(places);
        recyclePlaceAdapter.setPlaceCursor(cursor);
    }
    public void closeData(){
        recyclePlaceAdapter.closeCursor();
    }




}
