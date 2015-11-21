package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.reminderApi.model.CollectionResponseReminder;
import com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder;
import com.unal.tuapp.recapp.backend.model.reminderApi.model.ReminderCollection;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class ReminderEndPoint extends AsyncTask<Pair<Context,Pair<Reminder,String>>,Void,Void> {

    @Override
    protected Void doInBackground(Pair<Context, Pair<Reminder, String>>... pairs) {
        try{
            switch (pairs[0].second.second){
                case "addReminder":
                    Utility.getReminderApi().insert(pairs[0].second.first).execute();
                    break;
                case "deleteReminder":
                    Utility.getReminderApi().remove(pairs[0].second.first.getId()).execute();
                    break;
                case "getReminderUser":
                    ReminderCollection collectionResponseReminder = Utility.getReminderApi().listUser(pairs[0].second.first.getUserId()).execute();
                    List<Reminder> reminderList = collectionResponseReminder.getItems();
                    if(reminderList!=null){
                        List<ContentValues> valuesList = new ArrayList<>();
                        List<String> ids = new ArrayList<>();
                        String query = RecappContract.ReminderEntry._ID + " NOT IN (";
                        for (Reminder i: reminderList){
                            query+=" ?,";
                            ids.add(i.getId() + "");
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.ReminderEntry._ID,i.getId());
                            value.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY,i.getPlaceId());
                            value.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,i.getUserId());
                            value.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION,i.getDescription());
                            value.put(RecappContract.ReminderEntry.COLUMN_END_DATE,i.getEndDate());
                            value.put(RecappContract.ReminderEntry.COLUMN_NAME,i.getName());
                            value.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION,i.getNotification());
                            valuesList.add(value);
                        }
                        if(!valuesList.isEmpty()){
                            String queryArgs [] = new String[ids.size()];
                            ContentValues values [] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            ids.toArray(queryArgs);
                            query = query.substring(0,query.length()-1);
                            query+=")";
                            pairs[0].first.getContentResolver().delete(
                                    RecappContract.ReminderEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            pairs[0].first.getContentResolver().bulkInsert(
                                    RecappContract.ReminderEntry.CONTENT_URI,
                                    values
                            );
                        }
                    }
                    break;
                case "getAllReminders":
                    CollectionResponseReminder collectionResponseReminderAll = Utility.getReminderApi().list().execute();
                    List<Reminder> reminderListAll ;
                    String nextPage = "";
                    if(collectionResponseReminderAll.getNextPageToken()!=null) {
                        while (!collectionResponseReminderAll.getNextPageToken().equals(nextPage)) {
                            reminderListAll = collectionResponseReminderAll.getItems();
                            if (reminderListAll != null) {
                                List<ContentValues> valuesList = new ArrayList<>();
                                for (Reminder i : reminderListAll) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.ReminderEntry._ID, i.getId());
                                    value.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION, i.getNotification());
                                    value.put(RecappContract.ReminderEntry.COLUMN_NAME, i.getName());
                                    value.put(RecappContract.ReminderEntry.COLUMN_END_DATE, i.getEndDate());
                                    value.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION, i.getDescription());
                                    value.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                    value.put(RecappContract.ReminderEntry.COLUMN_USER_KEY, i.getUserId());
                                    valuesList.add(value);
                                }
                                ContentValues[] values = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.ReminderEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseReminderAll.getNextPageToken();
                                collectionResponseReminderAll = Utility.getReminderApi().list().setCursor(nextPage).execute();
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
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
