package com.unal.tuapp.recapp.data;

import android.content.Context;
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
                UserEntry._ID + " INTEGER PRIMARY KEY , "+
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL ," +
                UserEntry.COLUMN_USER_NAME + " TEXT, " +
                UserEntry.COLUMN_USER_LASTNAME + " TEXT, " +
                UserEntry.COLUMN_USER_IMAGE + " BLOB ,"+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE( "+ UserEntry._ID+" ) ON CONFLICT REPLACE); " ;
        final String CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " ( "+
                PlaceEntry._ID + " INTEGER PRIMARY KEY , "+
                PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_LAT + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_LOG + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_WEB + " TEXT NOT NULL, "+
                PlaceEntry.COLUMN_ADDRESS + " TEXT UNIQUE NOT NULL, " +
                PlaceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_RATING + " REAL DEFAULT 0.0 , "+
                PlaceEntry.COLUMN_IMAGE_FAVORITE + " BLOB NOT NULL , " +
                PlaceEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL , " +
                PlaceEntry.COLUMN_PASSWORD+" TEXT NOT NULL, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ PlaceEntry._ID+" ) ON CONFLICT REPLACE );";

        final String CREATE_REMINDER_TABLE = "CREATE TABLE "+ ReminderEntry.TABLE_NAME +" ( "+
                ReminderEntry._ID + " INTEGER PRIMARY KEY , " +
                ReminderEntry.COLUMN_END_DATE + " INTEGER NOT NULL, "+
                ReminderEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                ReminderEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ReminderEntry.COLUMN_NOTIFICATION + " INTEGER NOT NULL, " +
                ReminderEntry.COLUMN_USER_KEY + " INTEGER," +
                ReminderEntry.COLUMN_PLACE_KEY + " INTEGER, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( " +ReminderEntry._ID+" ) ON CONFLICT REPLACE ,"+
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_USER_KEY +" ) REFERENCES " +
                UserEntry.TABLE_NAME + " ( "+ UserEntry._ID +" ), " +
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_COMMENT_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " ( "+
                CommentEntry._ID + " INTEGER PRIMARY KEY , " +
                CommentEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "+
                CommentEntry.COLUMN_RATING + " REAL NOT NULL," +
                CommentEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                CommentEntry.COLUMN_USER_KEY + " INTEGER , "+
                CommentEntry.COLUMN_PLACE_KEY + " INTEGER , " +
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ CommentEntry._ID +" ) ON CONFLICT REPLACE ,"+
                "FOREIGN KEY ("  + CommentEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " ("+ UserEntry._ID +"), " +
                "FOREIGN KEY ( " + CommentEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_PLACE_IMAGE_TABLE = "CREATE TABLE "+ PlaceImageEntry.TABLE_NAME + " ( " +
                PlaceImageEntry._ID + " INTEGER PRIMARY KEY , "+
                PlaceImageEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                PlaceImageEntry.COLUMN_WORTH + " INTEGER NOT NULL DEFAULT 0, " +
                PlaceImageEntry.COLUMN_PLACE_KEY + " INTEGER, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+PlaceImageEntry.COLUMN_PLACE_KEY+" , "+ PlaceImageEntry._ID+" ) ON CONFLICT REPLACE , "+
                "FOREIGN KEY ( " + PlaceImageEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_TUTORIAL_TABLE = "CREATE TABLE " + TutorialEntry.TABLE_NAME + " ( " +
                TutorialEntry._ID + " INTEGER PRIMARY KEY , " +
                TutorialEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TutorialEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                TutorialEntry.COLUMN_LINK_VIDEO + " TEXT NOT NULL , " +
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ TutorialEntry._ID+" ) ON CONFLICT REPLACE );";
        final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " ( " +
                CategoryEntry._ID + "  INTEGER PRIMARY KEY , " +
                CategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, "+
                CategoryEntry.COLUMN_IMAGE + " BLOB NOT NULL ," +
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 ,"+
                "UNIQUE ( "+ CategoryEntry._ID +" ) ON CONFLICT REPLACE );";
        final String CREATE_SUB_CATEGORY_TABLE = "CREATE TABLE " + SubCategoryEntry.TABLE_NAME + "(" +
                SubCategoryEntry._ID + " INTEGER PRIMARY KEY , " +
                SubCategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                SubCategoryEntry.COLUMN_CATEGORY_KEY + " INTEGER, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ SubCategoryEntry._ID +" ) ON CONFLICT REPLACE, "+
                "FOREIGN KEY (" + SubCategoryEntry.COLUMN_CATEGORY_KEY +") REFERENCES " +
                CategoryEntry.TABLE_NAME + "( "+CategoryEntry._ID+" ) );";
        final String CREATE_SUB_CATEGORY_BY_PLACE = "CREATE TABLE "+ SubCategoryByPlaceEntry.TABLE_NAME + "("+
                SubCategoryByPlaceEntry._ID + " INTEGER PRIMARY KEY , "+
                SubCategoryByPlaceEntry.COLUMN_PLACE_KEY+ " INTEGER NOT NULL,"+
                SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY+ " INTEGER NOT NULL, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ SubCategoryByPlaceEntry._ID+" )  ON CONFLICT REPLACE ,"+
                "FOREIGN KEY (" +SubCategoryByPlaceEntry.COLUMN_PLACE_KEY+") REFERENCES "+
                PlaceEntry.TABLE_NAME+"(" +PlaceEntry._ID+") , "+
                "FOREIGN KEY ("+SubCategoryByPlaceEntry.COLUMN_SUBCATEGORY_KEY+") REFERENCES "+
                SubCategoryEntry.TABLE_NAME+"("+SubCategoryEntry._ID+") );";
        final String CREATE_SUB_CATEGORY_BY_TUTORIAL = "CREATE TABLE "+ SubCategoryByTutorialEntry.TABLE_NAME +"("+
                SubCategoryByTutorialEntry._ID + " INTEGER PRIMARY KEY , "+
                SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY+" INTEGER NOT NULL,"+
                SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY+" INTEGER NOT NULL, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+ SubCategoryByTutorialEntry._ID+" ) ON CONFLICT REPLACE, "+
                "FOREIGN KEY (" + SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY+") REFERENCES "+
                TutorialEntry.TABLE_NAME+"("+TutorialEntry._ID+"), "+
                "FOREIGN KEY (" +SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY+") REFERENCES "+
                SubCategoryEntry.TABLE_NAME+"("+SubCategoryEntry._ID+"));";

        final String CREATE_USER_BY_PLACE_TABLE = "CREATE TABLE " + UserByPlaceEntry.TABLE_NAME + "(" +
                UserByPlaceEntry._ID + " INTEGER PRIMARY KEY , " +
                UserByPlaceEntry.COLUMN_USER_KEY +" INTEGER NOT NULL, " +
                UserByPlaceEntry.COLUMN_PLACE_KEY + " INTEGER NOT NULL, " +
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+UserByPlaceEntry._ID+ " ) ON CONFLICT REPLACE, "+
                "FOREIGN KEY (" + UserByPlaceEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + "(" + UserEntry._ID+"), " +
                "FOREIGN KEY (" + UserByPlaceEntry.COLUMN_PLACE_KEY +") REFERENCES " +
                PlaceEntry.TABLE_NAME + "(" + PlaceEntry._ID+") );";
        final String CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + "(" +
                EventEntry._ID + " INTEGER PRIMARY KEY , "+
                EventEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                EventEntry.COLUMN_DESCRIPTION + " TEXT, "+
                EventEntry.COLUMN_ADDRESS +" TEXT NOT NULL, "+
                EventEntry.COLUMN_CREATOR+ " TEXT NOT NULL, "+
                EventEntry.COLUMN_DATE + " INTEGER NOT NULL, "+
                EventEntry.COLUMN_IMAGE + " BLOB , "+
                EventEntry.COLUMN_LAT + " REAL NOT NULL, "+
                EventEntry.COLUMN_LOG +" REAL NOT NULL , " +
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( " + EventEntry._ID+" ) ON CONFLICT REPLACE );";

        final String CREATE_EVENT_BY_USER_TABLE = "CREATE TABLE " + EventByUserEntry.TABLE_NAME+ "(" +
                EventByUserEntry._ID + " INTEGER PRIMARY KEY , "+
                EventByUserEntry.COLUMN_KEY_EVENT +" INTEGER NOT NULL,  "+
                EventByUserEntry.COLUMN_KEY_USER + " TEXT NOT NULL, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0 , "+
                "UNIQUE ( "+EventByUserEntry._ID+" ) ON CONFLICT REPLACE, "+
                "FOREIGN KEY ("+ EventByUserEntry.COLUMN_KEY_USER +") REFERENCES " +
                UserEntry.TABLE_NAME +" ( "+ UserEntry.COLUMN_EMAIL +"), "+
                "FOREIGN KEY (" + EventByUserEntry.COLUMN_KEY_EVENT + ") REFERENCES "+
                EventEntry.TABLE_NAME + " (" + EventEntry._ID+") );";
        final String CREATE_STATISTICS_TABLE = "CREATE TABLE " +StatisticsEntry.TABLE_NAME + "(" +
                StatisticsEntry._ID + " INTEGER PRIMARY KEY , "+
                StatisticsEntry.COLUMN_DATE+ " INTEGER NOT NULL, "+
                RecappContract.COLUMN_IS_SEND + " INTEGER DEFAULT 0, "+
                StatisticsEntry.COLUMN_POINT + " INTEGER NOT NULL ," +
                StatisticsEntry.COLUMN_KEY_USER + " INTEGER NOT NULL, "+
                "FOREIGN KEY ( " + StatisticsEntry.COLUMN_KEY_USER+ " ) REFERENCES "+
                UserEntry.TABLE_NAME+ " ( " +UserEntry._ID + " ) );";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACE_TABLE);
        sqLiteDatabase.execSQL(CREATE_REMINDER_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACE_IMAGE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TUTORIAL_TABLE);
        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUB_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUB_CATEGORY_BY_PLACE);
        sqLiteDatabase.execSQL(CREATE_SUB_CATEGORY_BY_TUTORIAL);
        sqLiteDatabase.execSQL(CREATE_USER_BY_PLACE_TABLE);
        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_EVENT_BY_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_STATISTICS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceImageEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TutorialEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubCategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubCategoryByPlaceEntry.TABLE_NAME );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubCategoryByTutorialEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserByPlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventByUserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StatisticsEntry.TABLE_NAME );
        //onCreate(sqLiteDatabase);

    }


}
