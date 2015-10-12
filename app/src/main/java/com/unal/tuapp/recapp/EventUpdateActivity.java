package com.unal.tuapp.recapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by andresgutierrez on 10/12/15.
 */
public class EventUpdateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private View root;
    private User user;
    private long eventId;
    private EventDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_event,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = getIntent().getExtras().getParcelable("user");
        eventId = getIntent().getExtras().getLong("event");

        dialog = new EventDialog();
        dialog.setOnEventListener(new EventDialog.OnEventListener() {
            @Override
            public void onAction(String action, Object... objects) {
                if(action.equals("save")){
                    Log.e("algo",(String)objects[0]);
                    //We should save the event
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.EventEntry.COLUMN_NAME,(String)objects[0]);
                    values.put(RecappContract.EventEntry.COLUMN_DESCRIPTION,(String)objects[1]);
                    values.put(RecappContract.EventEntry.COLUMN_DATE,((Date)objects[2]).getTime());
                    values.put(RecappContract.EventEntry.COLUMN_CREATOR,user.getEmail());
                    values.put(RecappContract.EventEntry.COLUMN_ADDRESS,(String)objects[3]);
                    values.put(RecappContract.EventEntry.COLUMN_LAT, (Double) objects[4]);
                    values.put(RecappContract.EventEntry.COLUMN_LOG,(Double)objects[5]);
                    Bitmap image = (Bitmap)objects[6];
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    values.put(RecappContract.EventEntry.COLUMN_IMAGE, stream.toByteArray());

                    getContentResolver().update(
                            RecappContract.EventEntry.CONTENT_URI,
                            values,
                            RecappContract.EventEntry._ID +" = ?",
                            new String[]{""+eventId}
                    );
                }else if(action.equals("delete")){

                    getContentResolver().delete(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            RecappContract.EventByUserEntry.COLUMN_KEY_EVENT + " = ?",
                            new String[]{"" + eventId}
                    );
                    getContentResolver().delete(
                            RecappContract.EventEntry.CONTENT_URI,
                            RecappContract.EventEntry._ID + " = ?",
                            new String[]{"" + eventId}
                    );
                }
                Intent  intent = new Intent(EventUpdateActivity.this,UserDetail.class);
                intent.putExtra("user",user);
                intent.putExtra("type","event");
                startActivity(intent);
            }
        });
        getSupportFragmentManager().beginTransaction().
                replace(R.id.event_container, dialog)
                .commit();

    }




}