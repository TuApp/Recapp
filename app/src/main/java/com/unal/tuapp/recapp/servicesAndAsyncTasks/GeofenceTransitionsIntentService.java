package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Recapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 10/16/15.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private String [] places;
    private List<String> placesNotification;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeongEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        plafencices = intent.getStringArrayExtra("places");
        placesNotification = new ArrayList<>();
        int geofenceTransitions = geofencingEvent.getGeofenceTransition();
        if (geofenceTransitions == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransitions == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            boolean notification = getGeofenceTransitionDetails(
                    this,
                    geofenceTransitions,
                    triggeringGeofences
            );
            if(notification) {
                sendNotification();
            }
        }
    }
    private boolean getGeofenceTransitionDetails(Context contex,int geofenceTransitions,
                                                    List<Geofence> triggeringGeofences){
        ArrayList<String> ids = new ArrayList<>();

        for (Geofence geofence: triggeringGeofences){
            ids.add(geofence.getRequestId());
            for(int i=0; i<places.length; i++){
                String place[] = places[i].split("\n");
                if(place[0].equals(geofence.getRequestId())){
                    placesNotification.add(places[i]);
                }
            }
        }
        return placesNotification.size()>0;
    }
    private void sendNotification(){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Places to recycler near to you")
                .setContentText("Hey dude, there are some nice places near")
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(alarmSound);
        if(placesNotification.size()==1){
            mBuilder.setContentText(placesNotification.get(0).split("\n")[1].trim());
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Places :");
        for (String place: placesNotification){
            String placeValue [] = place.split("\n");
            inboxStyle.addLine(placeValue[1].trim());
        }
        mBuilder.setStyle(inboxStyle);
        Intent result = new Intent(this,Recapp.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Recapp.class);
        stackBuilder.addNextIntent(result);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, mBuilder.build());

    }

}
