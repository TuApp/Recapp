package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;

/**
 * Created by andresgutierrez on 11/18/15.
 */
public class GcmRegistrationEndPoint extends AsyncTask<Void,Void,String> {
    private Context context;
    private GoogleCloudMessaging gcm;
    private static final String SENDER_ID = "937748015566";

    public GcmRegistrationEndPoint(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String msg = "";
        try {
            if(gcm == null){
                gcm = GoogleCloudMessaging.getInstance(context);

            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;
            Utility.getRegistrationApi().register(regId).execute();

        }catch (IOException e){

        }
        return msg;
    }

    /*@Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }*/
}
