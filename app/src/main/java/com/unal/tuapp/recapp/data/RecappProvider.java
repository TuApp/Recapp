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
    static final int TUTORIAL_IMAGE = 800;
    static final int TUTORIAL_IMAGE_WITH_TUTORIAL= 810;
    static final int TUTORIAL_IMAGE_WITH_ID = 820;
    static final int SUB_CATEGORY = 900;
    static final int SUB_CATEGORY_WITH_CATEGORY = 910;
    static final int SUB_CATEGORY_WITH_TUTORIAL = 920;
    static final int SUB_CATEGORY_WITH_PLACE = 930;
    static final int SUB_CATEGORY_WITH_ID = 940;
    static final int USER_BY_PLACE = 1000;
    static final int USER_BY_PLACE_USER = 1010;
    static final int USER_BY_PLACE_PLACE = 1020;
    static final int USER_BY_PLACE_ID = 1030;

    private static final SQLiteQueryBuilder reminderByUser;
    private static final SQLiteQueryBuilder reminderByPlace;
    private static final SQLiteQueryBuilder commentByUser;
    private static final SQLiteQueryBuilder commentByPlace;
    private static final SQLiteQueryBuilder placeImagePlace;
    private static final SQLiteQueryBuilder tutorialImageTutorial;
    private static final SQLiteQueryBuilder subCategoryCategory;
    private static final SQLiteQueryBuilder subCategoryPlace;
    private static final SQLiteQueryBuilder subCategoryTutorial;
    private static final SQLiteQueryBuilder userByPlaceUser;
    private static final SQLiteQueryBuilder userByPlacePlace;

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
        tutorialImageTutorial = new SQLiteQueryBuilder();
        tutorialImageTutorial.setTables(
                TutorialImageEntry.TABLE_NAME+ " INNER JOIN " +
                        TutorialEntry.TABLE_NAME + " ON " +
                        TutorialImageEntry.TABLE_NAME+"."+TutorialImageEntry.COLUMN_TUTORIAL_KEY +
                        " = " + TutorialEntry.TABLE_NAME+"."+TutorialEntry._ID
        );
        subCategoryCategory = new SQLiteQueryBuilder();
        subCategoryCategory.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        CategoryEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry.COLUMN_CATEGORY_KEY +
                        " = " + CategoryEntry.TABLE_NAME+"."+CategoryEntry._ID
        );
        subCategoryTutorial = new SQLiteQueryBuilder();
        subCategoryTutorial.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        TutorialEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry.COLUMN_TUTORIAL_KEY+
                        " = " + TutorialEntry.TABLE_NAME+"."+TutorialEntry._ID
        );
        subCategoryPlace = new SQLiteQueryBuilder();
        subCategoryPlace.setTables(
                SubCategoryEntry.TABLE_NAME + " INNER JOIN " +
                        PlaceEntry.TABLE_NAME + " ON " +
                        SubCategoryEntry.TABLE_NAME+"."+SubCategoryEntry.COLUMN_PLACE_KEY+
                        " = " + PlaceEntry.TABLE_NAME+"."+PlaceEntry._ID
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

        //Mathcer for category
        matcher.addURI(authority,RecappContract.PATH_CATEGORY,CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_CATEGORY+"/#",CATEGORY_WITH_ID);

        //Mathcers for tutorial
        matcher.addURI(authority,RecappContract.PATH_TUTORIAL,TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_TUTORIAL+"/#",TUTORIAL_WITH_ID);

        //Mathcers for place with image
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE,PLACE_IMAGE);
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE+"/"+RecappContract.PATH_PLACE+"/#",PLACE_IMAGE_WITH_PLACE);
        matcher.addURI(authority,RecappContract.PATH_PLACEIMAGE+"/#",PLACE_IMAGE_WITH_ID);

        //Mathcers for tutorial with image
        matcher.addURI(authority,RecappContract.PATH_TUTORIALIAMGE,TUTORIAL_IMAGE);
        matcher.addURI(authority,RecappContract.PATH_TUTORIALIAMGE+"/"+RecappContract.PATH_TUTORIAL+"/#",TUTORIAL_IMAGE_WITH_TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_TUTORIALIAMGE+"/#",TUTORIAL_IMAGE_WITH_ID);

        //Mathcers for sub category
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY,SUB_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/"+RecappContract.PATH_CATEGORY+"/#",SUB_CATEGORY_WITH_CATEGORY);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/"+RecappContract.PATH_TUTORIAL+"/#",SUB_CATEGORY_WITH_TUTORIAL);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/"+RecappContract.PATH_PLACE+"/#",SUB_CATEGORY_WITH_PLACE);
        matcher.addURI(authority,RecappContract.PATH_SUBCATEGORY+"/#",SUB_CATEGORY_WITH_ID);

        //Matchers for user by place
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE,USER_BY_PLACE);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/"+RecappContract.PATH_USER+"/#",USER_BY_PLACE_USER);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/"+RecappContract.PATH_PLACE+"/#",USER_BY_PLACE_PLACE);
        matcher.addURI(authority,RecappContract.PATH_USERBYPLACE+"/#",USER_BY_PLACE_ID);
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
            case TUTORIAL_IMAGE:
                return TutorialImageEntry.CONTENT_TYPE;
            case TUTORIAL_IMAGE_WITH_TUTORIAL:
                return TutorialImageEntry.CONTENT_TYPE;
            case TUTORIAL_IMAGE_WITH_ID:
                return TutorialImageEntry.CONTENT_ITEM_TYPE;
            case SUB_CATEGORY:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_CATEGORY:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_TUTORIAL:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_PLACE:
                return SubCategoryEntry.CONTENT_TYPE;
            case SUB_CATEGORY_WITH_ID:
                return SubCategoryEntry.CONTENT_ITEM_TYPE;
            case USER_BY_PLACE:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_USER:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_PLACE:
                return UserByPlaceEntry.CONTENT_TYPE;
            case USER_BY_PLACE_ID:
                return UserByPlaceEntry.CONTENT_ITEM_TYPE;
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
            case TUTORIAL_IMAGE:
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
            case TUTORIAL_IMAGE_WITH_TUTORIAL:
                tutorialId = TutorialImageEntry.getTutorialFromUri(uri);
                selection = TutorialImageEntry.COLUMN_TUTORIAL_KEY + " = ? ";
                retCursor = tutorialImageTutorial.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+tutorialId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case TUTORIAL_IMAGE_WITH_ID:
                long tutorialImageId = TutorialImageEntry.getIdFromUri(uri);
                selection = TutorialImageEntry._ID + " = ? ";
                retCursor = recappDBHelper.getReadableDatabase().query(
                        TutorialImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{""+tutorialImageId},
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
            case SUB_CATEGORY_WITH_TUTORIAL:
                tutorialId = SubCategoryEntry.getTutorialFromUri(uri);
                selection = TutorialEntry._ID + " = ? ";
                retCursor = subCategoryCategory.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+tutorialId},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUB_CATEGORY_WITH_PLACE:
                placeId = SubCategoryEntry.getPlaceFromUri(uri);
                selection = PlaceEntry._ID + " = ? ";
                retCursor = subCategoryPlace.query(
                        recappDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        new String[]{""+placeId},
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

            case TUTORIAL_IMAGE:
                id = db.insert(TutorialImageEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = TutorialImageEntry.buildTutorialImageUri(id);
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
            case USER_BY_PLACE:
                id = db.insert(UserByPlaceEntry.TABLE_NAME,null,values);
                if(id>0){
                    returnUri = UserByPlaceEntry.buildUserByPlaceUri(id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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

            case TUTORIAL_IMAGE:
                rowsDeleted = db.delete(TutorialImageEntry.TABLE_NAME,selection,selectionArgs);
                break;

            case SUB_CATEGORY:
                rowsDeleted = db.delete(SubCategoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case USER_BY_PLACE:
                rowsDeleted = db.delete(UserByPlaceEntry.TABLE_NAME,selection,selectionArgs);
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

            case TUTORIAL_IMAGE:
                rowUpdated = db.update(TutorialImageEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case SUB_CATEGORY:
                rowUpdated = db.update(SubCategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_BY_PLACE:
                rowUpdated = db.update(UserByPlaceEntry.TABLE_NAME,values,selection,selectionArgs);
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
            case TUTORIAL_IMAGE:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for(ContentValues value:values){
                        long id = db.insert(TutorialImageEntry.TABLE_NAME,null,value);
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
            default:
                return super.bulkInsert(uri,values);

        }

    }

}
