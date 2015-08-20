package com.unal.tuapp.recapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by andresgutierrez on 8/20/15.
 */
public class MyAlarmReceiver extends BroadcastReceiver{
    private String title;
    private String body;
    private long notificationId;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras!=null){
            title = extras.getString("title");
            body =  extras.getString("body");
            notificationId = extras.getLong("notification");
            Intent newIntent = new Intent(context,Recapp.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000,1000,1000,1000})
                    .setLights(Color.WHITE,3000,3000)
                    .setSound(alarmSound);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int)notificationId,mBuilder.build());
        }
    }
}
