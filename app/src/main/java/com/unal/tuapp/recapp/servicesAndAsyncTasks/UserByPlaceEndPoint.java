package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.CollectionResponseUserByPlace;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.UserByPlace;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.UserByPlaceCollection;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class UserByPlaceEndPoint extends AsyncTask<Pair<Context,Pair<UserByPlace,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<UserByPlace,String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "addUserByPlace":
                    Utility.getUserByPlaceApi().insert(pairs[0].second.first).execute();
                    break;
                case "deleteUserByPlace":
                    Utility.getUserByPlaceApi().remove(pairs[0].second.first.getId()).execute();
                    break;
                case "getUserByPlaceUser":
                    UserByPlaceCollection userByPlaceCollection =Utility.getUserByPlaceApi().listUser(pairs[0].second.first.getUserId()).execute();
                    List<UserByPlace> userByPlaceList = userByPlaceCollection.getItems();
                    if(userByPlaceList!=null){
                        List<String> ids = new ArrayList<>();
                        String query = RecappContract.UserByPlaceEntry._ID + " NOT IN ( ";
                        List<ContentValues> valuesList = new ArrayList<>();
                        for(UserByPlace i: userByPlaceList){
                            ids.add(i.getId()+"");
                            query+="?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.UserByPlaceEntry._ID,i.getId());
                            value.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY,i.getPlaceId());
                            value.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY,i.getUserId());
                            valuesList.add(value);
                        }
                        if(!valuesList.isEmpty()){
                            String queryArgs [] = new String[valuesList.size()];
                            ContentValues values [] = new ContentValues[valuesList.size()];
                            query = query.substring(0,query.length()-1);
                            query+=")";
                            ids.toArray(queryArgs);
                            valuesList.toArray(values);
                            pairs[0].first.getContentResolver().delete(
                                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            pairs[0].first.getContentResolver().bulkInsert(
                                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                                    values
                            );
                        }
                    }
                    break;
                case "getUserByPlace":
                    CollectionResponseUserByPlace collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().execute();
                    List<UserByPlace> userByPlaceListAll;
                    String nextPage = "";
                    while (!collectionResponseUserByPlace.getNextPageToken().equals(nextPage)){
                        userByPlaceListAll = collectionResponseUserByPlace.getItems();
                        if(userByPlaceListAll!=null){
                            List<ContentValues> valuesList = new ArrayList<>();
                            for (UserByPlace i:userByPlaceListAll){
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.UserByPlaceEntry._ID,i.getId());
                                value.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY,i.getUserId());
                                value.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY,i.getId());
                                valuesList.add(value);
                            }
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            pairs[0].first.getContentResolver().bulkInsert(
                                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                                    values
                            );
                            nextPage = collectionResponseUserByPlace.getNextPageToken();
                            collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().setCursor(nextPage).execute();
                        }
                    }
                    break;


            }
        }catch (IOException e){

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
