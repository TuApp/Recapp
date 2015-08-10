package com.unal.tuapp.recapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.unal.tuapp.recapp.data.RecappContract.*;

/**
 * Created by andresgutierrez on 8/2/15.
 */
public class RecappDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recaap.db";
    private static RecappDBHelper instance = null;

    private RecappDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public static RecappDBHelper getInstance(Context context){
        if(instance == null){
            synchronized (RecappDBHelper.class){
                if(instance == null){
                    instance = new RecappDBHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ( "+
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL ," +
                UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_LASTNAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_IMAGE + " BLOB," +
                UserEntry.COLUMN_LAT + " REAL,"+
                UserEntry.COLUMN_LOG + " REAL );";
        final String CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " ( "+
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_LAT + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_LOG + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_ADDRESS + " TEXT UNIQUE NOT NULL, " +
                PlaceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_RATING + " REAL DEFAULT 0.0 , "+
                PlaceEntry.COLUMN_IMAGE_FAVORITE + " BLOB NOT NULL);";

        final String CREATE_REMINDER_TABLE = "CREATE TABLE "+ ReminderEntry.TABLE_NAME +" ( "+
                ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReminderEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                ReminderEntry.COLUMN_END_DATE + " INTEGER NOT NULL, "+
                ReminderEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                ReminderEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ReminderEntry.COLUMN_USER_KEY + " INTEGER," +
                ReminderEntry.COLUMN_PLACE_KEY + " INTEGER, "+
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_USER_KEY +" ) REFERENCES " +
                UserEntry.TABLE_NAME + " ( "+ UserEntry._ID +" ), " +
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_COMMENT_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " ( "+
                CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CommentEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "+
                CommentEntry.COLUMN_RATING + " REAL NOT NULL," +
                CommentEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                CommentEntry.COLUMN_USER_KEY + " INTEGER, "+
                CommentEntry.COLUMN_PLACE_KEY + " INTEGER, " +
                "FOREIGN KEY ("  + CommentEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " ("+ UserEntry._ID +"), " +
                "FOREIGN KEY ( " + CommentEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_PLACE_IMAGE_TABLE = "CREATE TABLE "+ PlaceImageEntry.TABLE_NAME + " ( " +
                PlaceImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlaceImageEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                PlaceImageEntry.COLUMN_WORTH + " INTEGER NOT NULL DEFAULT 0, " +
                PlaceImageEntry.COLUMN_PLACE_KEY + " INTEGER, "+
                "FOREIGN KEY ( " + PlaceImageEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_TUTORIAL_TABLE = "CREATE TABLE " + TutorialEntry.TABLE_NAME + " ( " +
                TutorialEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TutorialEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TutorialEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL );";
        final String CREATE_TUTORIAL_IMAGE_TABLE = "CREATE TABLE " + TutorialImageEntry.TABLE_NAME + " ( " +
                TutorialImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TutorialImageEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                TutorialImageEntry.COLUMN_WORTH + " INTEGER NOT NULL DEFAULT 0, "+
                TutorialImageEntry.COLUMN_TUTORIAL_KEY+ " INTEGER, " +
                "FOREIGN KEY ( "+ TutorialImageEntry.COLUMN_TUTORIAL_KEY + " ) REFERENCES " +
                TutorialEntry.TABLE_NAME + " ( " + TutorialEntry._ID + " ) );";
        final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " ( " +
                CategoryEntry._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL );";
        final String CREATE_SUB_CATEGORY_TABLE = "CREATE TABLE " + SubCategoryEntry.TABLE_NAME + "(" +
                SubCategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubCategoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                SubCategoryEntry.COLUMN_CATEGORY_KEY + " INTEGER, "+
                SubCategoryEntry.COLUMN_PLACE_KEY + " INTEGER, "+
                SubCategoryEntry.COLUMN_TUTORIAL_KEY + " INTEGER, "+
                "FOREIGN KEY (" + SubCategoryEntry.COLUMN_CATEGORY_KEY +") REFERENCES " +
                CategoryEntry.TABLE_NAME + "("+CategoryEntry._ID+"), " +
                "FOREIGN KEY (" + SubCategoryEntry.COLUMN_TUTORIAL_KEY +") REFERENCES "+
                TutorialEntry.TABLE_NAME +"("+TutorialEntry._ID+"), " +
                "FOREIGN KEY (" + SubCategoryEntry.COLUMN_PLACE_KEY+") REFERENCES "+
                PlaceEntry.TABLE_NAME +"("+PlaceEntry._ID+") );";
        final String CREATE_USER_BY_PLACE_TABLE = "CREATE TABLE " + UserByPlaceEntry.TABLE_NAME + "(" +
                UserByPlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserByPlaceEntry.COLUMN_USER_KEY +" INTEGER NOT NULL, " +
                UserByPlaceEntry.COLUMN_PLACE_KEY + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + UserByPlaceEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + "(" + UserEntry._ID+"), " +
                "FOREIGN KEY (" + UserByPlaceEntry.COLUMN_PLACE_KEY +") REFERENCES " +
                PlaceEntry.TABLE_NAME + "(" + PlaceEntry._ID+") );";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACE_TABLE);
        sqLiteDatabase.execSQL(CREATE_REMINDER_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACE_IMAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TUTORIAL_TABLE);
        sqLiteDatabase.execSQL(CREATE_TUTORIAL_IMAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUB_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_BY_PLACE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceImageEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TutorialEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TutorialImageEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubCategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserByPlaceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


}
