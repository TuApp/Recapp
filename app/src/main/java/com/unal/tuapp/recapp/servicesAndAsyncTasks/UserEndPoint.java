package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.userApi.model.CollectionResponseUser;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */

//This asynctask is for addUser and UpdateUser
public class UserEndPoint extends AsyncTask<Pair<Pair<Context,String>, Pair<User,String>>,Void,String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Pair<Pair<Context,String>, Pair<User, String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "addUser":
                    Utility.getUserApi().insert(pairs[0].second.first).execute();
                    break;
                case "updateUser":
                    //Log.e("algo","algo");
                    User user = pairs[0].second.first;
                    Utility.getUserApi().update(user.getId(),user).execute();
                    break;
                case "getUsers":
                    CollectionResponseUser collectionResponseUser = Utility.getUserApi().list().execute();
                    String nextPage = "";
                    List<User> users;
                    while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                        users = collectionResponseUser.getItems();
                        if(users!=null) {
                            List<ContentValues> valuesList = new ArrayList<>();
                            for (User i : users) {
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.UserEntry._ID, i.getId());
                                value.put(RecappContract.UserEntry.COLUMN_EMAIL, i.getEmail());
                                value.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME, i.getLastname());
                                value.put(RecappContract.UserEntry.COLUMN_USER_NAME, i.getName());
                                value.put(RecappContract.UserEntry.COLUMN_USER_IMAGE, Utility.decodeImage(i.getProfileImage()));
                                valuesList.add(value);
                            }
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            pairs[0].first.first.getContentResolver().bulkInsert(
                                    RecappContract.UserEntry.CONTENT_URI,
                                    values
                            );
                            nextPage = collectionResponseUser.getNextPageToken();
                            collectionResponseUser = Utility.getUserApi().list().setCursor(nextPage).execute();
                        }

                    }
                    break;
                case "getUser":
                    User temp = Utility.getUserApi().getEmail(pairs[0].first.second).execute();
                    if(temp!=null) {
                        ContentValues values = new ContentValues();
                        values.put(RecappContract.UserEntry.COLUMN_EMAIL,temp.getEmail());
                        values.put(RecappContract.UserEntry.COLUMN_USER_NAME,temp.getName());
                        values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME,temp.getLastname());
                        values.put(RecappContract.UserEntry._ID, temp.getId());
                        try {
                            pairs[0].first.first.getContentResolver().insert(
                                    RecappContract.UserEntry.CONTENT_URI,
                                    values
                            );
                        }catch (Exception e){}

                        return "success";
                    }
                    break;

            }

        } catch (IOException e) {
            Log.e("erorr",e.toString());
        }
        return "nothing";
    }

}
