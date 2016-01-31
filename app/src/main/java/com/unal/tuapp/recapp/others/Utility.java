package com.unal.tuapp.recapp.others;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.unal.tuapp.recapp.activities.Company;
import com.unal.tuapp.recapp.activities.UserDetail;
import com.unal.tuapp.recapp.backend.model.categoryApi.CategoryApi;
import com.unal.tuapp.recapp.backend.model.commentApi.CommentApi;
import com.unal.tuapp.recapp.backend.model.eventApi.EventApi;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.EventByUserApi;
import com.unal.tuapp.recapp.backend.model.placeApi.PlaceApi;
import com.unal.tuapp.recapp.backend.model.placeImageApi.PlaceImageApi;
import com.unal.tuapp.recapp.backend.model.registrationApi.RegistrationApi;
import com.unal.tuapp.recapp.backend.model.reminderApi.ReminderApi;
import com.unal.tuapp.recapp.backend.model.statisticsApi.StatisticsApi;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.SubCategoryApi;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.SubCategoryByPlaceApi;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.SubCategoryByTutorialApi;
import com.unal.tuapp.recapp.backend.model.tutorialApi.TutorialApi;
import com.unal.tuapp.recapp.backend.model.userApi.UserApi;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.UserByPlaceApi;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by andresgutierrez on 8/11/15.
 */
public class Utility {
    private static UserApi userApi = null;
    private static PlaceApi placeApi = null;
    private static PlaceImageApi placeImageApi = null;
    private static CommentApi commentApi = null;
    private static CategoryApi categoryApi = null;
    private static SubCategoryApi subCategoryApi = null;
    private static TutorialApi tutorialApi = null;
    private static ReminderApi reminderApi = null;
    private static UserByPlaceApi userByPlaceApi = null;
    private static SubCategoryByPlaceApi subCategoryByPlaceApi =  null;
    private static SubCategoryByTutorialApi subCategoryByTutorialApi = null;
    private static EventApi eventApi = null;
    private static EventByUserApi eventByUserApi = null;
    private static RegistrationApi registrationApi = null;
    private static StatisticsApi statisticsApi =  null;
    private static String points = "";
    private static  ProgressDialog progressDialog;
    private static final String TAG = Utility.class.getSimpleName();
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
    public static boolean isWifiAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return  activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
    public static boolean isMobileAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return  activeNetworkInfo.getType() ==  ConnectivityManager.TYPE_MOBILE;
    }
    public static void addUser(Context context,String email,String name,String lastname) throws ExecutionException, InterruptedException {
        Cursor userCursor = context.getContentResolver().query(
                RecappContract.UserEntry.buildUserEmail(email),
                null,
                null,
                null,
                null
        );
        if(!userCursor.moveToFirst()){ //New User so we can add him/her
            User user = new User();
            user.setId(System.currentTimeMillis());
            user.setLastname(lastname);
            user.setName(name);
            user.setEmail(email);
            user.setPoints(0L);

            Pair<Pair<Context,String>,Pair<User,String>> pair = new Pair<>(new Pair<>(context,email),new Pair<>(user,"getUser"));
            String answer = new UserEndPoint().execute(pair).get();
            if(answer.equals("nothing")){
                Pair<Pair<Context,String>,Pair<User,String>> newPair = new Pair<>(new Pair<>(context,email),new Pair<>(user,"addUser"));
                new UserEndPoint().execute(newPair);
                ContentValues values = new ContentValues();
                values.put(RecappContract.UserEntry.COLUMN_EMAIL,user.getEmail());
                values.put(RecappContract.UserEntry.COLUMN_USER_NAME, user.getName());
                values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME, user.getLastname());
                values.put(RecappContract.UserEntry._ID, user.getId());
                context.getContentResolver().insert(RecappContract.UserEntry.CONTENT_URI, values);
            }


        }else{//There is a user but we should update the values
            User user = new User();
            user.setId(userCursor.getLong(userCursor.getColumnIndexOrThrow(RecappContract.UserEntry._ID)));
            user.setEmail(email);
            user.setProfileImage(encodeImage(userCursor.getBlob(userCursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE))));
            user.setName(userCursor.getString(userCursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME)));
            user.setLastname(userCursor.getString(userCursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_LASTNAME)));
            ContentValues values = new ContentValues();
            if(!name.equals("")) {
                user.setName(name);
                values.put(RecappContract.UserEntry.COLUMN_USER_NAME, name);
            }
            if(!lastname.equals("")) {
                user.setLastname(lastname);
                values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME, lastname);
            }
            values.put(RecappContract.COLUMN_IS_SEND,0);
            if(values.size()>0) {
                context.getContentResolver().update(RecappContract.UserEntry.CONTENT_URI, values,
                        RecappContract.UserEntry.COLUMN_EMAIL + "= ?",
                        new String[]{email});
            }
            Pair<Pair<Context,String>,Pair<User,String>> pair = new Pair<>(new Pair<>(context,email),new Pair<>(user,"updateUser"));

            new UserEndPoint().execute(pair);
        }
        userCursor.close();
    }

    public static UserApi getUserApi(){
        if(userApi==null){
            synchronized (Utility.class){
                if(userApi==null){
                    UserApi.Builder builder = new UserApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");

                    userApi = builder.build();
                }
            }
        }
        return userApi;
    }
    public static PlaceApi getPlaceApi(){
        if(placeApi==null){
            synchronized (Utility.class){
                if(placeApi==null){
                    PlaceApi.Builder builder = new PlaceApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");

                    placeApi = builder.build();
                }
            }
        }
        return placeApi;
    }
    public static PlaceImageApi getPlaceImageApi(){
        if(placeImageApi==null){
            synchronized (Utility.class){
                if(placeImageApi==null){
                    PlaceImageApi.Builder builder = new PlaceImageApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");

                    placeImageApi = builder.build();
                }
            }
        }
        return placeImageApi;
    }
    public static CommentApi getCommentApi(){
        if(commentApi==null){
            synchronized (Utility.class){
                if(commentApi==null){
                    CommentApi.Builder builder = new CommentApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");

                    commentApi = builder.build();
                }
            }
        }
        return commentApi;
    }
    public static CategoryApi getCategoryApi(){
        if(categoryApi==null){
            synchronized (Utility.class){
                if(categoryApi==null) {
                    CategoryApi.Builder builder = new CategoryApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    categoryApi = builder.build();
                }
            }
        }
        return categoryApi;
    }

    public static SubCategoryApi getSubCategoryApi(){
        if(subCategoryApi==null){
            synchronized (Utility.class){
                if(subCategoryApi==null) {
                    SubCategoryApi.Builder builder = new SubCategoryApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    subCategoryApi = builder.build();
                }
            }
        }
        return subCategoryApi;
    }
    public static TutorialApi getTutorialApi(){
        if(tutorialApi==null){
            synchronized (Utility.class){
                if(tutorialApi==null) {
                    TutorialApi.Builder builder = new TutorialApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    tutorialApi = builder.build();
                }
            }
        }
        return tutorialApi;
    }
    public static ReminderApi getReminderApi(){
        if(reminderApi==null){
            synchronized (Utility.class){
                if(reminderApi==null) {
                    ReminderApi.Builder builder = new ReminderApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    reminderApi = builder.build();
                }
            }
        }
        return reminderApi;
    }
    public static UserByPlaceApi getUserByPlaceApi(){
        if(userByPlaceApi==null){
            synchronized (Utility.class){
                if(userByPlaceApi==null){
                    UserByPlaceApi.Builder builder = new UserByPlaceApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    userByPlaceApi = builder.build();
                }
            }
        }
        return userByPlaceApi;
    }
    public static SubCategoryByPlaceApi getSubCategoryByPlaceApi(){
        if(subCategoryByPlaceApi==null){
            synchronized (Utility.class){
                if(subCategoryByPlaceApi==null){
                    SubCategoryByPlaceApi.Builder builder = new SubCategoryByPlaceApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    subCategoryByPlaceApi = builder.build();
                }
            }
        }
        return subCategoryByPlaceApi;
    }
    public static SubCategoryByTutorialApi getSubCategoryByTutorialApi(){
        if(subCategoryByTutorialApi==null){
            synchronized (Utility.class){
                if(subCategoryByTutorialApi==null){
                    SubCategoryByTutorialApi.Builder builder = new SubCategoryByTutorialApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    subCategoryByTutorialApi = builder.build();
                }
            }
        }
        return subCategoryByTutorialApi;
    }

    public static EventApi getEventApi(){
        if(eventApi==null){
            synchronized (Utility.class){
                if(eventApi==null){
                    EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                            null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    eventApi = builder.build();
                }
            }
        }
        return eventApi;
    }
    public static EventByUserApi getEventByUserApi(){
        if(eventByUserApi==null){
            synchronized (Utility.class){
                if(eventByUserApi==null){
                    EventByUserApi.Builder builder = new EventByUserApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    eventByUserApi = builder.build();
                }
            }
        }
        return eventByUserApi;
    }
    public static RegistrationApi getRegistrationApi(){
        if(registrationApi == null){
            synchronized (Utility.class){
                if(registrationApi==null){
                    RegistrationApi.Builder builder = new RegistrationApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    registrationApi = builder.build();
                }
            }
        }
        return registrationApi;
    }
    public static StatisticsApi getStatisticsApi(){
        if(statisticsApi==null){
            synchronized (Utility.class){
                if(statisticsApi==null){
                    StatisticsApi.Builder builder = new StatisticsApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),null)
                            .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
                    statisticsApi = builder.build();
                }
            }
        }
        return statisticsApi;
    }



    public static String encodeImage(byte[] image){
        return new String(Base64.encodeBase64String(image));
    }
    public static byte[] decodeImage(String image){
        return Base64.decodeBase64(image);
    }

    public static RecyclerView.LayoutManager getLayoutManager(FragmentActivity fragmentActivity,int width){
        if(width >= 600 && width<900) {
            return new GridLayoutManager(fragmentActivity, 2);
        }else if(width>900){
            return new GridLayoutManager(fragmentActivity,3);
        }
        else {
            return new LinearLayoutManager(fragmentActivity);
        }

    }

    public static Long[] getStatisticsWeek(){
        Long time [] = new Long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMinimum(Calendar.MINUTE));
        time[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMaximum(Calendar.MINUTE));
        time[1] = calendar.getTimeInMillis();
        return time;
    }
    public static Long[] getStatisticsMonth(){
        Long time [] = new Long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE,1);
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMinimum(Calendar.MINUTE));
        time[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMaximum(Calendar.MINUTE));
        time[1] = calendar.getTimeInMillis();
        return time;
    }
    public static Long[] getStatisticsYear(){
        Long time [] = new Long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMinimum(Calendar.MINUTE));
        time[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY,calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE,calendar.getActualMaximum(Calendar.MINUTE));
        time[1] = calendar.getTimeInMillis();
        return time;
    }

}
