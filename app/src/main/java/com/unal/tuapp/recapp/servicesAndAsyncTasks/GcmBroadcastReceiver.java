package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unal.tuapp.recapp.activities.Recapp;

/**
 * Created by andresgutierrez on 11/19/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        //ComponentName comp = new ComponentName(context.getPackageName(),
        //  GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        //startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
        GoogleCloudMessaging gcm =
                GoogleCloudMessaging.getInstance(context);
        // Get the type of GCM message
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
            //Log.e("algo",intent.getExtras().getString("message"));
            Bundle extras = new Bundle();
            extras.putString("message", intent.getExtras().getString("message"));

            ContentResolver.requestSync(Recapp.mAccount, Recapp.AUTHORITY,extras);
        }

    }
}
