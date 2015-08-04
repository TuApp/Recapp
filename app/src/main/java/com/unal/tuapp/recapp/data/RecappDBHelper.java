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
    private SQLiteDatabase database;

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
    private String createTables(){
        final String CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ( "+
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL ," +
                UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_LASTNAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_IMAGE + " BLOB NOT NULL," +
                UserEntry.COLUMN_LAT + " REAL,"+
                UserEntry.COLUMN_LOG + " REAL );";
        final String CREATE_PLACE_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " ( "+
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_LAT + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_LOG + " REAL NOT NULL, " +
                PlaceEntry.COLUMN_ADDRESS + " TEXT UNIQUE NOT NULL, " +
                PlaceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL );";
        final String CREATE_REMINDER_TABLE = "CREATE TABLE "+ ReminderEntry.TABLE_NAME +" ( "+
                ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReminderEntry.COLUMN_START_DATE + " INTEGER NOT NULL, " +
                ReminderEntry.COLUMN_END_DATE + " INTEGER NOT NULL, "+
                ReminderEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                ReminderEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_USER_KEY +" ) REFERENCES " +
                UserEntry.TABLE_NAME + " ( "+ UserEntry._ID +" ), " +
                "FOREIGN KEY ( " + ReminderEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_COMMENT_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " ( "+
                CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CommentEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "+
                CommentEntry.COLUMN_RATING + " REAL NOT NULL," +
                "FOREIGN KEY ( " + CommentEntry.COLUMN_USER_KEY + " ) REFERENCES " +
                UserEntry.TABLE_NAME + " ( "+ UserEntry._ID +" )," +
                "FOREIGN KEY ( " + CommentEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_PLACE_IMAGE_TABLE = "CREATE TABLE "+ PlaceImageEntry.TABLE_NAME + " ( " +
                PlaceImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlaceImageEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                "FOREIGN KEY ( " + PlaceImageEntry.COLUMN_PLACE_KEY + " ) REFERENCES " +
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ) );";
        final String CREATE_TUTORIAL_TABLE = "CREATE TABLE " + TutorialEntry.TABLE_NAME + " ( " +
                TutorialEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TutorialEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TutorialEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL );";
        final String CREATE_TUTORIAL_IMAGE_TABLE = "CREATE TABLE " + TutorialImageEntry.TABLE_NAME + " ( " +
                TutorialImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TutorialImageEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                "FOREIGN KEY ( "+ TutorialImageEntry.COLUMN_TUTORIAL_KEY + " ) REFERENCES " +
                TutorialEntry.TABLE_NAME + " ( " + TutorialEntry._ID + " ) );";
        final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " ( " +
                CategoryEntry._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL );";
        final String CREATE_SUB_CATEGORY_TABLE = "CREATE TABLE " + SubCategoryEntry.TABLE_NAME + " (  " +
                SubCategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubCategoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                "FOREIGN_KEY ( " + SubCategoryEntry.COLUMN_PLACE_KEY + " )  REFERENCES "+
                PlaceEntry.TABLE_NAME + " ( " + PlaceEntry._ID + " ), " +
                "FOREIGN KEY ( " + SubCategoryEntry.COLUMN_CATEGORY_KEY + " ) REFERENCES " +
                CategoryEntry.TABLE_NAME + " ( " + CategoryEntry._ID + " ), " +
                "FOREIGN KEY ( " + SubCategoryEntry.COLUMN_TUTORIAL_KEY + " ) REFERENCES " +
                TutorialEntry.TABLE_NAME + " ( " + TutorialEntry._ID + " ) );";
        final String SQL = CREATE_USER_TABLE + "\n" + CREATE_PLACE_TABLE + "\n" +
                CREATE_REMINDER_TABLE + "\n" +  CREATE_COMMENT_TABLE + "\n" +
                CREATE_PLACE_IMAGE_TABLE + "\n" + CREATE_TUTORIAL_TABLE + "\n" +
                CREATE_TUTORIAL_IMAGE_TABLE + "\n" + CREATE_CATEGORY_TABLE + "\n" + CREATE_SUB_CATEGORY_TABLE;

        return SQL;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createTables());

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
        onCreate(sqLiteDatabase);

    }

    public Cursor getUserByEmail(String email){
        return database.query(
                UserEntry.TABLE_NAME,
                null,
                UserEntry.COLUMN_EMAIL+"=?",
                new String[]{email},
                null,
                null,
                null
        );
    }
    public Cursor getUserById(String id){
        return database.query(
                UserEntry.TABLE_NAME,
                null,
                UserEntry._ID + "=?",
                new String[]{id},
                null,
                null,
                null
        );

    }
    public Cursor getPlaceById(String id){
        return database.query(
                PlaceEntry.TABLE_NAME,
                null,
                PlaceEntry._ID + "=?",
                new String []{id},
                null,
                null,
                null
        );
    }
    public Cursor getCategoryById(String id){
        return database.query(
                CategoryEntry.TABLE_NAME,
                null,
                CategoryEntry._ID + "=?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getTutorialById(String id){
        return database.query(
                TutorialEntry.TABLE_NAME,
                null,
                TutorialEntry._ID +"=?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getSubCategoryById(String id){
        return database.query(
                SubCategoryEntry.TABLE_NAME,
                null,
                SubCategoryEntry._ID +"=?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getReminderById(String id){
        return database.query(
                ReminderEntry.TABLE_NAME,
                null,
                ReminderEntry._ID + "=?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getAllPlaces(String[] columns, String selection, String [] selectionArgs, String groupBy, String having ,
                               String orderBy, String limit){
        database = instance.getReadableDatabase();
        return database.query(
                PlaceEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
        );

    }
    public Cursor getAllCategories(String[] columns, String selection, String [] selectionArgs, String groupBy, String having ,
                                   String orderBy, String limit){
        database = instance.getReadableDatabase();
        return database.query(
                CategoryEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
        );
    }
    public Cursor getAllSubCategories(String[] columns, String selection, String [] selectionArgs, String groupBy, String having ,
                                      String orderBy, String limit){
        database = instance.getReadableDatabase();
        return database.query(
                SubCategoryEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
        );
    }
    public Cursor getCommentByUser(String idUser){
        final String myQuery = "SELECT * FROM " + CommentEntry.TABLE_NAME +" AS Comment JOIN " + UserEntry.TABLE_NAME + " AS User ON "
                + "Comment."+CommentEntry.COLUMN_USER_KEY+"=User."+UserEntry._ID +" WHERE User."+UserEntry._ID+"=?;";
        database = instance.getReadableDatabase();
        return database.rawQuery(myQuery, new String[]{idUser});

    }
    public Cursor getCommentByPlace(String idPlace){
        final String myQuery = "SELECT * FROM " + CommentEntry.TABLE_NAME +" AS Comment JOIN " + PlaceEntry.TABLE_NAME + " AS Place ON "
                + "Comment."+CommentEntry.COLUMN_USER_KEY+"=Place."+PlaceEntry._ID +" WHERE Place."+PlaceEntry._ID+"=?;";
        database = instance.getReadableDatabase();
        return database.rawQuery(myQuery, new String[]{idPlace});

    }
    public long addUser(String email, String name, String lastname, byte[] image,double lon,double lat){
        database = instance.getReadableDatabase();
        Cursor cursor = getUserByEmail(email);
        long insertId = -1;
        if(cursor==null){//This is a new user so we will create it in the database
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_EMAIL,email);
            values.put(UserEntry.COLUMN_USER_NAME,name);
            values.put(UserEntry.COLUMN_USER_LASTNAME,lastname);
            values.put(UserEntry.COLUMN_USER_IMAGE,image);
            values.put(UserEntry.COLUMN_LAT,lat);
            values.put(UserEntry.COLUMN_LOG, lon);
            insertId = database.insert(UserEntry.TABLE_NAME, null, values);
        }

        return insertId;

    }
    //The places are added for us so we don't check if this place exist
    public long addPlace(String name,Double lat,Double lon,String address,String description){
        database = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlaceEntry.COLUMN_NAME,name);
        values.put(PlaceEntry.COLUMN_LAT,lat);
        values.put(PlaceEntry.COLUMN_LOG,lon);
        values.put(PlaceEntry.COLUMN_ADDRESS,address);
        values.put(PlaceEntry.COLUMN_DESCRIPTION,description);

        long insertId = database.insert(PlaceEntry.TABLE_NAME,null,values);
        return insertId;
    }

    //First, we are going to check, if id for place is correct, I mean if it exist in the database
    public long addImagePlace(long idPlace,byte[] image){
        database =  instance.getReadableDatabase();

        Cursor cursor = getPlaceById(""+idPlace);

        long insertId = -1;
        if(cursor!=null) {//The id exist so we can add the image for that place
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PlaceImageEntry.COLUMN_IMAGE, image);
            values.put(PlaceImageEntry.COLUMN_PLACE_KEY, idPlace);
            insertId = database.insert(PlaceImageEntry.TABLE_NAME, null, values);
        }
        return insertId;
    }
    public long addCategory(String name){
        database = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME,name);
        long insertId = database.insert(CategoryEntry.TABLE_NAME,null,values);
        return insertId;
    }
    public long addTutorial(String name,String description){
        database = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TutorialEntry.COLUMN_NAME,name);
        values.put(TutorialEntry.COLUMN_DESCRIPTION,description);
        long insertId = database.insert(TutorialEntry.TABLE_NAME,null,values);

        return insertId;
    }
    public long addImageTutorial(long idTutorial,byte [] image){
        database = instance.getReadableDatabase();
        long insertId = -1;
        Cursor cursor = getTutorialById("" + idTutorial);
        if(cursor!=null){
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TutorialImageEntry.COLUMN_IMAGE,image);
            values.put(TutorialImageEntry.COLUMN_TUTORIAL_KEY,idTutorial);
            insertId = database.insert(TutorialImageEntry.TABLE_NAME,null,values);
        }
        return insertId;
    }
    public long addSubCategory(long idPlace,long idTutorial,long idCategory,String name){
        database = instance.getReadableDatabase();
        long insertId = -1 ;

        Cursor cursorPlace = getPlaceById(""+idPlace);
        Cursor cursorTutorial = getTutorialById("" + idTutorial);
        Cursor cursorCategory = getCategoryById("" + idCategory);
        if(cursorCategory!=null && cursorTutorial!=null && cursorPlace!=null){
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SubCategoryEntry.COLUMN_NAME,name);
            values.put(SubCategoryEntry.COLUMN_PLACE_KEY,idPlace);
            values.put(SubCategoryEntry.COLUMN_CATEGORY_KEY,idCategory);
            values.put(SubCategoryEntry.COLUMN_TUTORIAL_KEY,idTutorial);
            insertId = database.insert(SubCategoryEntry.TABLE_NAME,null,values);
        }

        return insertId;
    }
    public long addReminder(long idUser,long idPlace,long starDate,long endDate,String name,String description){
        database = instance.getReadableDatabase();
        Cursor cursorUser = getUserById("" + idUser);
        Cursor cursorPlace = getPlaceById("" + idPlace);
        long insertId = -1;
        if(cursorPlace!=null && cursorUser!=null){
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ReminderEntry.COLUMN_START_DATE,starDate);
            values.put(ReminderEntry.COLUMN_END_DATE,endDate);
            values.put(ReminderEntry.COLUMN_NAME,name);
            values.put(ReminderEntry.COLUMN_DESCRIPTION,description);
            values.put(ReminderEntry.COLUMN_USER_KEY,idUser);
            values.put(ReminderEntry.COLUMN_PLACE_KEY,idPlace);
            insertId = database.insert(ReminderEntry.TABLE_NAME,null,values);
        }
        return insertId;
    }
    public long addComment(long idUser,long idPlace, String description, double rating){
        database = instance.getReadableDatabase();
        Cursor cursorUser = getUserById("" + idUser);
        Cursor cursorPlace = getPlaceById("" + idPlace);
        long insertId = -1;
        if(cursorPlace!=null && cursorUser!=null) {
            database = instance.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CommentEntry.COLUMN_DESCRIPTION,description);
            values.put(CommentEntry.COLUMN_RATING,rating);
            values.put(CommentEntry.COLUMN_USER_KEY,idUser);
            values.put(CommentEntry.COLUMN_PLACE_KEY,idPlace);
            insertId = database.insert(CommentEntry.TABLE_NAME,null,values);
        }
        return insertId;
    }
}
