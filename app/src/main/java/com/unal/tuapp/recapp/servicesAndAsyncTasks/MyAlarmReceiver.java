package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Recapp;


/**
 * Created by andresgutierrez on 8/20/15.
 */
public class MyAlarmReceiver extends BroadcastReceiver{
    private String title;
    private String body;
    private String placeName;
    private String placeAddress;
    private byte[] placeImage;
    private long notificationId;



    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();


        if(extras!=null){
            title = extras.getString("title");
            body =  extras.getString("body");
            placeName = extras.getString("placeName");
            placeAddress = extras.getString("placeAddress");
            notificationId = extras.getLong("notification");
            placeImage = extras.getByteArray("placeImage");
            Intent newIntent = new Intent(context,Recapp.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(BitmapFactory.decodeByteArray(placeImage, 0, placeImage.length))
                    .setBigContentTitle("Place name: " + placeName)
                    .setSummaryText("Place address: " + placeAddress);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Event name: " + title)
                    .setContentText("Event description: " + body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle);
            Notification notification = mBuilder.build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int)notificationId,notification);
        }
    }

}
