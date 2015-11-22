package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.placeApi.model.CollectionResponsePlace;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.backend.model.userApi.model.CollectionResponseUser;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class PlaceEndPoint extends AsyncTask<Pair<Context,Pair<Place,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<Place,String>>... pairs) {
        try{
            switch (pairs[0].second.second){
                case "getPlaces":
                    CollectionResponsePlace collectionResponseUser = Utility.getPlaceApi().list().execute();
                    List<Place> places;
                    List<String> ids = new ArrayList<>();
                    String query = RecappContract.PlaceEntry._ID + " NOT IN ( ";
                    String nextPage = "";
                    if(collectionResponseUser.getNextPageToken()!=null) {
                        while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                            places = collectionResponseUser.getItems();
                            List<ContentValues> valuesList = new ArrayList<>();
                            if (places != null) {
                                for (Place i : places) {
                                    query += "?,";
                                    ids.add(i.getId() + "");
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, Utility.decodeImage(i.getImageFavorite()));
                                    value.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, i.getDescription());
                                    value.put(RecappContract.PlaceEntry.COLUMN_NAME, i.getName());
                                    value.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, i.getAddress());
                                    value.put(RecappContract.PlaceEntry.COLUMN_EMAIL, i.getEmail());
                                    value.put(RecappContract.PlaceEntry.COLUMN_PASSWORD, i.getPassword());
                                    value.put(RecappContract.PlaceEntry._ID, i.getId());
                                    value.put(RecappContract.PlaceEntry.COLUMN_LAT, i.getLat());
                                    value.put(RecappContract.PlaceEntry.COLUMN_LOG, i.getLng());
                                    value.put(RecappContract.PlaceEntry.COLUMN_RATING, i.getRating());
                                    value.put(RecappContract.PlaceEntry.COLUMN_WEB, i.getWeb());
                                    valuesList.add(value);
                                }
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.PlaceEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseUser.getNextPageToken();
                                collectionResponseUser = Utility.getPlaceApi().list().setCursor(nextPage).execute();
                            }
                        }
                        query = query.substring(0, query.length() - 1);
                        query += ")";

                        String queryArgs[] = new String[ids.size()];
                        ids.toArray(queryArgs);
                        if (ids.isEmpty()) {
                            query = null;
                            queryArgs = null;
                        }
                        pairs[0].first.getContentResolver().delete(
                                RecappContract.PlaceEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                    }

                    break;
                case "updatePlaceRating":
                    Place place = Utility.getPlaceApi().get(pairs[0].second.first.getId()).execute();
                    place.setRating(pairs[0].second.first.getRating());
                    Utility.getPlaceApi().update(place.getId(),place).execute();
                    break;
                case "updatePlace":
                    Place placeUpdate = Utility.getPlaceApi().get(pairs[0].second.first.getId()).execute();
                    pairs[0].second.first.setEmail(placeUpdate.getEmail());
                    pairs[0].second.first.setPassword(placeUpdate.getPassword());
                    pairs[0].second.first.setRating(placeUpdate.getRating());
                    Utility.getPlaceApi().update(pairs[0].second.first.getId(),pairs[0].second.first).execute();
                    break;
            }
        }catch (IOException e) {
            Log.e("erorr", e.toString());
        }


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
