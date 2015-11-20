package com.unal.tuapp.recapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.MyAlarmReceiver;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.dialogs.ReminderDialog;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.ReminderEndPoint;

import java.io.ByteArrayOutputStream;
import java.util.Date;


/**
 * Created by andresgutierrez on 8/18/15.
 */
public class ReminderActivity extends AppCompatActivity implements ReminderDialog.OnDialogListener {
    private long id;
    private User user;
    private View root;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_reminder,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reminder_container,new ReminderDialog(),"Dialog")
                .commit();

    }

    @Override
    public void onAction(String action,Object...values) {
        //We should save the new reminder and put it in the notification manager
        if(action.equals("save")) {
            Reminder reminder = new Reminder();
            reminder.setId(System.currentTimeMillis());
            reminder.setNotification(System.currentTimeMillis());
            reminder.setEndDate(((Date) values[3]).getTime());
            reminder.setName((String) values[0]);
            reminder.setDescription((String) values[1]);
            reminder.setUserId(user.getId());
            reminder.setPlaceId(this.id);
            long notificationId = reminder.getNotification();
            ContentValues valuesReminder = new ContentValues();
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_END_DATE, reminder.getEndDate());
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_NAME, reminder.getName());
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION, reminder.getDescription());
            //Log.e("algo",(String) values[1]);
            if((Boolean)values[2]){
                valuesReminder.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION, notificationId); //Unique id for the notification
                /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Title")

                        .setContentText("Body");
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify((int)notificationId,mBuilder.build());*/
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap bitmap = (Bitmap) values[6];
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                Intent intent = new Intent(this,MyAlarmReceiver.class);
                intent.putExtra("title",(String) values[0]);
                intent.putExtra("body",(String)values[1]);
                intent.putExtra("notification",notificationId);
                intent.putExtra("placeName",(String)values[4]);
                intent.putExtra("placeAddress",(String)values[5]);
                intent.putExtra("placeImage",out.toByteArray());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,(int)notificationId,intent,0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,((Date) values[3]).getTime(),pendingIntent);


            }else{
                valuesReminder.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION,reminder.getNotification());
            }
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,reminder.getUserId());
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY, reminder.getPlaceId());
            valuesReminder.put(RecappContract.ReminderEntry._ID,reminder.getId());
            getContentResolver().insert(
                    RecappContract.ReminderEntry.CONTENT_URI,
                    valuesReminder
            );
            Pair<Context,Pair<Reminder,String>> pair = new Pair<>(getApplicationContext(),new Pair<>(reminder,"addReminder"));
            new ReminderEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pair);



        }
        Intent intent = new Intent(ReminderActivity.this, Detail.class);
        intent.putExtra("id", id);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}
