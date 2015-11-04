package com.unal.tuapp.recapp.others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.unal.tuapp.recapp.data.RecappContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andresgutierrez on 8/11/15.
 */
public class Utility {
    public static String getDate(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }
    public static String getDateTime(long time){
        Date date = new Date(time);
        SimpleDateFormat  format = new SimpleDateFormat("EEE, MM d, yyyy - HH:mm");
        return format.format(date);
    }
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
    public static void addUser(Context context,String email,String name,String lastname){
        Cursor userCursor = context.getContentResolver().query(
                RecappContract.UserEntry.buildUserEmail(email),
                new String[]{RecappContract.UserEntry._ID},
                null,
                null,
                null
        );
        if(!userCursor.moveToFirst()){ //New User so we can add him/her
            ContentValues values = new ContentValues();
            values.put(RecappContract.UserEntry.COLUMN_EMAIL,email);
            values.put(RecappContract.UserEntry.COLUMN_USER_NAME,name);
            values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME,lastname);
            context.getContentResolver().insert(RecappContract.UserEntry.CONTENT_URI,values);

        }else{//There is a user but we should update the values
            ContentValues values = new ContentValues();
            if(!name.equals("")) {
                values.put(RecappContract.UserEntry.COLUMN_USER_NAME, name);
            }
            if(!lastname.equals("")) {
                values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME, lastname);
            }
            if(values.size()>0) {
                context.getContentResolver().update(RecappContract.UserEntry.CONTENT_URI, values,
                        RecappContract.UserEntry.COLUMN_EMAIL + "= ?",
                        new String[]{email});
            }
        }
        userCursor.close();
    }
}
