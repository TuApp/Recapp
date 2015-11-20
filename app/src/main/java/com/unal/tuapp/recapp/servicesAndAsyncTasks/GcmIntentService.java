package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
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
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if(extras!=null && !extras.isEmpty()){
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                //showToast(extras.getString("message"));
                switch (extras.getString("message")){
                    case "category":
                        getCategories();
                        break;
                    case "comment":
                        getComments();
                        break;
                    case "eventByUser":
                        getEventByUser();
                        break;
                    case "addEvent":
                        getEvents("addEvent");
                        break;
                    case "deleteEvent":
                        getEvents("deleteEvent");
                        break;
                    case "place":
                        getPlaces();
                        break;
                    case "imagePlace":
                        getImagePlace();
                        break;
                    case "reminder":
                        getReminders();
                        break;
                    case "subCategoryByPlace":
                        getSubCategoryByPlace();
                        break;
                    case "subCategoryByTutorial":
                        getSubCategoryByTutorial();
                        break;
                    case "subCategory":
                        getSubCategory();
                        break;
                    case "tutorial":
                        getTutorial();
                        break;
                    case "favoritePlace":
                        getFavoritePlace();
                        break;
                    case "user":
                        getUsers();
                        break;

                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);


    }
    /*protected void showToast(final String message) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });

    }*/
    private void getCategories(){
        try {
            CollectionResponseCategory collectionResponseCategory = Utility.getCategoryApi().list().execute();
            List<Category> categoryList;
            String nextPage = "";
            while (!collectionResponseCategory.getNextPageToken().equals(nextPage)) {
                categoryList = collectionResponseCategory.getItems();
                if (categoryList != null) {
                    List<ContentValues> valuesList = new ArrayList<>();
                    for (Category i : categoryList) {
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.CategoryEntry._ID, i.getId());
                        value.put(RecappContract.CategoryEntry.COLUMN_NAME, i.getName());
                        value.put(RecappContract.CategoryEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                        valuesList.add(value);
                    }
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.CategoryEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseCategory.getNextPageToken();
                    collectionResponseCategory = Utility.getCategoryApi().list().setCursor(nextPage).execute();
                }
            }

        }catch (IOException e){

        }

    }

    private void getPlaces(){
        try {
            CollectionResponsePlace collectionResponseUser = Utility.getPlaceApi().list().execute();
            List<Place> places;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.PlaceEntry._ID + " NOT IN ( ";
            String queryComment = RecappContract.CommentEntry.COLUMN_PLACE_KEY + " NOT IN ( ";
            String nextPage = "";
            while (!collectionResponseUser.getNextPageToken().equals(nextPage)) {
                places = collectionResponseUser.getItems();
                List<ContentValues> valuesList = new ArrayList<>();
                if (places != null) {
                    for (Place i : places) {
                        query+="?,";
                        queryComment ="?,";
                        ids.add(i.getId()+"");

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

                    this.getContentResolver().bulkInsert(
                            RecappContract.PlaceEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseUser.getNextPageToken();
                    collectionResponseUser = Utility.getPlaceApi().list().setCursor(nextPage).execute();
                }
            }
            query = query.substring(0,query.length()-1);
            query+=")";
            queryComment = queryComment.substring(0,queryComment.length()-1);
            queryComment+=")";

            String queryArgs [] = new String[ids.size()];
            ids.toArray(queryArgs);
            if(ids.isEmpty()){
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
            while (cursor.moveToNext()){
                idsComments.add(cursor.getLong(0));
            }
            if(!idsComments.isEmpty()){
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

        }catch (IOException e){

        }

    }

    private void getComments(){
        try {
            CollectionResponseComment collectionResponseComment = Utility.getCommentApi().list().execute();
            List<Comment> commentListAll;
            String nextPage = "";
            List<String> idsAll = new ArrayList<>();
            String queryAll = RecappContract.CommentEntry._ID + " NOT IN ( ";
            while (!collectionResponseComment.getNextPageToken().equals(nextPage)){
                commentListAll = collectionResponseComment.getItems();
                if(commentListAll!=null){
                    List<ContentValues> valuesList = new ArrayList<>();
                    for (Comment i: commentListAll){
                        queryAll+="?,";
                        idsAll.add(i.getId()+"");
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, i.getDescription());
                        value.put(RecappContract.CommentEntry.COLUMN_DATE, i.getDate());
                        value.put(RecappContract.CommentEntry.COLUMN_RATING, i.getRating());
                        value.put(RecappContract.CommentEntry._ID, i.getId());
                        value.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                        value.put(RecappContract.CommentEntry.COLUMN_USER_KEY, i.getUserId());
                        valuesList.add(value);
                    }
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.CommentEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseComment.getNextPageToken();
                    collectionResponseComment = Utility.getCommentApi().list().setCursor(nextPage).execute();

                }
            }
            String idsListAll[] = new String[idsAll.size()];
            queryAll=queryAll.substring(0,queryAll.length()-1);
            queryAll+=")";
            idsAll.toArray(idsListAll);
            if(idsAll.isEmpty()){
                queryAll = null;
                idsListAll = null;
            }
            this.getContentResolver().delete(
                    RecappContract.CommentEntry.CONTENT_URI,
                    queryAll,
                    idsListAll
            );
        }catch (IOException e){

        }

    }

    private void getUsers(){
        try {
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
                    this.getContentResolver().bulkInsert(
                            RecappContract.UserEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseUser.getNextPageToken();
                    collectionResponseUser = Utility.getUserApi().list().setCursor(nextPage).execute();
                }

            }
        }catch (IOException e){

        }

    }
    public void getEventByUser(){
        try {
            CollectionResponseEventByUser collectionResponseEventByUser = Utility.getEventByUserApi().list().execute();
            List<EventByUser> eventByUserList;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.EventByUserEntry._ID + " NOT IN ( ";
            String nextPage = "";
            while (!collectionResponseEventByUser.getNextPageToken().equals(nextPage)){
                eventByUserList = collectionResponseEventByUser.getItems();
                if(eventByUserList!=null){
                    List<ContentValues> valuesList = new ArrayList<>();
                    for (EventByUser i : eventByUserList){
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.EventByUserEntry._ID,i.getId());
                        value.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT,i.getEventId());
                        value.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER,i.getEmail());
                        valuesList.add(value);
                    }
                    ContentValues [] values = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.EventEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseEventByUser.getNextPageToken();
                    collectionResponseEventByUser = Utility.getEventByUserApi().list().setCursor(nextPage).execute();
                }
            }
            query = query.substring(0,query.length()-1);
            query = ")";
            String queryArgs [] = new String[ids.size()];
            if(ids.isEmpty()){
                query = null;
                queryArgs = null;
            }
            this.getContentResolver().delete(
                    RecappContract.EventByUserEntry.CONTENT_URI,
                    query,
                    queryArgs
            );
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
            while (!collectionResponseEvent.getNextPageToken().equals(nextPage)){
                List<ContentValues> valuesList = new ArrayList<>();
                eventList = collectionResponseEvent.getItems();
                if(eventList!=null) {
                    for (Event i : eventList) {
                        ids.add(i.getId()+"");
                        query+="?,";
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
                    this.getContentResolver().bulkInsert(
                            RecappContract.EventEntry.CONTENT_URI,
                            values
                    );
                    collectionResponseEvent = Utility.getEventApi().list().setCursor(nextPage).execute();
                }

            }
            query = query.substring(0,query.length()-1);
            query = ")";
            String [] queryArgs = new String[ids.size()];
            ids.toArray(queryArgs);
            switch (type){
                case "addEvent":
                    //We should make the notification to the events
                    //NotificationCompat.InboxStyle inboxStyle = new NotificationCompat
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Notification notification = new Notification.Builder(this)
                            .setContentTitle("There are new events")
                            .setContentText("which are waiting for you")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000})
                            .setLights(Color.RED, 3000, 3000)
                            .setSound(alarmSound)
                            .build();
                    NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(-1,notification);
                    break;
                case "deleteEvent":
                    List<Long> idsUser = new ArrayList<>();
                    if(ids.isEmpty()){
                        query = null;
                        queryArgs = null;
                    }
                    Cursor cursor  = this.getContentResolver().query(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            new String[]{RecappContract.EventEntry._ID},
                            query,
                            queryArgs,
                            null
                    );
                    while (cursor.moveToNext()){
                        idsUser.add(cursor.getLong(0));
                    }
                    if(!idsUser.isEmpty()){
                        for (Long i:idsUser){
                            Utility.getEventByUserApi().removeUsers(i);
                        }
                    }
                    break;
            }
            if(ids.isEmpty()){
                query = null;
                queryArgs = null;
            }
            this.getContentResolver().delete(
                    RecappContract.EventEntry.CONTENT_URI,
                    query,
                    queryArgs
            );
        }catch (IOException e){

        }
    }
    public void getImagePlace(){
        try {
            CollectionResponsePlaceImage collectionResponsePlaceImage = Utility.getPlaceImageApi().list().execute();
            List<PlaceImage> placeImageList ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.PlaceImageEntry._ID + " NOT IN ( ";
            String nextPage = "";
            while (!collectionResponsePlaceImage.getNextPageToken().equals(nextPage)){
                placeImageList = collectionResponsePlaceImage.getItems();
                if(placeImageList!=null){
                    List<ContentValues> valuesList = new ArrayList<>();
                    for(PlaceImage i:placeImageList){
                        ids.add(i.getId() + "");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.PlaceImageEntry._ID,i.getId());
                        value.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,i.getWorth());
                        value.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY,i.getPlaceId());
                        value.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE,Utility.decodeImage(i.getImage()));
                        valuesList.add(value);
                    }
                    ContentValues values [] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.PlaceImageEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponsePlaceImage.getNextPageToken();
                    collectionResponsePlaceImage = Utility.getPlaceImageApi().list().setCursor(nextPage).execute();
                }
            }
            query = query.substring(0,query.length()-1);
            query = ")";
            String queryArgs[] = new String[ids.size()];
            ids.toArray(queryArgs);
            if(ids.isEmpty()){
                query = null;
                queryArgs = null;
            }
            this.getContentResolver().delete(
                    RecappContract.PlaceImageEntry.CONTENT_URI,
                    query,
                    queryArgs
            );
        }catch (IOException e){

        }
    }

    public void getReminders(){
        try {
            CollectionResponseReminder collectionResponseReminderAll = Utility.getReminderApi().list().execute();
            List<Reminder> reminderListAll ;
            String nextPage = "";
            List<String> ids = new ArrayList<>();
            String query = RecappContract.ReminderEntry._ID + " NOT IN ( ";
            while (!collectionResponseReminderAll.getNextPageToken().equals(nextPage)){
                reminderListAll = collectionResponseReminderAll.getItems();
                if(reminderListAll!=null){
                    List<ContentValues> valuesList = new ArrayList<>();
                    for(Reminder i: reminderListAll){
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.ReminderEntry._ID,i.getId());
                        value.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION, i.getNotification());
                        value.put(RecappContract.ReminderEntry.COLUMN_NAME,i.getName());
                        value.put(RecappContract.ReminderEntry.COLUMN_END_DATE,i.getEndDate());
                        value.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION,i.getDescription());
                        value.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY,i.getPlaceId());
                        value.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,i.getUserId());
                        valuesList.add(value);
                    }
                    ContentValues [] values = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.ReminderEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseReminderAll.getNextPageToken();
                    collectionResponseReminderAll = Utility.getReminderApi().list().setCursor(nextPage).execute();
                }
            }
            query = query.substring(0,query.length()-1);
            query +=")";
            String queryArgs[] = new String[ids.size()];
            ids.toArray(queryArgs);
            if(ids.isEmpty()){
                query = null;
                queryArgs = null;
            }
            this.getContentResolver().delete(
                    RecappContract.ReminderEntry.CONTENT_URI,
                    query,
                    queryArgs
            );
        }catch (IOException e){}
    }

    public void getSubCategoryByPlace(){
        try {
            CollectionResponseSubCategoryByPlace collectionResponseSubCategoryByPlace =
                    Utility.getSubCategoryByPlaceApi().list().execute();
            List<SubCategoryByPlace> subCategoryByPlaceList;

            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryByPlaceEntry._ID + " NOT IN ( ";

            String nextPage = "";
            while (!collectionResponseSubCategoryByPlace.getNextPageToken().equals(nextPage)){
                subCategoryByPlaceList = collectionResponseSubCategoryByPlace.getItems();
                List<ContentValues> valuesList = new ArrayList<>();
                if(subCategoryByPlaceList!=null) {
                    for (SubCategoryByPlace i : subCategoryByPlaceList) {
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.SubCategoryByPlaceEntry._ID, i.getId());
                        value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                        value.put(RecappContract.SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                        valuesList.add(value);
                    }
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseSubCategoryByPlace.getNextPageToken();
                    collectionResponseSubCategoryByPlace = Utility.getSubCategoryByPlaceApi().list().setCursor(nextPage)
                            .execute();
                }
                query = query.substring(0,query.length()-1);
                String queryArgs [] = new String[ids.size()];
                ids.toArray(queryArgs);
                if(ids.isEmpty()){
                    query = null;
                    queryArgs = null;
                }
                this.getContentResolver().delete(
                        RecappContract.SubCategoryByPlaceEntry.CONTENT_URI,
                        query,
                        queryArgs
                );

            }
        }catch (IOException e){}
    }

    public void getSubCategoryByTutorial(){
        try {
            CollectionResponseSubCategoryByTutorial collectionResponseSubCategoryByTutorial =
                    Utility.getSubCategoryByTutorialApi().list().execute();
            List<SubCategoryByTutorial> subCategoryByTutorialList ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryByTutorialEntry._ID + " NOT IN ( ";

            String nextPage = "";
            while (collectionResponseSubCategoryByTutorial.getNextPageToken().equals(nextPage)) {
                subCategoryByTutorialList = collectionResponseSubCategoryByTutorial.getItems();
                List<ContentValues> valuesList = new ArrayList<>();
                if(subCategoryByTutorialList!=null) {
                    for (SubCategoryByTutorial i : subCategoryByTutorialList) {
                        ids.add(i.getId()+"");
                        query +="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.SubCategoryByTutorialEntry._ID, i.getId());
                        value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                        value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY, i.getTutorialId());
                        valuesList.add(value);
                    }
                    ContentValues values[] = new ContentValues[subCategoryByTutorialList.size()];
                    subCategoryByTutorialList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                            values
                    );
                    nextPage = collectionResponseSubCategoryByTutorial.getNextPageToken();
                    collectionResponseSubCategoryByTutorial = Utility.getSubCategoryByTutorialApi().list()
                            .setCursor(nextPage).execute();
                }
            }
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
        }catch (IOException e){}
    }

    public void getSubCategory(){
        try{
            CollectionResponseSubCategory collectionResponseSubCategory = Utility.getSubCategoryApi().list().execute();
            List<SubCategory> subCategoryList ;

            List<String> ids = new ArrayList<>();
            String query = RecappContract.SubCategoryEntry._ID + " NOT IN ( ";

            String nextPage = "";
            while (!collectionResponseSubCategory.getNextPageToken().equals(nextPage)){
                subCategoryList = collectionResponseSubCategory.getItems();
                List<ContentValues> valuesList = new ArrayList<>();
                if(subCategoryList!=null) {
                    for (SubCategory i : subCategoryList) {
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.SubCategoryEntry._ID, i.getId());
                        value.put(RecappContract.SubCategoryEntry.COLUMN_NAME, i.getName());
                        value.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, i.getCategoryId());
                        valuesList.add(value);
                    }
                    nextPage = collectionResponseSubCategory.getNextPageToken();
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.SubCategoryEntry.CONTENT_URI,
                            values
                    );
                    collectionResponseSubCategory = Utility.getSubCategoryApi().list().setCursor(nextPage).execute();
                }
            }
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
        }catch (IOException e){}
    }

    public void getTutorial(){
        try {
            CollectionResponseTutorial collectionResponseTutorial = Utility.getTutorialApi().list().execute();
            List<Tutorial> tutorialList ;
            List<String> ids = new ArrayList<>();
            String query = RecappContract.TutorialEntry._ID + " NOT IN ( ";
            String nextPage = "";
            while (!collectionResponseTutorial.getNextPageToken().equals(nextPage)){
                tutorialList = collectionResponseTutorial.getItems();
                List<ContentValues> valuesList = new ArrayList<>();
                if(tutorialList!=null) {
                    for (Tutorial i : tutorialList) {
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.TutorialEntry._ID, i.getId());
                        value.put(RecappContract.TutorialEntry.COLUMN_NAME, i.getName());
                        value.put(RecappContract.TutorialEntry.COLUMN_DESCRIPTION, i.getDescription());
                        value.put(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO, i.getLink());
                        valuesList.add(value);
                    }
                    nextPage = collectionResponseTutorial.getNextPageToken();
                    ContentValues values[] = new ContentValues[valuesList.size()];
                    valuesList.toArray(values);
                    this.getContentResolver().bulkInsert(
                            RecappContract.TutorialEntry.CONTENT_URI,
                            values
                    );
                    collectionResponseTutorial = Utility.getTutorialApi().list().setCursor(nextPage).execute();
                }
            }
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
        }catch (IOException e){}
    }

    public void getFavoritePlace(){
        try {
            CollectionResponseUserByPlace collectionResponseUserByPlace = Utility.getUserByPlaceApi().list().execute();
            List<UserByPlace> userByPlaceListAll;
            String nextPage = "";
            List<String> ids = new ArrayList<>();
            String query = RecappContract.UserByPlaceEntry._ID + " NOT IN ( ";
            while (!collectionResponseUserByPlace.getNextPageToken().equals(nextPage)){
                userByPlaceListAll = collectionResponseUserByPlace.getItems();
                if(userByPlaceListAll!=null){
                    List<ContentValues> valuesList = new ArrayList<>();
                    for (UserByPlace i:userByPlaceListAll){
                        ids.add(i.getId()+"");
                        query+="?,";
                        ContentValues value = new ContentValues();
                        value.put(RecappContract.UserByPlaceEntry._ID,i.getId());
                        value.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY,i.getUserId());
                        value.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY,i.getId());
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
            query = query.substring(0,query.length()-1);
            query+=")";
            String queryArgs [] = new String[ids.size()];
            ids.toArray(queryArgs);
            if(ids.isEmpty()){
                query=null;
                queryArgs = null;
            }
            this.getContentResolver().delete(
                    RecappContract.UserByPlaceEntry.CONTENT_URI,
                    query,
                    queryArgs
            );
        }catch (IOException e){}
    }
}

