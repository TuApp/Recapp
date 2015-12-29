package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.unal.tuapp.recapp.syncAdapter.SyncAdapter;

/**
 * Created by andresgutierrez on 12/26/15.
 */
public class SyncService extends Service {
    private static SyncAdapter syncAdapter = null;
    private static final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock){
            if(syncAdapter==null){
                syncAdapter = new SyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
