package com.unal.tuapp.recapp.BackendConnection;


import com.unal.tuapp.recapp.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by yeisondavid on 20/09/2015.
 */
public class getAPI {
    static MyApi myApiService = null;
    public static MyApi getMyAPI()
    {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://tonal-vector-100303.appspot.com/_ah/api/");
            // end options for devappserver





            myApiService = builder.build();
        }
        return myApiService;
    }
}
