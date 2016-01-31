package com.unal.tuapp.recapp.syncAdapter;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Recapp;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.Category;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.CollectionResponseCategory;
import com.unal.tuapp.recapp.backend.model.commentApi.model.CollectionResponseComment;
import com.unal.tuapp.recapp.backend.model.commentApi.model.Comment;
import com.unal.tuapp.recapp.backend.model.eventApi.model.CollectionResponseEvent;
import com.unal.tuapp.recapp.backend.model.eventApi.model.Event;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.CollectionResponseEventByUser;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.EventByUser;
import com.unal.tuapp.recapp.backend.model.placeApi.model.CollectionResponsePlace;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.CollectionResponsePlaceImage;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.PlaceImage;
import com.unal.tuapp.recapp.backend.model.reminderApi.model.CollectionResponseReminder;
import com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder;
import com.unal.tuapp.recapp.backend.model.statisticsApi.model.CollectionResponseStatistics;
import com.unal.tuapp.recapp.backend.model.statisticsApi.model.Statistics;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.CollectionResponseSubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.SubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.CollectionResponseSubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.CollectionResponseSubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.tutorialApi.model.CollectionResponseTutorial;
import com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial;
import com.unal.tuapp.recapp.backend.model.userApi.model.CollectionResponseUser;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.CollectionResponseUserByPlace;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.UserByPlace;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 12/26/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver mContentResolver;
    private boolean network;
    private boolean wifi;
    private boolean mobileData;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        wifi = Utility.isWifiAvailable(getContext());
        mobileData = Utility.isMobileAvailable(getContext());

        if(!Recapp.network){
            network = wifi;
        }else{
            network = wifi || mobileData;
        }
        if(bundle.containsKey("message")) { // Message from GCM
            String message = bundle.getString("message");
            switch (message){
                case "category":
                    getCategories("category");
                    break;
                case "deleteCategory":
                    getCategories("deleteCategory");
                    break;
                case "comment":
                    getComments("comment");
                    break;
                case "deleteComment":
                    getComments("deleteComment");
                    break;
                case "eventByUser":
                    getEventByUser("eventByUser");
                    break;
                case "deleteEventByUser":
                    getEventByUser("deleteEventByUser");
                    break;
                case "event":
                    getEvents("event");
                    break;
                case "deleteEvent":
                    getEvents("deleteEvent");
                    break;
                case "place":
                    getPlaces("place");
                    break;
                case "deletePlace":
                    getPlaces("deletePlace");
                    break;
                case "imagePlace":
                    getImagePlace("imagePlace");
                    break;
                case "deleteImagePlace":
                    getImagePlace("deleteImagePlace");
                    break;
                case "reminder":
                    getReminders("reminder");
                    break;
                case "deleteReminder":
                    getReminders("deleteReminder");
                case "subCategoryByPlace":
                    getSubCategoryByPlace("subCategoryByPlace");
                    break;
                case "deleteSubCategoryByPlace":
                    getSubCategoryByPlace("deleteSubCategoryByPlace");
                    break;
                case "subCategoryByTutorial":
                    getSubCategoryByTutorial("subCategoryByTutorial");
                    break;
                case "deleteSubCategoryByTutorial":
                    getSubCategoryByTutorial("deleteSubCategoryByTutorial");
                    break;
                case "subCategory":
                    getSubCategory("subCategory");
                    break;
                case "deleteSubCategory":
                    getSubCategory("deleteSubCategory");
                    break;
                case "tutorial":
                    getTutorial("tutorial");
                    break;
                case "deleteTutorial":
                    getTutorial("deleteTutorial");
                    break;
                case "favoritePlace":
                    getFavoritePlace();
                    break;
                case "user":
                    getUsers("user");
                    break;
                case "deleteUser":
                    getUsers("deleteUser");
                    break;
                case "statistics":
                    getStatistics("statistics");
                    break;
                case "deleteStatics":
                    getStatistics("deleteStatistics");
                    break;

            }
        }else{ //There is a network
            addCategories();
            addPlace();
            addComments();
            addUsers();
            addEvents();
            addEventByUser();
            addImage();
            addReminders();
            addSubCategory();
            addTutorials();
            addSubCategoryByPlace();
            addSubCategoryByTutorial();
            addFavoritePlace();
            addStatistics();

        }
    }
    private void addCategories(){
        if(network) {
            Cursor cursorCategories = mContentResolver.query(
                    RecappContract.CategoryEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + "=? ",
                    new String[]{"0"},
                    null
            );
            while (cursorCategories.moveToNext()) {
                Category category = new Category();
                category.setId(cursorCategories.getLong(cursorCategories.getColumnIndexOrThrow(RecappContract.CategoryEntry._ID)));
                category.setName(cursorCategories.getString(cursorCategories.getColumnIndexOrThrow(RecappContract.CategoryEntry.COLUMN_NAME)));
                category.setImage(Utility.encodeImage(cursorCategories.getBlob(cursorCategories.getColumnIndexOrThrow(RecappContract.CategoryEntry.COLUMN_IMAGE))));
                try {
                    Utility.getCategoryApi().insert(category).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorCategories.close();
        }
    }

    private void getCategories(String type){
        if(network) {
            try {
                CollectionResponseCategory collectionResponseCategory = Utility.getCategoryApi().list().execute();
                List<Category> categoryList;
                String nextPage = "";
                List<String> ids = new ArrayList<>();
                List<ContentValues> valuesList = new ArrayList<>();
                String where = RecappContract.CategoryEntry._ID + " NOT IN ( ";
                while (!collectionResponseCategory.getNextPageToken().equals(nextPage)) {
                    categoryList = collectionResponseCategory.getItems();
                    if (categoryList != null) {
                        for (Category i : categoryList) {
                            ids.add("" + i.getId());
                            where += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.CategoryEntry._ID, i.getId());
                            value.put(RecappContract.CategoryEntry.COLUMN_NAME, i.getName());
                            value.put(RecappContract.CategoryEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                            value.put(RecappContract.COLUMN_IS_SEND, 1);
                            valuesList.add(value);
                        }

                        nextPage = collectionResponseCategory.getNextPageToken();
                        collectionResponseCategory = Utility.getCategoryApi().list().setCursor(nextPage).execute();
                    }
                }
                switch (type) {
                    case "category":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        mContentResolver.bulkInsert(
                                RecappContract.CategoryEntry.CONTENT_URI,
                                values
                        );
                        break;
                    case "deleteCategory":
                        String idsDelete[] = new String[ids.size()];
                        where = where.substring(0, where.length() - 1);
                        ids.toArray(idsDelete);
                        if (ids.isEmpty()) {
                            where = null;
                            idsDelete = null;
                        }

                        mContentResolver.delete(
                                RecappContract.CategoryEntry.CONTENT_URI,
                                where,
                                idsDelete
                        );

                }

            } catch (IOException e) {

            }
        }

    }

    private void addPlace(){
        if(network) {
            Cursor cursorPlace = mContentResolver.query(
                    RecappContract.PlaceEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorPlace.moveToNext()) {
                try {
                    Place place = Utility.getPlaceApi().get(cursorPlace.getLong(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry._ID))).execute();
                    if (place == null) { // This is a new place
                        place = new Place();
                        place.setPoints(0l);
                    }
                    place.setId(cursorPlace.getLong(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry._ID)));
                    place.setName(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_NAME)));
                    place.setRating(cursorPlace.getFloat(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_RATING)));
                    place.setDescription(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_DESCRIPTION)));
                    place.setAddress(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_ADDRESS)));
                    place.setEmail(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_EMAIL)));
                    place.setPassword(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_PASSWORD)));
                    place.setLat(cursorPlace.getFloat(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LAT)));
                    place.setLng(cursorPlace.getFloat(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LOG)));
                    place.setWeb(cursorPlace.getString(cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_WEB)));
                    place.setImageFavorite(Utility.encodeImage(cursorPlace.getBlob(
                            cursorPlace.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)
                    )));
                    Utility.getPlaceApi().insert(place).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursorPlace.close();
            }
        }
    }

    private void getPlaces(String type){
        if(network) {
            try {
                CollectionResponsePlace collectionResponseUser = Utility.getPlaceApi().list().execute();
                List<Place> places;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.PlaceEntry._ID + " NOT IN ( ";
                String queryComment = RecappContract.CommentEntry.COLUMN_PLACE_KEY + " NOT IN ( ";
                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                    places = collectionResponseUser.getItems();

                    if (places != null) {
                        for (Place i : places) {
                            query += "?,";
                            queryComment += "?,";
                            ids.add(i.getId() + "");
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND, 1);
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

                        nextPage = collectionResponseUser.getNextPageToken();
                        collectionResponseUser = Utility.getPlaceApi().list().setCursor(nextPage).execute();
                    }
                }
                switch (type) {
                    case "place":
                        if (!valuesList.isEmpty()) {
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.PlaceEntry.CONTENT_URI,
                                    values
                            );
                        }
                        break;
                    case "deletePlace":
                        if (!ids.isEmpty()) {
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            queryComment = queryComment.substring(0, queryComment.length() - 1);
                            queryComment += ")";

                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                                queryComment = null;
                            }
                            Cursor cursor = mContentResolver.query(
                                    RecappContract.PlaceEntry.CONTENT_URI,
                                    new String[]{RecappContract.PlaceEntry._ID},
                                    query,
                                    queryArgs,
                                    null
                            );
                            List<Long> idsComments = new ArrayList<>();
                            while (cursor.moveToNext()) {
                                idsComments.add(cursor.getLong(0));
                            }
                            if (!idsComments.isEmpty()) {
                                Utility.getCommentApi().removeComments(idsComments).execute();
                            }
                            mContentResolver.delete(
                                    RecappContract.CommentEntry.CONTENT_URI,
                                    queryComment,
                                    queryArgs
                            );
                            mContentResolver.delete(
                                    RecappContract.PlaceEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                        }
                        break;
                }


            } catch (IOException e) {

            }
        }

    }

    private void addComments(){
        if(network) {
            Cursor cursorComment = mContentResolver.query(
                    RecappContract.CommentEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorComment.moveToNext()) {
                Comment comment = new Comment();
                comment.setDescription(cursorComment.getString(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_DESCRIPTION)));
                comment.setId(cursorComment.getLong(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry._ID)));
                comment.setRating(cursorComment.getFloat(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_RATING)));
                comment.setUserId(cursorComment.getLong(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_USER_KEY)));
                comment.setPlaceId(cursorComment.getLong(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_PLACE_KEY)));
                comment.setDate(cursorComment.getLong(cursorComment.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_DATE)));
                try {
                    Utility.getCommentApi().insert(comment).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorComment.close();
        }
    }

    private void getComments(String type){
        if(network) {
            try {
                CollectionResponseComment collectionResponseComment = Utility.getCommentApi().list().execute();
                List<Comment> commentListAll;
                String nextPage = "";
                List<String> idsAll = new ArrayList<>();
                String queryAll = RecappContract.CommentEntry._ID + " NOT IN ( ";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseComment.getNextPageToken() != null) {
                    while (!collectionResponseComment.getNextPageToken().equals(nextPage)) {
                        commentListAll = collectionResponseComment.getItems();
                        if (commentListAll != null) {
                            for (Comment i : commentListAll) {
                                queryAll += "?,";
                                idsAll.add(i.getId() + "");
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, i.getDescription());
                                value.put(RecappContract.CommentEntry.COLUMN_DATE, i.getDate());
                                value.put(RecappContract.CommentEntry.COLUMN_RATING, i.getRating());
                                value.put(RecappContract.CommentEntry._ID, i.getId());
                                value.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                value.put(RecappContract.CommentEntry.COLUMN_USER_KEY, i.getUserId());
                                valuesList.add(value);
                            }

                            nextPage = collectionResponseComment.getNextPageToken();
                            collectionResponseComment = Utility.getCommentApi().list().setCursor(nextPage).execute();

                        }
                    }
                    switch (type) {
                        case "comment":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.CommentEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteComment":
                            String idsListAll[] = new String[idsAll.size()];
                            queryAll = queryAll.substring(0, queryAll.length() - 1);
                            queryAll += ")";
                            idsAll.toArray(idsListAll);
                            if (idsAll.isEmpty()) {
                                queryAll = null;
                                idsListAll = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.CommentEntry.CONTENT_URI,
                                    queryAll,
                                    idsListAll
                            );

                            break;
                    }

                }
            } catch (IOException e) {

            }
        }

    }

    private void addUsers(){
        if(network) {
            Cursor cursorUser = mContentResolver.query(
                    RecappContract.UserEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorUser.moveToNext()) {
                try {
                    User user = Utility.getUserApi().get(cursorUser.getLong(cursorUser.getColumnIndexOrThrow(RecappContract.UserEntry._ID))).execute();
                    if (user == null) {
                        user = new User();
                        user.setPoints(0l);
                    }
                    user.setName(cursorUser.getString(cursorUser.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME)));
                    user.setLastname(cursorUser.getString(cursorUser.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_LASTNAME)));
                    user.setEmail(cursorUser.getString(cursorUser.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_EMAIL)));
                    user.setProfileImage(Utility.encodeImage(cursorUser.getBlob(cursorUser.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE))));
                    Utility.getUserApi().insert(user).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorUser.close();
        }


    }

    private void getUsers(String type){
        if(network) {
            try {
                CollectionResponseUser collectionResponseUser = Utility.getUserApi().list().execute();
                String nextPage = "";
                List<User> users;
                List<String> ids = new ArrayList<>();
                List<ContentValues> valuesList = new ArrayList<>();
                String where = RecappContract.UserEntry._ID + " NOT IN ( ";
                while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                    users = collectionResponseUser.getItems();
                    if (users != null) {
                        for (User i : users) {
                            ids.add("" + i.getId());
                            where += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND, 1);
                            value.put(RecappContract.UserEntry._ID, i.getId());
                            value.put(RecappContract.UserEntry.COLUMN_EMAIL, i.getEmail());
                            value.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME, i.getLastname());
                            value.put(RecappContract.UserEntry.COLUMN_USER_NAME, i.getName());
                            value.put(RecappContract.UserEntry.COLUMN_USER_IMAGE, Utility.decodeImage(i.getProfileImage()));
                            valuesList.add(value);
                        }

                        nextPage = collectionResponseUser.getNextPageToken();
                        collectionResponseUser = Utility.getUserApi().list().setCursor(nextPage).execute();
                    }

                }
                switch (type) {
                    case "user":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        mContentResolver.bulkInsert(
                                RecappContract.UserEntry.CONTENT_URI,
                                values
                        );
                        break;
                    case "deleteUser":
                        where = where.substring(0, where.length() - 1);
                        where += ")";
                        String idsDelete[] = new String[ids.size()];
                        ids.toArray(idsDelete);
                        if (ids.isEmpty()) {
                            where = null;
                            idsDelete = null;
                        }
                        mContentResolver.delete(
                                RecappContract.UserEntry.CONTENT_URI,
                                where,
                                idsDelete
                        );

                        break;
                }
            } catch (IOException e) {

            }
        }

    }

    private void addEvents(){
        if(network) {
            Cursor cursorEvents = mContentResolver.query(
                    RecappContract.EventEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorEvents.moveToNext()) {
                Event event = new Event();
                event.setId(cursorEvents.getLong(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry._ID)));
                event.setName(cursorEvents.getString(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_NAME)));
                event.setDescription(cursorEvents.getString(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DESCRIPTION)));
                event.setAddress(cursorEvents.getString(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_ADDRESS)));
                event.setCreator(cursorEvents.getString(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_CREATOR)));
                event.setImage(Utility.encodeImage(cursorEvents.getBlob(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_IMAGE))));
                event.setLng(cursorEvents.getFloat(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LOG)));
                event.setLat(cursorEvents.getFloat(cursorEvents.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LAT)));
                try {
                    Utility.getEventApi().insert(event).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorEvents.close();
        }
    }

    private void getEvents(String type){
        if(network) {
            try {
                CollectionResponseEvent collectionResponseEvent = Utility.getEventApi().list().execute();
                List<Event> eventList;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.EventEntry._ID + " NOT IN ( ";
                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseEvent.getNextPageToken() != null) {
                    while (!collectionResponseEvent.getNextPageToken().equals(nextPage)) {

                        eventList = collectionResponseEvent.getItems();
                        if (eventList != null) {
                            for (Event i : eventList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
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
                            collectionResponseEvent = Utility.getEventApi().list().setCursor(nextPage).execute();
                        }

                    }
                    switch (type) {
                        case "event":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.EventEntry.CONTENT_URI,
                                    values
                            );
                            Intent newIntent = new Intent(getContext(), Recapp.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, newIntent, 0);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification notification = new Notification.Builder(getContext())
                                    .setContentTitle("There are new events")
                                    .setContentText("which are waiting for you")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(pendingIntent)
                                    .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                                    .setAutoCancel(true)
                                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                    .setLights(Color.RED, 3000, 3000)
                                    .setSound(alarmSound)
                                    .build();
                            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(-1, notification);
                            break;
                        case "deleteEvent":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String[] queryArgs = new String[ids.size()];
                            ids.toArray(queryArgs);

                            List<Long> idsUser = new ArrayList<>();
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            Cursor cursor = mContentResolver.query(
                                    RecappContract.EventByUserEntry.CONTENT_URI,
                                    new String[]{RecappContract.EventEntry._ID},
                                    query,
                                    queryArgs,
                                    null
                            );
                            while (cursor.moveToNext()) {
                                idsUser.add(cursor.getLong(0));
                            }
                            if (!idsUser.isEmpty()) {
                                for (Long i : idsUser) {
                                    Utility.getEventByUserApi().removeUsers(i);
                                }
                            }

                            mContentResolver.delete(
                                    RecappContract.EventEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;
                    }


                }
            } catch (IOException e) {

            }
        }
    }


    private void addEventByUser(){
        if(network) {
            Cursor cursorEventByUser = mContentResolver.query(
                    RecappContract.EventByUserEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorEventByUser.moveToNext()) {
                EventByUser eventByUser = new EventByUser();
                eventByUser.setId(cursorEventByUser.getLong(cursorEventByUser.getColumnIndexOrThrow(RecappContract.EventByUserEntry._ID)));
                eventByUser.setEmail(cursorEventByUser.getString(cursorEventByUser.getColumnIndexOrThrow(
                        RecappContract.EventByUserEntry.COLUMN_KEY_USER
                )));
                eventByUser.setEventId(cursorEventByUser.getLong(cursorEventByUser.getColumnIndexOrThrow(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT)));

                try {
                    Utility.getEventByUserApi().insert(eventByUser);
                } catch (IOException e) {

                }
            }
            cursorEventByUser.close();
        }
    }

    private void getEventByUser(String type){
        if(network) {
            try {
                CollectionResponseEventByUser collectionResponseEventByUser = Utility.getEventByUserApi().list().execute();
                List<EventByUser> eventByUserList;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.EventByUserEntry._ID + " NOT IN ( ";
                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseEventByUser.getNextPageToken() != null) {
                    while (!collectionResponseEventByUser.getNextPageToken().equals(nextPage)) {
                        eventByUserList = collectionResponseEventByUser.getItems();
                        if (eventByUserList != null) {

                            for (EventByUser i : eventByUserList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.EventByUserEntry._ID, i.getId());
                                value.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, i.getEventId());
                                value.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER, i.getEmail());
                                valuesList.add(value);
                            }

                            nextPage = collectionResponseEventByUser.getNextPageToken();
                            collectionResponseEventByUser = Utility.getEventByUserApi().list().setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "eventByUser":
                            ContentValues[] values = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.EventByUserEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteEventByUser":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.EventByUserEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;

                    }

                }
            } catch (IOException e) {

            }
        }

    }

    private void addImage(){
        if(network) {
            Cursor cursorImagePlace = mContentResolver.query(
                    RecappContract.PlaceImageEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorImagePlace.moveToNext()) {
                PlaceImage placeImage = new PlaceImage();
                placeImage.setId(cursorImagePlace.getLong(cursorImagePlace.getColumnIndexOrThrow(RecappContract.PlaceImageEntry._ID)));
                placeImage.setPlaceId(cursorImagePlace.getLong(cursorImagePlace.getColumnIndexOrThrow(
                        RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY
                )));
                placeImage.setWorth(cursorImagePlace.getInt(cursorImagePlace.getColumnIndexOrThrow(RecappContract.PlaceImageEntry.COLUMN_WORTH)));
                placeImage.setImage(Utility.encodeImage(cursorImagePlace.getBlob(cursorImagePlace.getColumnIndexOrThrow(
                        RecappContract.PlaceImageEntry.COLUMN_IMAGE
                ))));
                try {
                    Utility.getPlaceImageApi().insert(placeImage).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            cursorImagePlace.close();
        }
    }

    private void getImagePlace(String type){
        if(network) {
            try {
                CollectionResponsePlaceImage collectionResponsePlaceImage = Utility.getPlaceImageApi().list().execute();
                List<PlaceImage> placeImageList;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.PlaceImageEntry._ID + " NOT IN ( ";
                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponsePlaceImage.getNextPageToken() != null) {
                    while (!collectionResponsePlaceImage.getNextPageToken().equals(nextPage)) {
                        placeImageList = collectionResponsePlaceImage.getItems();
                        if (placeImageList != null) {

                            for (PlaceImage i : placeImageList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.PlaceImageEntry._ID, i.getId());
                                value.put(RecappContract.PlaceImageEntry.COLUMN_WORTH, i.getWorth());
                                value.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                value.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                                valuesList.add(value);
                            }

                            nextPage = collectionResponsePlaceImage.getNextPageToken();
                            collectionResponsePlaceImage = Utility.getPlaceImageApi().list().setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "imagePlace":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.PlaceImageEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteImagePlace":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.PlaceImageEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;
                    }

                }
            } catch (IOException e) {

            }
        }
    }

    private void addReminders(){
        if(network) {
            Cursor cursorReminders = mContentResolver.query(
                    RecappContract.ReminderEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorReminders.moveToNext()) {
                Reminder reminder = new Reminder();
                reminder.setId(cursorReminders.getLong(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry._ID)));
                reminder.setPlaceId(cursorReminders.getLong(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_PLACE_KEY)));
                reminder.setUserId(cursorReminders.getLong(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_USER_KEY)));
                reminder.setName(cursorReminders.getString(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_NAME)));
                reminder.setDescription(cursorReminders.getString(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_DESCRIPTION)));
                reminder.setEndDate(cursorReminders.getLong(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_END_DATE)));
                reminder.setNotification(cursorReminders.getLong(cursorReminders.getColumnIndexOrThrow(RecappContract.ReminderEntry.COLUMN_NOTIFICATION)));
                try {
                    Utility.getReminderApi().insert(reminder).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorReminders.close();
        }

    }

    private void getReminders(String type){
        if(network) {
            try {
                CollectionResponseReminder collectionResponseReminderAll = Utility.getReminderApi().list().execute();
                List<Reminder> reminderListAll;
                String nextPage = "";
                List<String> ids = new ArrayList<>();
                String query = RecappContract.ReminderEntry._ID + " NOT IN ( ";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseReminderAll.getNextPageToken() != null) {
                    while (!collectionResponseReminderAll.getNextPageToken().equals(nextPage)) {
                        reminderListAll = collectionResponseReminderAll.getItems();
                        if (reminderListAll != null) {

                            for (Reminder i : reminderListAll) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.ReminderEntry._ID, i.getId());
                                value.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION, i.getNotification());
                                value.put(RecappContract.ReminderEntry.COLUMN_NAME, i.getName());
                                value.put(RecappContract.ReminderEntry.COLUMN_END_DATE, i.getEndDate());
                                value.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION, i.getDescription());
                                value.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                value.put(RecappContract.ReminderEntry.COLUMN_USER_KEY, i.getUserId());
                                valuesList.add(value);
                            }

                            nextPage = collectionResponseReminderAll.getNextPageToken();
                            collectionResponseReminderAll = Utility.getReminderApi().list().setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "reminder":
                            ContentValues[] values = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.ReminderEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteReminder":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.ReminderEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;

                    }

                }
            } catch (IOException e) {
            }
        }
    }

    private void addSubCategory(){
        if(network) {
            Cursor cursorSubCategories = mContentResolver.query(
                    RecappContract.SubCategoryEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorSubCategories.moveToNext()) {
                SubCategory subCategory = new SubCategory();
                subCategory.setId(cursorSubCategories.getLong(cursorSubCategories.getColumnIndexOrThrow(RecappContract.SubCategoryEntry._ID)));
                subCategory.setName(cursorSubCategories.getString(cursorSubCategories.getColumnIndexOrThrow(RecappContract.SubCategoryEntry.COLUMN_NAME)));
                subCategory.setCategoryId(cursorSubCategories.getLong(cursorSubCategories.getColumnIndexOrThrow(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY)));
                try {
                    Utility.getSubCategoryApi().insert(subCategory).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorSubCategories.close();
        }
    }

    private void getSubCategory(String type){
        if(network) {
            try {
                CollectionResponseSubCategory collectionResponseSubCategory = Utility.getSubCategoryApi().list().execute();
                List<SubCategory> subCategoryList;

                List<String> ids = new ArrayList<>();
                String query = RecappContract.SubCategoryEntry._ID + " NOT IN ( ";
                List<ContentValues> valuesList = new ArrayList<>();
                String nextPage = "";
                if (collectionResponseSubCategory.getNextPageToken() != null) {
                    while (!collectionResponseSubCategory.getNextPageToken().equals(nextPage)) {
                        subCategoryList = collectionResponseSubCategory.getItems();
                        if (subCategoryList != null) {
                            for (SubCategory i : subCategoryList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.SubCategoryEntry._ID, i.getId());
                                value.put(RecappContract.SubCategoryEntry.COLUMN_NAME, i.getName());
                                value.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, i.getCategoryId());
                                valuesList.add(value);
                            }
                            nextPage = collectionResponseSubCategory.getNextPageToken();

                            collectionResponseSubCategory = Utility.getSubCategoryApi().list().setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "subCategory":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.SubCategoryEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteSubCategory":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.SubCategoryEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                    }
                }

            } catch (IOException e) {
            }
        }
    }

    private void addTutorials(){
        if(network) {
            Cursor cursorTutorial = mContentResolver.query(
                    RecappContract.TutorialEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorTutorial.moveToNext()) {
                Tutorial tutorial = new Tutorial();
                tutorial.setId(cursorTutorial.getLong(cursorTutorial.getColumnIndexOrThrow(RecappContract.TutorialEntry._ID)));
                tutorial.setName(cursorTutorial.getString(cursorTutorial.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_NAME)));
                tutorial.setDescription(cursorTutorial.getString(cursorTutorial.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_DESCRIPTION)));
                tutorial.setLink(cursorTutorial.getString(cursorTutorial.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO)));

                try {
                    Utility.getTutorialApi().insert(tutorial).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorTutorial.close();
        }
    }

    private void getTutorial(String type){
        if(network) {
            try {
                CollectionResponseTutorial collectionResponseTutorial = Utility.getTutorialApi().list().execute();
                List<Tutorial> tutorialList;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.TutorialEntry._ID + " NOT IN ( ";
                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseTutorial.getNextPageToken() != null) {
                    while (!collectionResponseTutorial.getNextPageToken().equals(nextPage)) {
                        tutorialList = collectionResponseTutorial.getItems();
                        if (tutorialList != null) {
                            for (Tutorial i : tutorialList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.TutorialEntry._ID, i.getId());
                                value.put(RecappContract.TutorialEntry.COLUMN_NAME, i.getName());
                                value.put(RecappContract.TutorialEntry.COLUMN_DESCRIPTION, i.getDescription());
                                value.put(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO, i.getLink());
                                valuesList.add(value);
                            }
                            nextPage = collectionResponseTutorial.getNextPageToken();

                            collectionResponseTutorial = Utility.getTutorialApi().list().setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "tutorial":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.TutorialEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteTutorial":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.TutorialEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                    }
                }

            } catch (IOException e) {
            }
        }
    }

    private void addSubCategoryByPlace(){
        if(network) {
            Cursor cursorSubCategoryByPlace = mContentResolver.query(
                    RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorSubCategoryByPlace.moveToNext()) {
                SubCategoryByPlace subCategoryByPlace = new SubCategoryByPlace();
                subCategoryByPlace.setId(cursorSubCategoryByPlace.getLong(cursorSubCategoryByPlace.getColumnIndexOrThrow(RecappContract.SubCategoryByPlaceEntry._ID)));
                subCategoryByPlace.setPlaceId(cursorSubCategoryByPlace.getLong(cursorSubCategoryByPlace.getColumnIndexOrThrow(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY)));
                subCategoryByPlace.setSubCategoryId(cursorSubCategoryByPlace.getLong(cursorSubCategoryByPlace.getColumnIndexOrThrow(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY)));

                try {
                    Utility.getSubCategoryByPlaceApi().insert(subCategoryByPlace).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorSubCategoryByPlace.close();
        }
    }

    private void getSubCategoryByPlace(String type){
        if(network) {
            try {
                CollectionResponseSubCategoryByPlace collectionResponseSubCategoryByPlace =
                        Utility.getSubCategoryByPlaceApi().list().execute();
                List<SubCategoryByPlace> subCategoryByPlaceList;

                List<String> ids = new ArrayList<>();
                String query = RecappContract.SubCategoryByPlaceEntry._ID + " NOT IN ( ";

                String nextPage = "";
                List<ContentValues> valuesList = new ArrayList<>();
                if (collectionResponseSubCategoryByPlace.getNextPageToken() != null) {
                    while (!collectionResponseSubCategoryByPlace.getNextPageToken().equals(nextPage)) {
                        subCategoryByPlaceList = collectionResponseSubCategoryByPlace.getItems();
                        if (subCategoryByPlaceList != null) {
                            for (SubCategoryByPlace i : subCategoryByPlaceList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.SubCategoryByPlaceEntry._ID, i.getId());
                                value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                                valuesList.add(value);
                            }

                            nextPage = collectionResponseSubCategoryByPlace.getNextPageToken();
                            collectionResponseSubCategoryByPlace = Utility.getSubCategoryByPlaceApi().list().setCursor(nextPage)
                                    .execute();
                        }
                        switch (type) {
                            case "subCategoryByPlace":
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                mContentResolver.bulkInsert(
                                        RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                                        values
                                );
                                break;
                            case "deleteSubCategoryByPlace":
                                query = query.substring(0, query.length() - 1);
                                query += ")";
                                String queryArgs[] = new String[ids.size()];
                                ids.toArray(queryArgs);
                                if (ids.isEmpty()) {
                                    query = null;
                                    queryArgs = null;
                                }
                                mContentResolver.delete(
                                        RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                                        query,
                                        queryArgs
                                );
                                break;

                        }


                    }
                }
            } catch (IOException e) {
            }
        }
    }

    private void addSubCategoryByTutorial(){
        if(network) {
            Cursor cursorSubCategoryByTutorial = mContentResolver.query(
                    RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorSubCategoryByTutorial.moveToNext()) {
                SubCategoryByTutorial subCategoryByTutorial = new SubCategoryByTutorial();
                subCategoryByTutorial.setId(cursorSubCategoryByTutorial.getLong(cursorSubCategoryByTutorial.getColumnIndexOrThrow(
                        RecappContract.SubCategoryByTutorialEntry._ID
                )));
                subCategoryByTutorial.setSubCategoryId(cursorSubCategoryByTutorial.getLong(cursorSubCategoryByTutorial.getColumnIndexOrThrow(
                        RecappContract.SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY
                )));
                subCategoryByTutorial.setTutorialId(cursorSubCategoryByTutorial.getLong(cursorSubCategoryByTutorial.getColumnIndexOrThrow(
                        RecappContract.SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY
                )));
                try {
                    Utility.getSubCategoryByTutorialApi().insert(subCategoryByTutorial).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorSubCategoryByTutorial.close();
        }
    }

    private void getSubCategoryByTutorial(String type){
        if(network) {
            try {
                CollectionResponseSubCategoryByTutorial collectionResponseSubCategoryByTutorial =
                        Utility.getSubCategoryByTutorialApi().list().execute();
                List<SubCategoryByTutorial> subCategoryByTutorialList = new ArrayList<>();
                List<String> ids = new ArrayList<>();
                String query = RecappContract.SubCategoryByTutorialEntry._ID + " NOT IN ( ";
                List<ContentValues> valuesList = new ArrayList<>();

                String nextPage = "";
                if (collectionResponseSubCategoryByTutorial.getNextPageToken() != null) {
                    while (collectionResponseSubCategoryByTutorial.getNextPageToken().equals(nextPage)) {
                        subCategoryByTutorialList = collectionResponseSubCategoryByTutorial.getItems();
                        if (subCategoryByTutorialList != null) {
                            for (SubCategoryByTutorial i : subCategoryByTutorialList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                value.put(RecappContract.SubCategoryByTutorialEntry._ID, i.getId());
                                value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                                value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY, i.getTutorialId());
                                valuesList.add(value);
                            }

                            nextPage = collectionResponseSubCategoryByTutorial.getNextPageToken();
                            collectionResponseSubCategoryByTutorial = Utility.getSubCategoryByTutorialApi().list()
                                    .setCursor(nextPage).execute();
                        }
                    }
                    switch (type) {
                        case "subCategoryByTutorial":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            subCategoryByTutorialList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                                    values
                            );
                            break;
                        case "deleteSubCategoryByTutorial":
                            query = query.substring(0, query.length() - 1);
                            query += ")";
                            String queryArgs[] = new String[ids.size()];
                            ids.toArray(queryArgs);
                            if (ids.isEmpty()) {
                                query = null;
                                queryArgs = null;
                            }
                            mContentResolver.delete(
                                    RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;
                    }

                }

            } catch (IOException e) {
            }
        }
    }


    private void addFavoritePlace(){
        if(network) {
            Cursor cursorFavoritePlace = mContentResolver.query(
                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorFavoritePlace.moveToNext()) {
                UserByPlace userByPlace = new UserByPlace();
                userByPlace.setId(cursorFavoritePlace.getLong(cursorFavoritePlace.getColumnIndexOrThrow(RecappContract.UserByPlaceEntry._ID)));
                userByPlace.setPlaceId(cursorFavoritePlace.getLong(cursorFavoritePlace.getColumnIndexOrThrow(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY)));
                userByPlace.setUserId(cursorFavoritePlace.getLong(cursorFavoritePlace.getColumnIndexOrThrow(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY)));
                try {
                    Utility.getUserByPlaceApi().insert(userByPlace).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorFavoritePlace.close();
        }
    }

    private void getFavoritePlace(){
        if(network) {
            try {
                CollectionResponseUserByPlace collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().execute();
                List<UserByPlace> userByPlaceListAll;
                String nextPage = "";
                List<String> ids = new ArrayList<>();
                String query = RecappContract.UserByPlaceEntry._ID + " NOT IN ( ";
                if (collectionResponseUserByPlace.getNextPageToken() != null) {
                    while (!collectionResponseUserByPlace.getNextPageToken().equals(nextPage)) {
                        userByPlaceListAll = collectionResponseUserByPlace.getItems();
                        if (userByPlaceListAll != null) {
                            List<ContentValues> valuesList = new ArrayList<>();
                            for (UserByPlace i : userByPlaceListAll) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues value = new ContentValues();
                                value.put(RecappContract.UserByPlaceEntry._ID, i.getId());
                                value.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY, i.getUserId());
                                value.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                value.put(RecappContract.COLUMN_IS_SEND, 1);
                                valuesList.add(value);
                            }
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            mContentResolver.bulkInsert(
                                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                                    values
                            );
                            nextPage = collectionResponseUserByPlace.getNextPageToken();
                            collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().setCursor(nextPage).execute();
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
                    mContentResolver.delete(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            query,
                            queryArgs
                    );
                }
            } catch (IOException e) {
            }
        }
    }

    private void addStatistics(){
        if(network) {
            Cursor cursorStatistics = mContentResolver.query(
                    RecappContract.StatisticsEntry.CONTENT_URI,
                    null,
                    RecappContract.COLUMN_IS_SEND + " =? ",
                    new String[]{"0"},
                    null
            );
            while (cursorStatistics.moveToNext()) {
                Statistics statistics = new Statistics();
                statistics.setId(cursorStatistics.getLong(cursorStatistics.getColumnIndexOrThrow(RecappContract.StatisticsEntry._ID)));
                statistics.setUserId(cursorStatistics.getLong(cursorStatistics.getColumnIndexOrThrow(RecappContract.StatisticsEntry.COLUMN_KEY_USER)));
                statistics.setDate(cursorStatistics.getLong(cursorStatistics.getColumnIndexOrThrow(RecappContract.StatisticsEntry.COLUMN_DATE)));
                statistics.setPoints(cursorStatistics.getLong(cursorStatistics.getColumnIndexOrThrow(RecappContract.StatisticsEntry.COLUMN_POINT)));
                try {
                    Utility.getStatisticsApi().insert(statistics).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursorStatistics.close();
        }
    }

    private void getStatistics(String type){
        if(network) {
            try {
                CollectionResponseStatistics collectionResponseStatistics =
                        Utility.getStatisticsApi().list().execute();
                List<Statistics> statisticsList;
                List<String> ids = new ArrayList<>();
                String query = RecappContract.StatisticsEntry._ID + " NOT IN ( ";
                List<ContentValues> valuesList = new ArrayList<>();

                String nextPage = "";

                if (collectionResponseStatistics.getNextPageToken() != null) {
                    while (!collectionResponseStatistics.getNextPageToken().equals(nextPage)) {
                        statisticsList = collectionResponseStatistics.getItems();
                        if (statisticsList != null) {
                            for (Statistics i : statisticsList) {
                                ids.add(i.getId() + "");
                                query += "?,";
                                ContentValues values = new ContentValues();
                                values.put(RecappContract.COLUMN_IS_SEND, 1);
                                values.put(RecappContract.StatisticsEntry._ID, i.getId());
                                values.put(RecappContract.StatisticsEntry.COLUMN_KEY_USER, i.getUserId());
                                values.put(RecappContract.StatisticsEntry.COLUMN_DATE, i.getDate());
                                values.put(RecappContract.StatisticsEntry.COLUMN_POINT, i.getPoints());
                                valuesList.add(values);
                            }
                            nextPage = collectionResponseStatistics.getNextPageToken();
                            collectionResponseStatistics = Utility.getStatisticsApi().list()
                                    .setCursor(nextPage).execute();

                        }
                        switch (type) {
                            case "statistics":
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                mContentResolver.bulkInsert(
                                        RecappContract.StatisticsEntry.CONTENT_URI,
                                        values
                                );
                                break;
                            case "deleteStatistics":
                                query = query.substring(0, query.length() - 1);
                                query += ")";
                                String queryArgs[] = new String[ids.size()];
                                ids.toArray(queryArgs);
                                if (ids.isEmpty()) {
                                    query = null;
                                    queryArgs = null;
                                }
                                mContentResolver.delete(
                                        RecappContract.StatisticsEntry.CONTENT_URI,
                                        query,
                                        queryArgs
                                );
                                break;
                        }
                    }
                }

            } catch (IOException e) {
            }
        }

    }
}
