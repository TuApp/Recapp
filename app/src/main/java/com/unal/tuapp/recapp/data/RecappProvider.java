package com.unal.tuapp.recapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
    static final int USER_BY_EMAIL = 110;
    static final int PLACE = 200;
    static final int REMINDER = 300;
    static final int REMINDER_WITH_USER = 310;
    static final int REMINDER_WITH_PLACE = 320;
    static final int COMMENT = 400;
    static final int COMMENT_WITH_USER = 410;
    static final int COMMENT_WITH_PLACE = 420;
    static final int CATEGORY = 500;
    static final int TUTORIAL = 600;
    static final int PLACE_IMAGE= 700;
    static final int PLACE_IMAGE_WITH_PLACE = 710;
    static final int TUTORIAL_IMAGE = 800;
    static final int TUTORIAL_IMAGE_WITH_TUTORIAL= 810;
    static final int SUB_CATEGORY = 900;
    static final int SUB_CATEGORY_WITH_CATEGORY = 910;
    static final int SUB_CATEGORY_WITH_TUTORIAL = 920;
    static final int SUB_CATEGORY_WITH_PLACE = 930;

    private static final SQLiteQueryBuilder reminderByUser;
    private static final SQLiteQueryBuilder reminderByPlace;
    private static final SQLiteQueryBuilder commentByUser;
    private static final SQLiteQueryBuilder commentByPlace;
    private static final SQLiteQueryBuilder placeImagePlace;
    private static final SQLiteQueryBuilder tutorialImageTutorial;
    private static final SQLiteQueryBuilder subCategoryCategory;
    private static final SQLiteQueryBuilder subCategoryPlace;
    private static final SQLiteQueryBuilder subCategoryTutorial;

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
                        PlaceImageEntry.TABLE_NAME + " ON " +
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

    }




    static UriMatcher buildMatcher(){
        return null;

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
                break;
            case USER_BY_EMAIL:
                break;
            case PLACE:
                break;
            case REMINDER:
                break;
            case REMINDER_WITH_USER:
                break;
            case REMINDER_WITH_PLACE:
                break;
            case COMMENT:
                break;
            case COMMENT_WITH_USER:
                break;
            case COMMENT_WITH_PLACE:
                break;
            case CATEGORY:
                break;
            case TUTORIAL:
                break;
            case PLACE_IMAGE:
                break;
            case PLACE_IMAGE_WITH_PLACE:
                break;
            case TUTORIAL_IMAGE:
                break;
            case TUTORIAL_IMAGE_WITH_TUTORIAL:
                break;
            case SUB_CATEGORY:
                break;
            case SUB_CATEGORY_WITH_CATEGORY:
                break;
            case SUB_CATEGORY_WITH_TUTORIAL:
                break;
            case SUB_CATEGORY_WITH_PLACE:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        return "";

    }

    @Override
     public Cursor query(Uri uri,String [] projection,String selection,String[] selectionArgs,
                         String sortOrder){
        Cursor retCursor = null;
        switch (uriMatcher.match(uri)){
            case USER:
                retCursor = null;
                break;
            case USER_BY_EMAIL:
                retCursor = null;
                break;
            case PLACE:
                break;
            case REMINDER:
                retCursor = null;
                break;
            case REMINDER_WITH_USER:
                retCursor = null;
                break;
            case REMINDER_WITH_PLACE:
                retCursor = null;
                break;
            case COMMENT:
                retCursor = null;
                break;
            case COMMENT_WITH_USER:
                retCursor = null;
                break;
            case COMMENT_WITH_PLACE:
                retCursor = null;
                break;
            case CATEGORY:
                retCursor = null;
                break;
            case TUTORIAL:
                retCursor = null;
                break;
            case PLACE_IMAGE:
                retCursor = null;
                break;
            case PLACE_IMAGE_WITH_PLACE:
                retCursor = null;
                break;
            case TUTORIAL_IMAGE:
                retCursor = null;
                break;
            case TUTORIAL_IMAGE_WITH_TUTORIAL:
                retCursor = null;
                break;
            case SUB_CATEGORY:
                retCursor = null;
                break;
            case SUB_CATEGORY_WITH_CATEGORY:
                retCursor = null;
                break;
            case SUB_CATEGORY_WITH_TUTORIAL:
                retCursor = null;
                break;
            case SUB_CATEGORY_WITH_PLACE:
                retCursor = null;
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
        switch (uriMatcher.match(uri)){
            case USER:
                break;
            case USER_BY_EMAIL:
                break;
            case PLACE:
                break;
            case REMINDER:
                break;
            case REMINDER_WITH_USER:
                break;
            case REMINDER_WITH_PLACE:
                break;
            case COMMENT:
                break;
            case COMMENT_WITH_USER:
                break;
            case COMMENT_WITH_PLACE:
                break;
            case CATEGORY:
                break;
            case TUTORIAL:
                break;
            case PLACE_IMAGE:
                break;
            case PLACE_IMAGE_WITH_PLACE:
                break;
            case TUTORIAL_IMAGE:
                break;
            case TUTORIAL_IMAGE_WITH_TUTORIAL:
                break;
            case SUB_CATEGORY:
                break;
            case SUB_CATEGORY_WITH_CATEGORY:
                break;
            case SUB_CATEGORY_WITH_TUTORIAL:
                break;
            case SUB_CATEGORY_WITH_PLACE:
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return 0;
    }

}
