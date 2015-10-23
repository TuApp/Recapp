package com.unal.tuapp.recapp.others;

import android.content.Context;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


/**
 * Created by andresgutierrez on 7/10/15.
 */
//Singleton
public class GooglePlus {

    private Context context;
    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;

    public static GoogleApiClient mGoogleApiClient;
    public static GooglePlus instance = null;

    public static final int RC_SIGN_IN = 0;
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_SIGN_IN = 1;
    public static final int STATE_IN_PROGRESS = 2;
    public static final int PROFILE_PIC_SIZE = 600;



    private GooglePlus(Context context, GoogleApiClient.ConnectionCallbacks connection, GoogleApiClient.OnConnectionFailedListener onConnection) {
        this.context = context;
        this.connectionCallbacks = connection;
        this. onConnectionFailedListener = onConnection;
        build();


    }

    private void  build(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    public static GooglePlus getInstance(Context context, GoogleApiClient.ConnectionCallbacks connection, GoogleApiClient.OnConnectionFailedListener onConnection) {
        if (instance == null) {
            synchronized (GooglePlus.class) {
                if (instance == null) {
                    instance = new GooglePlus(context, connection, onConnection);
                }
            }
        }
        return instance;
    }

}


