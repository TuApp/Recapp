package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.unal.tuapp.recapp.syncAdapter.Authenticator;

/**
 * Created by andresgutierrez on 12/26/15.
 */
public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
