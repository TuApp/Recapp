package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by andresgutierrez on 11/19/15.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       /* Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if(extras!=null && !extras.isEmpty()){
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                //showToast(extras.getString("message"));
                switch (extras.getString("message")){
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
                    case "deleteFavoritePlace":
                        getFavoritePlace();
                    case "user":
                        getUsers("user");
                        break;
                    case "deleteUser":
                        getUsers("deleteUser");

                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);


    }

    private void getCategories(String type){
        try {
            CollectionResponseCategory collectionResponseCategory = Utility.getCategoryApi().list().execute();
            List<Category> categoryList;
            String nextPage = "";
            List<String> ids =  new ArrayList<>();
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
                        value.put(RecappContract.COLUMN_IS_SEND,1);
                        valuesList.add(value);
                    }

                    nextPage = collectionResponseCategory.getNextPageToken();
                    collectionResponseCategory = Utility.getCategoryApi().list().setCursor(nextPage).execute();
                }
            }
            switch (type){
                case "category":
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.CategoryEntry.CONTENT_URI,
                            values
                    );
                    break;
                case "deleteCategory":
                    String idsDelete[] = new String[ids.size()];
                    where = where.substring(0,where.length()-1);
                    ids.toArray(idsDelete);
                    if(ids.isEmpty()) {
                        where = null;
                        idsDelete = null;
                    }

                    this.getContentResolver().delete(
                            RecappContract.CategoryEntry.CONTENT_URI,
                            where,
                            idsDelete
                    );

            }

        }catch (IOException e){

        }*/

    }

    /*private void getPlaces(String type){
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
                        query+="?,";
                        queryComment +="?,";
                        ids.add(i.getId()+"");
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.COLUMN_IS_SEND,1);
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
            switch (type){
                case "place":
                    if(!valuesList.isEmpty()) {
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
                                RecappContract.PlaceEntry.CONTENT_URI,
                                values
                        );
                    }
                    break;
                case "deletePlace":
                    if(!ids.isEmpty()) {
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
                        Cursor cursor = this.getContentResolver().query(
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
                        this.getContentResolver().delete(
                                RecappContract.CommentEntry.CONTENT_URI,
                                queryComment,
                                queryArgs
                        );
                        this.getContentResolver().delete(
                                RecappContract.PlaceEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                    }
                    break;
            }


        }catch (IOException e){

        }

    }

    private void getComments(String type){
        try {
            CollectionResponseComment collectionResponseComment = Utility.getCommentApi().list().execute();
            List<Comment> commentListAll;
            String nextPage = "";
            List<String> idsAll = new ArrayList<>();
            String queryAll = RecappContract.CommentEntry._ID + " NOT IN ( ";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponseComment.getNextPageToken()!=null) {
                while (!collectionResponseComment.getNextPageToken().equals(nextPage)) {
                    commentListAll = collectionResponseComment.getItems();
                    if (commentListAll != null) {
                        for (Comment i : commentListAll) {
                            queryAll += "?,";
                            idsAll.add(i.getId() + "");
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND,1);
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
                switch (type){
                    case "comment":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
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
                        this.getContentResolver().delete(
                                RecappContract.CommentEntry.CONTENT_URI,
                                queryAll,
                                idsListAll
                        );

                        break;
                }

            }
        }catch (IOException e){

        }

    }

    private void getUsers(String type){
        try {
            CollectionResponseUser collectionResponseUser = Utility.getUserApi().list().execute();
            String nextPage = "";
            List<User> users;
            List<String> ids = new ArrayList<>();
            List<ContentValues> valuesList = new ArrayList<>();
            String where = RecappContract.UserEntry._ID + " NOT IN ( ";
            while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                users = collectionResponseUser.getItems();
                if(users!=null) {
                    for (User i : users) {
                        ids.add(""+i.getId());
                        where += "?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.COLUMN_IS_SEND,1);
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
            switch (type){
                case "user":
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.UserEntry.CONTENT_URI,
                            values
                    );
                    break;
                case "deleteUser":
                    where = where.substring(0,where.length()-1);
                    where+=")";
                    String idsDelete [] = new String[ids.size()];
                    ids.toArray(idsDelete);
                    if(ids.isEmpty()) {
                        where = null;
                        idsDelete = null;
                    }
                    this.getContentResolver().delete(
                            RecappContract.UserEntry.CONTENT_URI,
                            where,
                            idsDelete
                    );

                    break;
            }
        }catch (IOException e){

        }

    }
    public void getEventByUser(String type){
        try {
            CollectionResponseEventByUser collectionResponseEventByUser = Utility.getEventByUserApi().list().execute();
            List<EventByUser> eventByUserList;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.EventByUserEntry._ID + " NOT IN ( ";
            String nextPage = "";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponseEventByUser.getNextPageToken()!=null) {
                while (!collectionResponseEventByUser.getNextPageToken().equals(nextPage)) {
                    eventByUserList = collectionResponseEventByUser.getItems();
                    if (eventByUserList != null) {

                        for (EventByUser i : eventByUserList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND,1);
                            value.put(RecappContract.EventByUserEntry._ID, i.getId());
                            value.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, i.getEventId());
                            value.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER, i.getEmail());
                            valuesList.add(value);
                        }

                        nextPage = collectionResponseEventByUser.getNextPageToken();
                        collectionResponseEventByUser = Utility.getEventByUserApi().list().setCursor(nextPage).execute();
                    }
                }
                switch (type){
                    case "eventByUser":
                        ContentValues[] values = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
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
                        this.getContentResolver().delete(
                                RecappContract.EventByUserEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                        break;

                }

            }
        }catch (IOException e){

        }

    }
    public void getEvents(String type){
        try{
            CollectionResponseEvent collectionResponseEvent = Utility.getEventApi().list().execute();
            List<Event> eventList;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.EventEntry._ID + " NOT IN ( ";
            String nextPage = "";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponseEvent.getNextPageToken()!=null) {
                while (!collectionResponseEvent.getNextPageToken().equals(nextPage)) {

                    eventList = collectionResponseEvent.getItems();
                    if (eventList != null) {
                        for (Event i : eventList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND,1);
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
                switch (type){
                    case "event":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
                                RecappContract.EventEntry.CONTENT_URI,
                                values
                        );
                        Intent newIntent = new Intent(this, Recapp.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Notification notification = new Notification.Builder(this)
                                .setContentTitle("There are new events")
                                .setContentText("which are waiting for you")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                .setLights(Color.RED, 3000, 3000)
                                .setSound(alarmSound)
                                .build();
                        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
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
                        Cursor cursor = this.getContentResolver().query(
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

                        this.getContentResolver().delete(
                                RecappContract.EventEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                        break;
                }



            }
        }catch (IOException e){

        }
    }
    public void getImagePlace(String type){
        try {
            CollectionResponsePlaceImage collectionResponsePlaceImage = Utility.getPlaceImageApi().list().execute();
            List<PlaceImage> placeImageList ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.PlaceImageEntry._ID + " NOT IN ( ";
            String nextPage = "";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponsePlaceImage.getNextPageToken()!=null) {
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
                switch (type){
                    case "imagePlace":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
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
                        this.getContentResolver().delete(
                                RecappContract.PlaceImageEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                        break;
                }

            }
        }catch (IOException e){

        }
    }

    public void getReminders(String type){
        try {
            CollectionResponseReminder collectionResponseReminderAll = Utility.getReminderApi().list().execute();
            List<Reminder> reminderListAll ;
            String nextPage = "";
            List<String> ids = new ArrayList<>();
            String query = RecappContract.ReminderEntry._ID + " NOT IN ( ";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponseReminderAll.getNextPageToken()!=null) {
                while (!collectionResponseReminderAll.getNextPageToken().equals(nextPage)) {
                    reminderListAll = collectionResponseReminderAll.getItems();
                    if (reminderListAll != null) {

                        for (Reminder i : reminderListAll) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.COLUMN_IS_SEND,1);
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
                switch (type){
                    case "reminder":
                        ContentValues[] values = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
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
                        this.getContentResolver().delete(
                                RecappContract.ReminderEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                        break;

                }

            }
        }catch (IOException e){}
    }

    public void getSubCategoryByPlace(String type){
        try {
            CollectionResponseSubCategoryByPlace collectionResponseSubCategoryByPlace =
                    Utility.getSubCategoryByPlaceApi().list().execute();
            List<SubCategoryByPlace> subCategoryByPlaceList;

            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryByPlaceEntry._ID + " NOT IN ( ";

            String nextPage = "";
            List<ContentValues> valuesList = new ArrayList<>();
            if (collectionResponseSubCategoryByPlace.getNextPageToken()!=null) {
                while (!collectionResponseSubCategoryByPlace.getNextPageToken().equals(nextPage)) {
                    subCategoryByPlaceList = collectionResponseSubCategoryByPlace.getItems();
                    if (subCategoryByPlaceList != null) {
                        for (SubCategoryByPlace i : subCategoryByPlaceList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.SubCategoryByPlaceEntry._ID, i.getId());
                            value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                            value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                            valuesList.add(value);
                        }

                        nextPage = collectionResponseSubCategoryByPlace.getNextPageToken();
                        collectionResponseSubCategoryByPlace = Utility.getSubCategoryByPlaceApi().list().setCursor(nextPage)
                                .execute();
                    }
                    switch (type){
                        case "subCategoryByPlace":
                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            this.getContentResolver().bulkInsert(
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
                            this.getContentResolver().delete(
                                    RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                                    query,
                                    queryArgs
                            );
                            break;

                    }


                }
            }
        }catch (IOException e){}
    }

    public void getSubCategoryByTutorial(String type){
        try {
            CollectionResponseSubCategoryByTutorial collectionResponseSubCategoryByTutorial =
                    Utility.getSubCategoryByTutorialApi().list().execute();
            List<SubCategoryByTutorial> subCategoryByTutorialList = new ArrayList<>() ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryByTutorialEntry._ID + " NOT IN ( ";
            List<ContentValues> valuesList = new ArrayList<>();

            String nextPage = "";
            if(collectionResponseSubCategoryByTutorial.getNextPageToken()!=null) {
                while (collectionResponseSubCategoryByTutorial.getNextPageToken().equals(nextPage)) {
                    subCategoryByTutorialList = collectionResponseSubCategoryByTutorial.getItems();
                    if (subCategoryByTutorialList != null) {
                        for (SubCategoryByTutorial i : subCategoryByTutorialList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
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
                switch (type){
                    case "subCategoryByTutorial":
                        ContentValues values[] = new ContentValues[subCategoryByTutorialList.size()];
                        subCategoryByTutorialList.toArray(values);
                        this.getContentResolver().bulkInsert(
                                RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                                values
                        );
                        break;
                    case "deleteSubCategoryByTutorial":
                        query = query.substring(0,query.length()-1);
                        query+=")";
                        String queryArgs []= new String[ids.size()];
                        ids.toArray(queryArgs);
                        if(ids.isEmpty()){
                            query = null;
                            queryArgs = null;
                        }
                        this.getContentResolver().delete(
                                RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                        break;
                }

            }

        }catch (IOException e){}
    }

    public void getSubCategory(String type){
        try{
            CollectionResponseSubCategory collectionResponseSubCategory = Utility.getSubCategoryApi().list().execute();
            List<SubCategory> subCategoryList ;

            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryEntry._ID + " NOT IN ( ";
            List<ContentValues> valuesList = new ArrayList<>();
            String nextPage = "";
            if(collectionResponseSubCategory.getNextPageToken()!=null) {
                while (!collectionResponseSubCategory.getNextPageToken().equals(nextPage)) {
                    subCategoryList = collectionResponseSubCategory.getItems();
                    if (subCategoryList != null) {
                        for (SubCategory i : subCategoryList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.SubCategoryEntry._ID, i.getId());
                            value.put(RecappContract.SubCategoryEntry.COLUMN_NAME, i.getName());
                            value.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, i.getCategoryId());
                            valuesList.add(value);
                        }
                        nextPage = collectionResponseSubCategory.getNextPageToken();

                        collectionResponseSubCategory = Utility.getSubCategoryApi().list().setCursor(nextPage).execute();
                    }
                }
                switch (type){
                    case "subCategory":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
                                RecappContract.SubCategoryEntry.CONTENT_URI,
                                values
                        );
                        break;
                    case "deleteSubCategory":
                        query = query.substring(0,query.length()-1);
                        query+= ")";
                        String queryArgs [] = new String[ids.size()];
                        ids.toArray(queryArgs);
                        if(ids.isEmpty()){
                            query=null;
                            queryArgs = null;
                        }
                        this.getContentResolver().delete(
                                RecappContract.SubCategoryEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                }
            }

        }catch (IOException e){}
    }

    public void getTutorial(String type){
        try {
            CollectionResponseTutorial collectionResponseTutorial = Utility.getTutorialApi().list().execute();
            List<Tutorial> tutorialList ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.TutorialEntry._ID + " NOT IN ( ";
            String nextPage = "";
            List<ContentValues> valuesList = new ArrayList<>();
            if(collectionResponseTutorial.getNextPageToken()!=null) {
                while (!collectionResponseTutorial.getNextPageToken().equals(nextPage)) {
                    tutorialList = collectionResponseTutorial.getItems();
                    if (tutorialList != null) {
                        for (Tutorial i : tutorialList) {
                            ids.add(i.getId() + "");
                            query += "?,";
                            ContentValues value = new ContentValues();
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
                switch (type){
                    case "tutorial":
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
                                RecappContract.TutorialEntry.CONTENT_URI,
                                values
                        );
                        break;
                    case "deleteTutorial":
                        query = query.substring(0,query.length()-1);
                        query+=")";
                        String queryArgs[] = new String[ids.size()];
                        ids.toArray(queryArgs);
                        if(ids.isEmpty()){
                            query = null;
                            queryArgs = null;
                        }
                        this.getContentResolver().delete(
                                RecappContract.TutorialEntry.CONTENT_URI,
                                query,
                                queryArgs
                        );
                }
            }

        }catch (IOException e){}
    }

    public void getFavoritePlace(){
        try {
            CollectionResponseUserByPlace collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().execute();
            List<UserByPlace> userByPlaceListAll;
            String nextPage = "";
            List<String> ids = new ArrayList<>();
            String query = RecappContract.UserByPlaceEntry._ID + " NOT IN ( ";
            if(collectionResponseUserByPlace.getNextPageToken()!=null) {
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
                            valuesList.add(value);
                        }
                        ContentValues values[] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        this.getContentResolver().bulkInsert(
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
                this.getContentResolver().delete(
                        RecappContract.UserByPlaceEntry.CONTENT_URI,
                        query,
                        queryArgs
                );
            }
        }catch (IOException e){}
    }*/
}

