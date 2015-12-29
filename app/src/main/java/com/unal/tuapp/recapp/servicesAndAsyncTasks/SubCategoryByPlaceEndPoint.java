package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.CollectionResponseSubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.PlacesFragment;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class SubCategoryByPlaceEndPoint extends AsyncTask<Pair<Context,Pair<SubCategoryByPlace,String>>,Void,Void> {

    public SubCategoryByPlaceEndPoint() {
    }


    @Override
    protected Void doInBackground(Pair<Context, Pair<SubCategoryByPlace, String>>... pairs) {
       try {
           switch (pairs[0].second.second) {
               case "getSubCategoryByPlace":
                   CollectionResponseSubCategoryByPlace collectionResponseSubCategoryByPlace =
                           Utility.getSubCategoryByPlaceApi().list().execute();
                   List<SubCategoryByPlace> subCategoryByPlaceList;

                   String nextPage = "";
                   if(collectionResponseSubCategoryByPlace.getNextPageToken()!=null) {
                       while (!collectionResponseSubCategoryByPlace.getNextPageToken().equals(nextPage)) {
                           subCategoryByPlaceList = collectionResponseSubCategoryByPlace.getItems();
                           List<ContentValues> valuesList = new ArrayList<>();
                           if (subCategoryByPlaceList != null) {
                               for (SubCategoryByPlace i : subCategoryByPlaceList) {
                                   ContentValues value = new ContentValues();
                                   value.put(RecappContract.SubCategoryByPlaceEntry._ID, i.getId());
                                   value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                   value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                                   value.put(RecappContract.COLUMN_IS_SEND,1);
                                   valuesList.add(value);
                               }
                               ContentValues values[] = new ContentValues[valuesList.size()];
                               valuesList.toArray(values);
                               pairs[0].first.getContentResolver().bulkInsert(
                                       RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                                       values
                               );
                               nextPage = collectionResponseSubCategoryByPlace.getNextPageToken();
                               collectionResponseSubCategoryByPlace = Utility.getSubCategoryByPlaceApi().list().setCursor(nextPage)
                                       .execute();
                           }

                       }
                   }

                   break;
           }
       }catch (IOException e){}


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
