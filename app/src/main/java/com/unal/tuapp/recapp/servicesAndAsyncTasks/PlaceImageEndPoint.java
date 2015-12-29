package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.placeImageApi.model.CollectionResponsePlaceImage;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.PlaceImage;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.PlaceImageCollection;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class PlaceImageEndPoint extends AsyncTask<Pair<Pair<Context,Long>,Pair<PlaceImage,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Pair<Context,Long>, Pair<PlaceImage, String>>... pairs) {
        try {
            switch (pairs[0].second.second) {
                case "getAllImagesPlace":
                    CollectionResponsePlaceImage collectionResponsePlaceImage = Utility.getPlaceImageApi().list().execute();
                    List<PlaceImage> placeImageList ;
                    String nextPage = "";
                    if(collectionResponsePlaceImage.getNextPageToken()!=null) {
                        while (!collectionResponsePlaceImage.getNextPageToken().equals(nextPage)) {
                            placeImageList = collectionResponsePlaceImage.getItems();
                            if (placeImageList != null) {
                                List<ContentValues> valuesList = new ArrayList<>();
                                for (PlaceImage i : placeImageList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.PlaceImageEntry._ID, i.getId());
                                    value.put(RecappContract.PlaceImageEntry.COLUMN_WORTH, i.getWorth());
                                    value.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                    value.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                                    value.put(RecappContract.COLUMN_IS_SEND,1);
                                    valuesList.add(value);
                                }
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.first.getContentResolver().bulkInsert(
                                        RecappContract.PlaceImageEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponsePlaceImage.getNextPageToken();
                                collectionResponsePlaceImage = Utility.getPlaceImageApi().list().setCursor(nextPage).execute();
                            }
                        }
                    }
                    break;

                case "getImagesPlace":
                    Long id = pairs[0].first.second;
                    PlaceImageCollection images = Utility.getPlaceImageApi().listPlaces(id).execute();
                    if(images.getItems()!=null) {
                        List<ContentValues> valuesList = new ArrayList<>();
                        List<String> ids =  new ArrayList<>();
                        String query = RecappContract.PlaceEntry._ID + " NOT IN ( ";
                        for (PlaceImage i : images.getItems()) {
                            query += " ?,";
                            ids.add("" + i.getId());
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, id);
                            value.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                            value.put(RecappContract.PlaceImageEntry.COLUMN_WORTH, i.getWorth());
                            value.put(RecappContract.PlaceImageEntry._ID, i.getId());
                            value.put(RecappContract.COLUMN_IS_SEND,1);
                            valuesList.add(value);
                        }
                        if(!valuesList.isEmpty()) {
                            query = query.trim().substring(0,query.length()-1);
                            query +=")";
                            String queryArgs [] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            pairs[0].first.first.getContentResolver().delete(
                                    RecappContract.PlaceImageEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            valuesList.toArray(values);
                            pairs[0].first.first.getContentResolver().bulkInsert(
                                    RecappContract.PlaceImageEntry.CONTENT_URI,
                                    values
                            );
                        }
                    }
                    break;
                case "addImage":
                    Utility.getPlaceImageApi().insert(pairs[0].second.first).execute();
                    break;
                case "deleteImage":
                    Utility.getPlaceImageApi().remove(pairs[0].second.first.getId()).execute();
                    break;



            }
        }catch (IOException e){
            Log.e("error",e.toString());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
