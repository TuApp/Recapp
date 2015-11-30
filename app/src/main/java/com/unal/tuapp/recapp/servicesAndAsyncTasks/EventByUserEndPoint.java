package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.activities.Recapp;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.CollectionResponseEventByUser;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.EventByUser;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class EventByUserEndPoint extends AsyncTask<Pair<Context,Pair<EventByUser,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<EventByUser, String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "addEventByUser":
                    Utility.getEventByUserApi().insert(pairs[0].second.first).execute();
                    break;
                case "deleteEventByUser":
                    Utility.getEventByUserApi().remove(pairs[0].second.first.getId());
                    break;
                case "deleteEventByUserEvent":
                    Utility.getEventByUserApi().removeUsers(pairs[0].second.first.getEventId()).execute();
                    break;
                case "getEventByUser":
                    CollectionResponseEventByUser collectionResponseEventByUser = Utility.getEventByUserApi().list().execute();
                    List<EventByUser> eventByUserList;
                    String nextPage = "";
                    if(collectionResponseEventByUser.getNextPageToken()!=null) {
                        while (!collectionResponseEventByUser.getNextPageToken().equals(nextPage)) {
                            eventByUserList = collectionResponseEventByUser.getItems();
                            if (eventByUserList != null) {
                                List<ContentValues> valuesList = new ArrayList<>();
                                for (EventByUser i : eventByUserList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.EventByUserEntry._ID, i.getId());
                                    value.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, i.getEventId());
                                    value.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER, i.getEmail());
                                    valuesList.add(value);
                                }
                                ContentValues[] values = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.EventByUserEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseEventByUser.getNextPageToken();
                                collectionResponseEventByUser = Utility.getEventByUserApi().list().setCursor(nextPage).execute();
                            }


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
        if(Recapp.initValue>0){
            Recapp.init.hide();
            Recapp.initValue = 0;
        }
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
