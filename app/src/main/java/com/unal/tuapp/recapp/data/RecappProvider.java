package com.unal.tuapp.recapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


import com.unal.tuapp.recapp.data.RecappContract.*;

/**
 * Created by andresgutierrez on 8/3/15.
 */
public class RecappProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildMatcher();
    private RecappDBHelper recappDBHelper;

    static final int USER = 100;
    static final int USER_WITH_EMAIL = 110;
    static final int USER_WITH_ID = 120;
    static final int PLACE = 200;
    static final int PLACE_WITH_ID = 210;
    static final int REMINDER = 300;
    static final int REMINDER_WITH_USER = 310;
    static final int REMINDER_WITH_PLACE = 320;
    static final int REMINDER_WITH_ID = 330;
    static final int COMMENT = 400;
    static final int COMMENT_WITH_USER = 410;
    static final int COMMENT_WITH_PLACE = 420;
    static final int COMMENT_WITH_PLACE_USER = 430;
    static final int COMMENT_WITH_ID = 440;
    static final int CATEGORY = 500;
    static final int CATEGORY_WITH_ID = 510;
    static final int TUTORIAL = 600;
    static final int TUTORIAL_WITH_ID = 610;
    static final int PLACE_IMAGE= 700;
    static final int PLACE_IMAGE_WITH_PLACE = 710;
    static final int PLACE_IMAGE_WITH_ID = 720;
    static final int SUB_CATEGORY = 800;
    static final int SUB_CATEGORY_WITH_CATEGORY = 810;
    static final int SUB_CATEGORY_WITH_CATEGORY_ID = 820;
    static final int SUB_CATEGORY_WITH_ID = 830;
    static final int SUB_CATEGORY_BY_PLACE = 900;
    static final int SUB_CATEGORY_BY_PLACE_ID = 910;
    static final int SUB_CATEGORY_BY_PLACE_PLACE = 920;
    static final int SUB_CATEGORY_BY_PLACE_PLACE_ID = 930;
    static final int SUB_CATEGORY_BY_PLACE_SUB_CATEGORY = 940;
    static final int SUB_CATEGORY_BY_PLACE_SUB_CATEGORY_ID = 950;
    static final int SUB_CATEGORY_BY_PLACE_PLACE_SUB_CATEGORY = 960;
    static final int SUB_CATEGORY_BY_TUTORIAL = 1000;
    static final int SUB_CATEGORY_BY_TUTORIAL_ID = 1010;
    static final int SUB_CATEGORY_BY_TUTORIAL_TUTORIAL = 1020;
    static final int SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_ID = 1030;
    static final int SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY = 1040;
    static final int SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY_ID = 1050;
    static final int SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_SUB_CATEGORY = 1060;
    static final int USER_BY_PLACE = 1100;
    static final int USER_BY_PLACE_USER = 1110;
    static final int USER_BY_PLACE_PLACE = 1120;
    static final int USER_BY_PLACE_ID = 1130;
    static final int EVENT = 1200;
    static final int EVENT_ID = 1210;
    static final int EVENT_BY_USER = 1300;
    static final int EVENT_BY_USER_EVENT = 1310;
    static final int EVENT_BY_USER_USER = 1320;
    static final int EVENT_BY_USER_ID = 1330;

    private static final SQLiteQueryBuilder reminderByUser;
    private static final SQLiteQueryBuilder reminderByPlace;
    private static final SQLiteQueryBuilder commentByUser;
    private static final SQLiteQueryBuilder commentByPlace;
    private static final SQLiteQueryBuilder placeImagePlace;
    private static final SQLiteQueryBuilder subCategoryCategory;
    private static final SQLiteQueryBuilder subCategoryByPlacePlace;
    private static final SQLiteQueryBuilder subCategoryByPlaceSubCategory;
    private static final SQLiteQueryBuilder subCategoryByPlacePlaceSubCategory;
    private static final SQLiteQueryBuilder subCategoryByTutorialTutorial;
    private static final SQLiteQueryBuilder subCategoryByTutorialSubCategory;
    private static final SQLiteQueryBuilder subCategoryByTutorialTutorialSubCategory;
    private static final SQLiteQueryBuilder userByPlaceUser;
    private static final SQLiteQueryBuilder userByPlacePlace;
    private static final SQLiteQueryBuilder eventByUserEvent;
    private static final SQLiteQueryBuilder eventByUserUser;

    static {

        reminderByUser = new SQLiteQueryBuilder();
        reminderByUser.setTables(
                ReminderEntry.TABLE_NAME + " INNER JOIN " +
                        UserEntry.TABLE_NAME + " ON " +
                        ReminderEntry.TABLE_NAME+"."+ReminderEntry.COLUMN_USER_KEY +
                        " = " + UserEntry.TABLE_NAME+"."+UserEntry._ID
        );
        reminderByPlace = new SQLiteQueryBuilder();
        reminderByPlace.setTables(
                ReminderEntry.TABLE_NAME + " INNER JOIN " +
                        PlaceEntry.TABLE_NAME +  " ON " +
                        ReminderEntry.TABLE_NAME+"."+ReminderEntry.COLUMN_PLACE_KEY +
                        " = " + PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID
        );
        commentByUser = new SQLiteQueryBuilder();
        commentByUser.setTables(
                CommentEntry.TABLE_NAME + " INNER JOIN " +
                        UserEntry.TABLE_NAME + " ON " +
                        CommentEntry.TABLE_NAME+"."+CommentEntry.COLUMN_USER_KEY +
                        " = " + UserEntry.TABLE_NAME+"."+UserEntry._ID
        );
        commentByPlace = new SQLiteQueryBuilder();
        commentByPlace.setTables(
                CommentEntry.TABLE_NAME + " INNER JOIN " +
                        PlaceEntry.TABLE_NAME + " ON " +
                        CommentEntry.TABLE_NAME+"."+CommentEntry.COLUMN_PLACE_KEY +
                        " = " + PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID
        );

        placeImagePlace = new SQLiteQueryBuilder();
        placeImagePlace.setTables(
                PlaceImageEntry.TABLE_NAME + " INNER JOIN " +
                        PlaceEntry.TABLE_NAME + " ON " +
                        PlaceImageEntry.TABLE_NAME+"."+PlaceImageEntry.COLUMN_PLACE_KEY +
                        " = " + PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID
        );
        subCategoryCategory = new SQLiteQueryBuilder();
        subCategoryCategory.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        CategoryEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry.COLUMN_CATEGORY_KEY +
                        " = " + CategoryEntry.TABLE_NAME+"."+CategoryEntry._ID
        );
        subCategoryByPlacePlace = new SQLiteQueryBuilder();
        subCategoryByPlacePlace.setTables(
                PlaceEntry.TABLE_NAME + " INNER JOIN " +
                        SubCategoryByPlaceEntry.TABLE_NAME + " ON " +
                        PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID+
                        " = " + SubCategoryByPlaceEntry.TABLE_NAME+"."+SubCategoryByPlaceEntry.COLUMN_PLACE_KEY
        );
        subCategoryByPlaceSubCategory = new SQLiteQueryBuilder();
        subCategoryByPlaceSubCategory.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        SubCategoryByPlaceEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME + "." + SubCategoryEntry._ID +
                        " = " + SubCategoryByPlaceEntry.TABLE_NAME + "." + SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY
        );
        subCategoryByPlacePlaceSubCategory =  new SQLiteQueryBuilder();
        subCategoryByPlacePlaceSubCategory.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        SubCategoryByPlaceEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME + "." + SubCategoryEntry._ID +
                        " = " + SubCategoryByPlaceEntry.TABLE_NAME + "." + SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY +
                        " INNER JOIN " + PlaceEntry.TABLE_NAME + " ON "+
                        PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID + "=" +
                        SubCategoryByPlaceEntry.TABLE_NAME+"."+SubCategoryByPlaceEntry.COLUMN_PLACE_KEY
        );
        subCategoryByTutorialTutorial = new SQLiteQueryBuilder();
        subCategoryByTutorialTutorial.setTables(
                SubCategoryByTutorialEntry.TABLE_NAME + " INNER JOIN "+
                        TutorialEntry.TABLE_NAME + " ON "+
                        SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY +
                        " = "+ TutorialEntry.TABLE_NAME+"."+TutorialEntry._ID
        );
        subCategoryByTutorialSubCategory = new SQLiteQueryBuilder();
        subCategoryByTutorialSubCategory.setTables(
                SubCategoryByTutorialEntry.TABLE_NAME + " INNER JOIN "+
                        SubCategoryEntry.TABLE_NAME + " ON "+
                        SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY+
                        " = "+ SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry._ID
        );
        subCategoryByTutorialTutorialSubCategory = new SQLiteQueryBuilder();
        subCategoryByTutorialTutorialSubCategory.setTables(
                SubCategoryByTutorialEntry.TABLE_NAME + " INNER JOIN "+
                        TutorialEntry.TABLE_NAME + " ON "+
                        SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY +
                        " = "+ TutorialEntry.TABLE_NAME+"."+TutorialEntry._ID +
                        " INNER JOIN " + SubCategoryEntry.TABLE_NAME + " ON "+
                        SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY+
                        " = "+ SubCategoryEntry.TABLE_NAME +"."+ SubCategoryEntry._ID
        );
        userByPlaceUser = new SQLiteQueryBuilder();
        userByPlaceUser.setTables(
                UserByPlaceEntry.TABLE_NAME + " INNER JOIN " +
                        UserEntry.TABLE_NAME + " ON "+
                        UserByPlaceEntry.TABLE_NAME+"."+UserByPlaceEntry.COLUMN_USER_KEY+
                        " = "+ UserEntry.TABLE_NAME+"."+UserEntry._ID
        );
        userByPlacePlace = new SQLiteQueryBuilder();
        userByPlacePlace.setTables(
                UserByPlaceEntry.TABLE_NAME + " INNER JOIN " +
                        PlaceEntry.TABLE_NAME +" ON "+
                        UserByPlaceEntry.TABLE_NAME+"."+UserByPlaceEntry.COLUMN_PLACE_KEY+
                        " = "+ PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID
        );
        eventByUserEvent = new SQLiteQueryBuilder();
        eventByUserEvent.setTables(
                EventByUserEntry.TABLE_NAME + " INNER JOIN " +
                        EventEntry.TABLE_NAME + " ON "+
                        EventByUserEntry.TABLE_NAME+"."+ EventByUserEntry.COLUMN_KEY_EVENT+
                        " = " + EventEntry.TABLE_NAME+"."+ EventEntry._ID
        );
        eventByUserUser = new SQLiteQueryBuilder();
        eventByUserUser.setTables(
                EventByUserEntry.TABLE_NAME + " INNER JOIN " +
                        UserEntry.TABLE_NAME + " ON "+
                        EventByUserEntry.TABLE_NAME+"."+ EventByUserEntry.COLUMN_KEY_USER+
                        " = "+ UserEntry.TABLE_NAME+"."+UserEntry._ID
        );

    }




    static UriMatcher buildMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecappContract.CONTENT_AUTHORITY;

        //Matchers for user
        matcher.addURI(authority, RecappContract.PATH_USER,USER);
        matcher.addURI(authority, RecappContract.PATH_USER+"/*",USER_WITH_EMAIL);
        matcher.addURI(authority,RecappContract.PATH_USER+"/#",USER_WITH_ID);

        //Matchers for place
        matcher.addURI(authority,RecappContract.PATH_PLACE,PLACE);
        matcher.addURI(authority,RecappContract.PATH_PLACE+"/#",PLACE_WITH_ID);

        //Matchers for reminder
        matcher.addURI(authority,RecappContract.PATH_REMINDER,REMINDER);
        matcher.addURI(authority,RecappContract.PATH_REMINDER+"/"+RecappContract.PATH_USER+"/#",REMINDER_WITH_USER);
        matcher.addURI(authority,RecappContract.PATH_REMINDER+"/"+RecappContract.PATH_PLACE+"/#",REMINDER_WITH_PLACE);
        matcher.addURI(authority,RecappContract.PATH_REMINDER+"/#",REMINDER_WITH_ID);

        //Matchers for comment
        matcher.addURI(authority,RecappContract.PATH_COMMENT,COMMENT);
        matcher.addURI(authority,RecappContract.PATH_COMMENT+"/"+RecappContract.PATH_USER+"/#",COMMENT_WITH_USER);
        matcher.addURI(authority,RecappContract.PATH_COMMENT+"/"+RecappContract.PATH_PLACE+"/#",COMMENT_WITH_PLACE);
        matcher.addURI(authority,RecappContract.PATH_COMMENT+"/"+RecappContract.PATH_PLACE+"/"+
        RecappContract.PATH_USER+"/#",COMMENT_WITH_PLACE_USER);
        matcher.addURI(authority,RecappContract.PATH_COMMENT+"/#",COMMENT_WITH_ID);

        //Mathcers for category
        matcher.addURI(authority,RecappContract.PATH_CATEGORY,CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_CATEGORY+"/#",CATEGORY_WITH_ID);

        //Matchers for tutorial
        matcher.addURI(authority,RecappContract.PATH_TUTORIAL,TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_TUTORIAL+"/#",TUTORIAL_WITH_ID);

        //Matchers for place with image
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE,PLACE_IMAGE);
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE+"/"+RecappContract.PATH_PLACE+"/#",PLACE_IMAGE_WITH_PLACE);
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE+"/#",PLACE_IMAGE_WITH_ID);

        //Matchers for sub category
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/"+RecappContract.PATH_CATEGORY,SUB_CATEGORY_WITH_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/"+RecappContract.PATH_CATEGORY+"/#",SUB_CATEGORY_WITH_CATEGORY_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/#",SUB_CATEGORY_WITH_ID);

        //Matchers for sub category by place
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE,SUB_CATEGORY_BY_PLACE);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/#",SUB_CATEGORY_BY_PLACE_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/"+RecappContract.PATH_PLACE,SUB_CATEGORY_BY_PLACE_PLACE);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/"+RecappContract.PATH_PLACE+"/#",SUB_CATEGORY_BY_PLACE_PLACE_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/"+RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY_BY_PLACE_SUB_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/"+RecappContract.PATH_SUBCATEGORY+"/#",SUB_CATEGORY_BY_PLACE_SUB_CATEGORY_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_PLACE+"/"+RecappContract.PATH_PLACE+"/"+RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY_BY_PLACE_PLACE_SUB_CATEGORY);

        //Matchers for sub category by tutorial
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL,SUB_CATEGORY_BY_TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/#",SUB_CATEGORY_BY_TUTORIAL_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/"+RecappContract.PATH_TUTORIAL,SUB_CATEGORY_BY_TUTORIAL_TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/"+RecappContract.PATH_TUTORIAL+"/#",SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/"+RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/"+RecappContract.PATH_SUBCATEGORY+"/#",SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY_ID);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY_BY_TUTORIAL+"/"+RecappContract.PATH_TUTORIAL+"/"+RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_SUB_CATEGORY);

        //Matchers for user by place
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE,USER_BY_PLACE);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/"+RecappContract.PATH_USER+"/#",USER_BY_PLACE_USER);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/"+RecappContract.PATH_PLACE+"/#",USER_BY_PLACE_PLACE);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/#",USER_BY_PLACE_ID);

        //Mathcers for event
        matcher.addURI(authority,RecappContract.PATH_EVENT,EVENT);
        matcher.addURI(authority,RecappContract.PATH_EVENT+"/#",EVENT_ID);

        //Mathcers for event by user
        matcher.addURI(authority,RecappContract.PATH_EVENTBYUSER,EVENT_BY_USER);
        matcher.addURI(authority,RecappContract.PATH_EVENTBYUSER+"/"+RecappContract.PATH_EVENT+"/#",EVENT_BY_USER_EVENT);
        matcher.addURI(authority,RecappContract.PATH_EVENTBYUSER+"/"+RecappContract.PATH_USER+"/*",EVENT_BY_USER_USER);
        matcher.addURI(authority,RecappContract.PATH_EVENTBYUSER+"/#",EVENT_BY_USER_ID);

        return matcher;

    }

    @Override
    public boolean onCreate(){
        recappDBHelper = RecappDBHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMatcher.match(uri);
        switch (match){
            case USER:
                return UserEntry.CONTENT_TYPE;
            case USER_WITH_EMAIL:
                return UserEntry.CONTENT_ITEM_TYPE;
            case USER_WITH_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            case PLACE:
                return PlaceEntry.CONTENT_TYPE;
            case PLACE_WITH_ID:
                return PlaceEntry.CONTENT_ITEM_TYPE;
            case REMINDER:
                return ReminderEntry.CONTENT_TYPE;
            case REMINDER_WITH_USER:
                return ReminderEntry.CONTENT_TYPE;
            case REMINDER_WITH_PLACE:
                return ReminderEntry.CONTENT_TYPE;
            case REMINDER_WITH_ID:
                return ReminderEntry.CONTENT_ITEM_TYPE;
            case COMMENT:
                return CommentEntry.CONTENT_TYPE;
            case COMMENT_WITH_USER:
                return CommentEntry.CONTENT_TYPE;
            case COMMENT_WITH_PLACE:
                return CommentEntry.CONTENT_TYPE;
            case COMMENT_WITH_PLACE_USER:
                return CommentEntry.CONTENT_TYPE;
            case COMMENT_WITH_ID:
                return CommentEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_TYPE;
            case CATEGORY_WITH_ID:
                return CategoryEntry.CONTENT_ITEM_TYPE;
            case TUTORIAL:
                return TutorialEntry.CONTENT_TYPE;
            case TUTORIAL_WITH_ID:
                return TutorialEntry.CONTENT_ITEM_TYPE;
            case PLACE_IMAGE:
                return PlaceImageEntry.CONTENT_TYPE;
            case PLACE_IMAGE_WITH_PLACE:
                return PlaceImageEntry.CONTENT_TYPE;
            case PLACE_IMAGE_WITH_ID:
                return PlaceImageEntry.CONTENT_ITEM_TYPE;
            case SUB_CATEGORY:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_CATEGORY:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_CATEGORY_ID:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_ID:
                return SubCategoryEntry.CONTENT_ITEM_TYPE;
            case SUB_CATEGORY_BY_PLACE:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_PLACE_ID:
                return SubCategoryByPlaceEntry.CONTENT_ITEM_TYPE;
            case SUB_CATEGORY_BY_PLACE_PLACE:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_PLACE_PLACE_ID:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_PLACE_SUB_CATEGORY:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_PLACE_SUB_CATEGORY_ID:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_PLACE_PLACE_SUB_CATEGORY:
                return SubCategoryByPlaceEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_ID:
                return SubCategoryByTutorialEntry.CONTENT_ITEM_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_ID:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY_ID:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_SUB_CATEGORY:
                return SubCategoryByTutorialEntry.CONTENT_TYPE;
            case USER_BY_PLACE:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_USER:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_PLACE:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_ID:
                return UserByPlaceEntry.CONTENT_ITEM_TYPE;
            case EVENT:
                return EventEntry.CONTENT_TYPE;
            case EVENT_ID:
                return EventEntry.CONTENT_ITEM_TYPE;
            case EVENT_BY_USER:
                return EventByUserEntry.CONTENT_TYPE;
            case EVENT_BY_USER_EVENT:
                return EventByUserEntry.CONTENT_TYPE;
            case EVENT_BY_USER_USER:
                return EventByUserEntry.CONTENT_TYPE;
            case EVENT_BY_USER_ID:
                return EventByUserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }


    }
    @Override
     public Cursor query(Uri uri,String [] projection,String selection,String[] selectionArgs,
                         String sortOrder){
        Cursor retCursor = null;
        switch (uriMatcher.match(uri)){
            case USER:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_WITH_EMAIL:
                String email = UserEntry.getEmailFromUri(uri);
                //Like the email is unique we can forget the selection that we received
                String selectionEmail = UserEntry.COLUMN_EMAIL + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        UserEntry.TABLE_NAME,
                        projection,
                        selectionEmail,
                        new String[]{email.trim()},
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_WITH_ID:
                long userId = UserEntry.getIdFromUri(uri);
                String selectionUserId = UserEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        UserEntry.TABLE_NAME,
                        projection,
                        selectionUserId,
                        new String[]{"" + userId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE_WITH_ID:
                long placeId = PlaceEntry.getIdFromUri(uri);
                String selectionPlaceId = PlaceEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        PlaceEntry.TABLE_NAME,
                        projection,
                        selectionPlaceId,
                        new String[]{""+placeId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REMINDER:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        ReminderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REMINDER_WITH_USER:
                placeId = ReminderEntry.getPlaceFromUri(uri);
                selection = ReminderEntry.COLUMN_PLACE_KEY + " = ? ";
                retCursor = reminderByUser.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+placeId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REMINDER_WITH_PLACE:
                userId = ReminderEntry.getUserFromUri(uri);
                selection = ReminderEntry.COLUMN_USER_KEY + " = ? ";
                retCursor = reminderByPlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+userId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REMINDER_WITH_ID:
                long reminderId = ReminderEntry.getIdFromUri(uri);
                selection = ReminderEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        ReminderEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+reminderId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case COMMENT:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        CommentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case COMMENT_WITH_USER:
                userId = CommentEntry.getUserFromUri(uri);
                selection = UserEntry.TABLE_NAME+"."+UserEntry._ID + " = ? ";
                retCursor = commentByUser.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+userId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case COMMENT_WITH_PLACE:
                placeId = CommentEntry.getPlaceFromUri(uri);
                String groupBy = selection;
                selection = PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID + " = ? ";

                retCursor = commentByPlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{"" + placeId},
                        groupBy,
                        null,
                        sortOrder
                );

                break;
            case COMMENT_WITH_PLACE_USER:
                placeId = CommentEntry.getPlaceUserFromUri(uri);
                selection = CommentEntry.TABLE_NAME+"."+CommentEntry.COLUMN_PLACE_KEY + " = ?";
                retCursor = commentByUser.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+placeId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case COMMENT_WITH_ID:
                long commentId = CommentEntry.getIdFromUri(uri);
                selection = CommentEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        CommentEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+commentId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY_WITH_ID:
                long categoryId = CategoryEntry.getIdFromUri(uri);
                selection = CategoryEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+categoryId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case TUTORIAL:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        TutorialEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TUTORIAL_WITH_ID:
                long tutorialId = TutorialEntry.getIdFromUri(uri);
                selection = TutorialEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        TutorialEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+tutorialId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE_IMAGE:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        PlaceImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE_IMAGE_WITH_PLACE:
                placeId = PlaceImageEntry.getPlaceFromUri(uri);
                selection = PlaceImageEntry.COLUMN_PLACE_KEY + " = ?";
                retCursor = placeImagePlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+placeId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE_IMAGE_WITH_ID:
                long placeImageId = PlaceImageEntry.getIdFromUri(uri);
                selection = PlaceImageEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        PlaceImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+ placeImageId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        SubCategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_WITH_CATEGORY:
                retCursor = subCategoryCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_WITH_CATEGORY_ID:
                categoryId = SubCategoryEntry.getCategoryFromUri(uri);
                selection = CategoryEntry._ID + " = ? ";
                retCursor = subCategoryCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+categoryId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_WITH_ID:
                long subCategoryId = SubCategoryEntry.getIdFromUri(uri);
                selection = SubCategoryEntry._ID + " = ? ";
                retCursor= recappDBHelper.getReadableDatabase().query(
                        SubCategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+subCategoryId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        SubCategoryByPlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_ID:
                long subCategoryByPlaceId = SubCategoryByPlaceEntry.getIdFromUri(uri);
                selection = SubCategoryByPlaceEntry._ID + " =? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        SubCategoryByPlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+subCategoryByPlaceId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_PLACE:
                subCategoryByPlacePlace.setDistinct(true);
                retCursor = subCategoryByPlacePlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_PLACE_ID:
                long subCategoryByPlaceByPlaceId = SubCategoryByPlaceEntry.getIdFromPlaceUri(uri);
                selection = PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID + "= ?";
                subCategoryByPlacePlace.setDistinct(false);
                retCursor = subCategoryByPlacePlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+subCategoryByPlaceByPlaceId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_SUB_CATEGORY:
                subCategoryByPlaceSubCategory.setDistinct(true);
                retCursor = subCategoryByPlaceSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_SUB_CATEGORY_ID:
                subCategoryByPlaceSubCategory.setDistinct(true);
                long subCategoryByPlaceBySubCategoryId = SubCategoryByPlaceEntry.getIdFromSubCategoryUri(uri);
                selection = SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry._ID +"=?";
                retCursor = subCategoryByPlaceSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+subCategoryByPlaceBySubCategoryId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_PLACE_PLACE_SUB_CATEGORY:
                subCategoryByPlacePlaceSubCategory.setDistinct(true);
                retCursor = subCategoryByPlacePlaceSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        SubCategoryByTutorialEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_ID:
                long subCategoryTutorialId = SubCategoryByTutorialEntry.getIdFromUri(uri);
                selection = SubCategoryByTutorialEntry._ID +"=?";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        SubCategoryByTutorialEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+subCategoryTutorialId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL:
                subCategoryByTutorialTutorial.setDistinct(true);
                retCursor = subCategoryByTutorialTutorial.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_ID:
                long subCategoryTutorialTutorialId = SubCategoryByTutorialEntry.getIdTutorialUir(uri);
                selection = SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY+" =?";
                retCursor = subCategoryByTutorialTutorial.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+subCategoryTutorialTutorialId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY:
                subCategoryByTutorialSubCategory.setDistinct(true);
                retCursor = subCategoryByTutorialSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_SUB_CATEGORY_ID:
                long subCategoryTutorialSubCategoryId = SubCategoryByTutorialEntry.getIdSubCategoryUri(uri);
                selection = SubCategoryByTutorialEntry.TABLE_NAME+"."+SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY+"=?";
                retCursor = subCategoryByTutorialSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+subCategoryTutorialSubCategoryId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_BY_TUTORIAL_TUTORIAL_SUB_CATEGORY:
                subCategoryByTutorialTutorialSubCategory.setDistinct(true);
                retCursor = subCategoryByTutorialTutorialSubCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_BY_PLACE:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        UserByPlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_BY_PLACE_USER:
                userId = UserByPlaceEntry.getPlaceFromUri(uri);
                selection = UserByPlaceEntry.COLUMN_USER_KEY + " = ? ";
                retCursor = userByPlacePlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{"" + userId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_BY_PLACE_PLACE:
                placeId = UserByPlaceEntry.getUserFromUri(uri);
                selection = UserByPlaceEntry.COLUMN_PLACE_KEY + " = ? ";
                retCursor = userByPlaceUser.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new  String[]{""+placeId},
                        null,
                        null,
                        sortOrder
                );

                break;
            case USER_BY_PLACE_ID:
                long userByPlaceId = UserByPlaceEntry.getIdFromUri(uri);
                selection = UserByPlaceEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        UserByPlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+userByPlaceId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT_ID:
                long eventId = EventEntry.getIdFromUri(uri);
                selection = EventEntry._ID + " = ?";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+eventId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT_BY_USER:
                retCursor = recappDBHelper.getReadableDatabase().query(
                        EventByUserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT_BY_USER_EVENT:
                eventId = EventByUserEntry.getEventFromUri(uri);
                selection =  EventByUserEntry.COLUMN_KEY_EVENT +" = ? ";
                retCursor = eventByUserUser.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+eventId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT_BY_USER_USER:
                String userEmail = EventByUserEntry.getUserFromUri(uri);
                if(selection==null) {
                    selection = EventByUserEntry.COLUMN_KEY_USER + " = ?";
                }
                retCursor = eventByUserEvent.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{userEmail},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  retCursor;
    }

    @Override
    public Uri insert(Uri uri,ContentValues values){
        Uri returnUri = null;
        final SQLiteDatabase db = recappDBHelper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)){
            case USER:
                id = db.insert(UserEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = UserEntry.buildUserUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PLACE:
                id = db.insert(PlaceEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = PlaceEntry.buildPlaceUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case REMINDER:
                id = db.insert(ReminderEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = ReminderEntry.buildReminderUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case COMMENT:

                id = db.insert(CommentEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = CommentEntry.buildCommentUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case CATEGORY:
                id = db.insert(CategoryEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = CategoryEntry.buildCategoryUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TUTORIAL:
                id = db.insert(TutorialEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = TutorialEntry.buildTutorialUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PLACE_IMAGE:
                id = db.insert(PlaceImageEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = PlaceImageEntry.buildPlaceImageUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case SUB_CATEGORY:
                id = db.insert(SubCategoryEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = SubCategoryEntry.buildSubCategoryUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SUB_CATEGORY_BY_PLACE:
                id = db.insert(SubCategoryByPlaceEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = SubCategoryByPlaceEntry.buildSubCategoryByPlaceUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " +uri);
                }
                break;
            case SUB_CATEGORY_BY_TUTORIAL:
                id = db.insert(SubCategoryByTutorialEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = SubCategoryByTutorialEntry.buildSubCategoryByTutorialUri(id);
                }else{
                    throw new android.database.SQLException("Failded to insert row into "+ uri);
                }
                break;
            case USER_BY_PLACE:
                id = db.insert(UserByPlaceEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = UserByPlaceEntry.buildUserByPlaceUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case EVENT:
                id = db.insert(EventEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = EventEntry.buildEventUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case EVENT_BY_USER:
                id = db.insert(EventByUserEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = EventByUserEntry.buildEventByUser(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into "+ uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted=0;
        final SQLiteDatabase db = recappDBHelper.getWritableDatabase();
        if ( null == selection ) selection = "1";
        switch (uriMatcher.match(uri)){
            case USER:
                rowsDeleted = db.delete(UserEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PLACE:
                rowsDeleted = db.delete(PlaceEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case REMINDER:
                rowsDeleted = db.delete(ReminderEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case COMMENT:
                rowsDeleted = db.delete(CommentEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(CategoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case TUTORIAL:
                rowsDeleted = db.delete(TutorialEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PLACE_IMAGE:
                rowsDeleted = db.delete(PlaceImageEntry.TABLE_NAME,selection,selectionArgs);
                break;


            case SUB_CATEGORY:
                rowsDeleted = db.delete(SubCategoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case SUB_CATEGORY_BY_PLACE:
                rowsDeleted = db.delete(SubCategoryByPlaceEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case SUB_CATEGORY_BY_TUTORIAL:
                rowsDeleted = db.delete(SubCategoryByTutorialEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case USER_BY_PLACE:
                rowsDeleted = db.delete(UserByPlaceEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case EVENT:
                rowsDeleted = db.delete(EventEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case EVENT_BY_USER:
                rowsDeleted = db.delete(EventByUserEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;

    }
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = recappDBHelper.getWritableDatabase();
        int rowUpdated = 0;
        switch (uriMatcher.match(uri)){
            case USER:
                rowUpdated = db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLACE:
                rowUpdated = db.update(PlaceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REMINDER:
                rowUpdated = db.update(ReminderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COMMENT:
                rowUpdated = db.update(CommentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CATEGORY:
                rowUpdated = db.update(CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TUTORIAL:
                rowUpdated = db.update(TutorialEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLACE_IMAGE:
                rowUpdated = db.update(PlaceImageEntry.TABLE_NAME, values, selection, selectionArgs);
                break;


            case SUB_CATEGORY:
                rowUpdated = db.update(SubCategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUB_CATEGORY_BY_PLACE:
                rowUpdated = db.update(SubCategoryByPlaceEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case SUB_CATEGORY_BY_TUTORIAL:
                rowUpdated = db.update(SubCategoryByTutorialEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case USER_BY_PLACE:
                rowUpdated = db.update(UserByPlaceEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case EVENT:
                rowUpdated = db.update(EventEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case EVENT_BY_USER:
                rowUpdated = db.update(EventByUserEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowUpdated;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = recappDBHelper.getWritableDatabase();
        int returnCount = 0;
        switch (uriMatcher.match(uri)){
            case USER:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(UserEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case PLACE:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(PlaceEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case REMINDER:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(ReminderEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case COMMENT:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(CommentEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case CATEGORY:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(CategoryEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case TUTORIAL:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(TutorialEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case PLACE_IMAGE:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(PlaceImageEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case SUB_CATEGORY:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(SubCategoryEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case SUB_CATEGORY_BY_PLACE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value: values){
                        long id = db.insert(SubCategoryByPlaceEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }

                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case SUB_CATEGORY_BY_TUTORIAL:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(SubCategoryByTutorialEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case USER_BY_PLACE:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for (ContentValues value:values){
                        long id = db.insert(UserByPlaceEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case EVENT:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(EventEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            case EVENT_BY_USER:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(EventByUserEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri,values);

        }

    }

}
