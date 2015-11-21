package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.eventApi.model.CollectionResponseEvent;
import com.unal.tuapp.recapp.backend.model.eventApi.model.Event;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class EventEndPoint extends AsyncTask <Pair<Context,Pair<Event,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<Event, String>>... pairs) {
        try{
            switch (pairs[0].second.second){
                case "addEvent":
                    Utility.getEventApi().insert(pairs[0].second.first).execute();
                    break;
                case "updateEvent":
                    Utility.getEventApi().update(pairs[0].second.first.getId(),pairs[0].second.first).execute();
                    break;
                case "deleteEvent":
                    Utility.getEventApi().remove(pairs[0].second.first.getId()).execute();
                    break;
                case "getEvents":
                    CollectionResponseEvent collectionResponseEvent = Utility.getEventApi().list().execute();
                    List<Event> eventList;

                    String nextPage = "";
                    if(collectionResponseEvent.getNextPageToken()!=null) {
                        while (!collectionResponseEvent.getNextPageToken().equals(nextPage)) {
                            List<ContentValues> valuesList = new ArrayList<>();
                            eventList = collectionResponseEvent.getItems();
                            if (eventList != null) {
                                for (Event i : eventList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.EventEntry._ID, i.getId());
                                    value.put(RecappContract.EventEntry.COLUMN_ADDRESS, i.getAddress());
                                    value.put(RecappContract.EventEntry.COLUMN_CREATOR, i.getCreator());
                                    value.put(RecappContract.EventEntry.COLUMN_DATE, i.getStartDate());
                                    value.put(RecappContract.EventEntry.COLUMN_DESCRIPTION, i.getDescription());
                                    value.put(RecappContract.EventEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                                    value.put(RecappContract.EventEntry.COLUMN_LAT, i.getLat());
                                    value.put(RecappContract.EventEntry.COLUMN_LOG, i.getLng());
                                    value.put(RecappContract.EventEntry.COLUMN_NAME, i.getName());
                                    valuesList.add(value);
                                }
                                nextPage = collectionResponseEvent.getNextPageToken();
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.EventEntry.CONTENT_URI,
                                        values
                                );
                                collectionResponseEvent = Utility.getEventApi().list().setCursor(nextPage).execute();
                            }

                        }
                    }
                    break;
            }
        }catch(IOException e){

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
