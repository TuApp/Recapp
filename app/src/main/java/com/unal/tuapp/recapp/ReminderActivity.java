package com.unal.tuapp.recapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.io.ByteArrayOutputStream;
import java.util.Date;


/**
 * Created by andresgutierrez on 8/18/15.
 */
public class ReminderActivity extends AppCompatActivity implements ReminderDialog.OnDialogListener{
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
            long notificationId = System.currentTimeMillis();
            ContentValues valuesReminder = new ContentValues();
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_END_DATE, ((Date) values[3]).getTime());
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_NAME, (String) values[0]);
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION, (String) values[1]);
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
                valuesReminder.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION,-1);
            }
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,user.getId());
            valuesReminder.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY,this.id);
            Uri uri =getContentResolver().insert(
                    RecappContract.ReminderEntry.CONTENT_URI,
                    valuesReminder
            );
            //The user who creates the event also should attend to it
            ContentValues values1 = new ContentValues();
            values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER,user.getId());
            values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, RecappContract.EventEntry.getIdFromUri(uri));
            getContentResolver().insert(
                    RecappContract.EventByUserEntry.CONTENT_URI,
                    values1
            );


        }
        Intent intent = new Intent(ReminderActivity.this, Detail.class);
        intent.putExtra("id", id);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}
