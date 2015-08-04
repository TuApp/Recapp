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
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";
    public static final String PATH_PLACE = "place";
    public static final String PATH_REMINDER = "reminder";
    public static final String PATH_COMMENT = "comment";
    public static final String PATH_PLACEIMAGE = "placeImage";
    public static final String PATH_TUTORIAL = "tutorial";
    public static final String PATH_TUTORIALIAMGE = "tutorialImage";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_SUBCATEGORY = "subCategory";

    public static class UserEntry implements BaseColumns{
        public static final String TABLE_NAME= "User";

        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_USER_NAME= "user_name";
        public static final String COLUMN_USER_LASTNAME = "user_lastname";
        public static final String COLUMN_USER_IMAGE = "user_image";
        public static final String COLUMN_LAT =  "lat";
        public static final String COLUMN_LOG = "log";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" +PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" + PATH_USER;

        public static Uri buildUserUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildUserEmail(String email){
            return CONTENT_URI.buildUpon().
                    appendQueryParameter(COLUMN_EMAIL,email).build();
        }

        public static String getEmailFromUri(Uri uri){
            String email = uri.getQueryParameter(COLUMN_EMAIL);
            return email;
        }



    }
    public static class PlaceEntry implements BaseColumns{
        public static final String TABLE_NAME = "Place";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LOG = "log";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_PLACE;
        public static  Uri buildPlaceUri(long id){
            return  ContentUris.withAppendedId(CONTENT_URI,id);

        }

    }
    public static class ReminderEntry implements BaseColumns{
        public static final String TABLE_NAME= "Reminder";

        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_USER_KEY = "user_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        public static Uri buildReminderUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildReminderUserUri(String user){
            return CONTENT_URI.buildUpon().
                    appendPath(user).build();
        }
        public static Uri buildReminderPlaceUri(String place){
            return CONTENT_URI.buildUpon()
                    .appendPath(place).build();
        }
        public static String getUserFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getPlaceFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class CommentEntry implements BaseColumns{
        public static final String TABLE_NAME = "Comment";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_USER_KEY = "user_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COMMENT;

        public static Uri buildCommentUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildCommentUserUri(String user){
            return CONTENT_URI.buildUpon()
                    .appendPath(user).build();
        }
        public static Uri buildCommentPlaceUri(String place){
            return CONTENT_URI.buildUpon()
                    .appendPath(place).build();
        }

        public static String getUserFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getPlaceFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class PlaceImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "PlaceImage";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_PLACE_KEY = "place_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACEIMAGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_PLACEIMAGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACEIMAGE;

        public static Uri buildPlaceImageUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildPlaceImagePlaceUri(String place){
            return CONTENT_URI.buildUpon()
                    .appendPath(place).build();
        }

        public static String getPlaceFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static class TutorialEntry  implements BaseColumns{
        public static final String TABLE_NAME = "Tutorial";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TUTORIAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIAL;

        public static Uri buildTutorialUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
    public static class TutorialImageEntry implements BaseColumns{
        public static final String TABLE_NAME = "TutorialImage";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_TUTORIAL_KEY ="tutorial_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TUTORIALIAMGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" + PATH_TUTORIALIAMGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TUTORIALIAMGE;

        public static Uri buildTutorialImageUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildTutorialImageTutorialUri(String tutorial){
            return CONTENT_URI.buildUpon()
                    .appendPath(tutorial).build();
        }

        public static String getTutorialFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static class CategoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "Category";

        public static final String COLUMN_NAME = "name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/"+ PATH_CATEGORY;

        public static Uri buildCategoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
    public static class SubCategoryEntry implements BaseColumns{
        public static final String TABLE_NAME = "SubCategory";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY_KEY = "category_id";
        public static final String COLUMN_TUTORIAL_KEY = "tutorial_id";
        public static final String COLUMN_PLACE_KEY = "place_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBCATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_SUBCATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBCATEGORY;

        public static Uri buildSubCategoryUir(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildSubCategoryCategoryUri(String category){
            return CONTENT_URI.buildUpon()
                    .appendPath(category).build();
        }
        public static Uri buildSubCategoryTutorialUri(String tutorial){
            return CONTENT_URI.buildUpon()
                    .appendPath(tutorial).build();
        }
        public static Uri buildSubCategoryPlaceUri(String place){
            return CONTENT_URI.buildUpon()
                    .appendPath(place).build();
        }

        public static String getCategoryFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getTutorialFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getPlaceFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
