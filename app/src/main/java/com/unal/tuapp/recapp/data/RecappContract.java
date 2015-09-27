package com.unal.tuapp.recapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

import android.provider.BaseColumns;

/**
 * Created by andresgutierrez on 7/29/15.
 */
public class RecappContract {

    public static final String CONTENT_AUTHORITY = "com.unal.tuapp.recapp.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";
    public static final String PATH_PLACE = "place";
    public static final String PATH_REMINDER = "reminder";
    public static final String PATH_COMMENT = "comment";
    public static final String PATH_PLACEIMAGE = "placeImage";
    public static final String PATH_TUTORIAL = "tutorial";
    public static final String PATH_TUTORIALIAMGE = "tutorialImage";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_SUBCATEGORY = "subCategory";
    public static final String PATH_SUBCATEGORY_BY_PLACE = "subCategoryByPlace";
    public static final String PATH_SUBCATEGORY_BY_TUTORIAL = "subCategoryByTutorial";
    public static final String PATH_USERBYPLACE = "userByPlace";
    public static final String PATH_EVENT = "event";
    public static final String PATH_EVENTBYUSER = "eventByUser";

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "User";

        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_LASTNAME = "user_lastname";
        public static final String COLUMN_USER_IMAGE = "user_image";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LOG = "log";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUserEmail(String email) {
            return CONTENT_URI.buildUpon().
                    appendPath(email).build();
        }

        public static String getEmailFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }


    }

    public static class PlaceEntry implements BaseColumns {
        public static final String TABLE_NAME = "Place";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LOG = "log";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RATING = "rating"; //We use it to sort the results
        public static final String COLUMN_IMAGE_FAVORITE = "imageFavorite";
        public static final String COLUMN_WEB = "web";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;

        public static Uri buildPlaceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "Reminder";

        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_NAME = "name_reminder";
        public static final String COLUMN_DESCRIPTION = "description_reminder";
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_USER_KEY = "user_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        public static Uri buildReminderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReminderUserUri(long user) {
            return CONTENT_URI.buildUpon().
                    appendPath(PATH_PLACE).
                    appendPath(""+user).build();
        }

        public static Uri buildReminderPlaceUri(long place) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_USER)
                    .appendPath("" + place).build();
        }

        public static long getUserFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getPlaceFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "Comment";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_USER_KEY = "user_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENT;

        public static Uri buildCommentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCommentUserUri(long user) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_USER)
                    .appendPath(""+user).build();
        }

        public static Uri buildCommentPlaceUri(long place) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE)
                    .appendPath("" + place).build();
        }
        public static Uri buildCommentPlaceUserUri(long place){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE)
                    .appendPath(PATH_USER)
                    .appendPath(""+place).build();
        }

        public static long getUserFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getPlaceFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getPlaceUserFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(3));
        }


        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static class PlaceImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "PlaceImage";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_WORTH = "worth";
        public static final String COLUMN_PLACE_KEY = "place_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACEIMAGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACEIMAGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACEIMAGE;

        public static Uri buildPlaceImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPlaceImagePlaceUri(long place) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE)
                    .appendPath("" + place).build();
        }

        public static long getPlaceFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class TutorialEntry implements BaseColumns {
        public static final String TABLE_NAME = "Tutorial";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TUTORIAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIAL;

        public static Uri buildTutorialUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class TutorialImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "TutorialImage";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_WORTH = "worth";
        public static final String COLUMN_TUTORIAL_KEY = "tutorial_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TUTORIALIAMGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIALIAMGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIALIAMGE;

        public static Uri buildTutorialImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTutorialImageTutorialUri(long tutorial) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_TUTORIAL)
                    .appendPath("" + tutorial).build();
        }

        public static long getTutorialFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Category";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image_category";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class SubCategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "SubCategory";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY_KEY = "category_id";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBCATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBCATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBCATEGORY;

        public static Uri buildSubCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSubCategoryCategoryUri(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_CATEGORY).build();
        }
        public static Uri buildSubCategoryCategoryUri(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_CATEGORY)
                    .appendPath(""+id).build();
        }
        public static long getCategoryFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
    public static class SubCategoryByPlaceEntry implements BaseColumns{
        public static final String TABLE_NAME = "SubCategoryByPlace";

        public static final String COLUMN_PLACE_KEY = "place_id";
        public static final String COLUMN_SUBCATEGORY_KEY = "subCategory_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBCATEGORY_BY_PLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/" +PATH_SUBCATEGORY_BY_PLACE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SUBCATEGORY_BY_PLACE;

        public static Uri buildSubCategoryByPlaceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildSubCategoryByPlacePlaceSubCategory(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE)
                    .appendPath(PATH_SUBCATEGORY).build();
        }

        public static Uri buildSubCategoryByPlaceByPlace(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE).build();
        }
        public static Uri buildSubCategoryByPlaceByPlace(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_PLACE)
                    .appendPath(""+id).build();
        }
        public static Uri buildSubCategoryByPlaceBySubCategory(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SUBCATEGORY).build();
        }
        public static Uri buildSubCategoryByPlaceBySubCategory(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SUBCATEGORY)
                    .appendPath(""+id).build();
        }

        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
        public static long getIdFromPlaceUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getIdFromSubCategoryUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
    public static class SubCategoryByTutorialEntry implements BaseColumns{
        public static final String TABLE_NAME = "SubCategoryByTutorial";

        public static final String COLUMN_TUTORIAL_KEY = "tutorial_id";
        public static final String COLUMN_SUBCATEGORY_KEY = "subCategory_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBCATEGORY_BY_TUTORIAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/" +PATH_SUBCATEGORY_BY_TUTORIAL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SUBCATEGORY_BY_TUTORIAL;

        public static Uri buildSubCategoryByTutorialUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildSubCategoryByTutorialSubCategoryTutorial(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_TUTORIAL)
                    .appendPath(PATH_SUBCATEGORY).build();
        }

        public static Uri buildSubCategoryByTutorialByTutorial(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_TUTORIAL).build();
        }
        public static Uri buildSubCategoryByTutorialByTutorial(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_TUTORIAL)
                    .appendPath(""+id).build();
        }
        public static Uri buildSubCategoryByTutorialBySubCategory(){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SUBCATEGORY).build();
        }
        public static Uri buildSubCategoryByTutorialBySubCategory(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SUBCATEGORY)
                    .appendPath(""+id).build();
        }

        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
        public static long getIdTutorialUir(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getIdSubCategoryUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }


    }
    public static class UserByPlaceEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserByPlace";

        public static final String COLUMN_USER_KEY = "user_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERBYPLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERBYPLACE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERBYPLACE;

        public static Uri buildUserByPlaceUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildUserByPlaceUserUri(long id){
            return CONTENT_URI.buildUpon().
                    appendPath(PATH_USER).
                    appendPath(""+id).build();
        }
        public static Uri buildUserByPlacePlaceUri(long id){
            return CONTENT_URI.buildUpon().
                    appendPath(PATH_PLACE).
                    appendPath(""+id).build();
        }

        public static long getPlaceFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getUserFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
    public static class Event implements BaseColumns{
        public static final String TABLE_NAME ="Event";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CREATOR = "creator"; // This is the email of the user who creates the event
        public static final String COLUMN_DATE = "start_Date";
        public static final String COLUMN_ADDRESS  = "address";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LOG = "log";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final Uri buildEventUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
    public static class EventByUser implements BaseColumns{
        public static final String TABLE_NAME = "EventByUser";

        public static final String COLUMN_KEY_USER = "user_key";
        public static final String COLUMN_KEY_EVENT = "evnet_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTBYUSER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTBYUSER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTBYUSER;

        public static final Uri buildEventByUser(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static final Uri buildEventByUserEvent(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_EVENT)
                    .appendPath(""+id).build();
        }
        public static final Uri buildEventByUserUser(long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_USER)
                    .appendPath(""+id).build();
        }

        public static final long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
        public static final long getEventFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static final long getUserFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

}

