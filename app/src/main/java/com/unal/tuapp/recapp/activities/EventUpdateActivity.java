package com.unal.tuapp.recapp.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.unal.tuapp.recapp.backend.model.eventApi.model.Event;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.EventByUser;
import com.unal.tuapp.recapp.dialogs.EventDialog;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.EventByUserEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.EventEndPoint;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by andresgutierrez on 10/12/15.
 */
public class EventUpdateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private View root;
    private User user;
    private String email;
    private long id;
    private long eventId;
    private EventDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_event,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent().getExtras().containsKey("user")) {
            user = getIntent().getExtras().getParcelable("user");
            email = user.getEmail();
        }else if(getIntent().getExtras().containsKey("email")){
            email = getIntent().getExtras().getString("email");
            id = getIntent().getExtras().getLong("id");
        }
        eventId = getIntent().getExtras().getLong("event");

        dialog = new EventDialog();
        dialog.setOnEventListener(new EventDialog.OnEventListener() {
            @Override
            public void onAction(String action, Object... objects) {
                if(action.equals("save")){
                    //We should save the event
                    Event event = new Event();
                    event.setId(eventId);
                    event.setName((String) objects[0]);
                    event.setDescription((String) objects[1]);
                    event.setStartDate(((Date) objects[2]).getTime());
                    event.setCreator(email);
                    event.setAddress((String) objects[3]);
                    event.setLat((Float) objects[4]);
                    event.setLng((Float)objects[5]);
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.EventEntry.COLUMN_NAME,event.getName());
                    values.put(RecappContract.EventEntry.COLUMN_DESCRIPTION,event.getDescription());
                    values.put(RecappContract.EventEntry.COLUMN_DATE,event.getStartDate());
                    values.put(RecappContract.EventEntry.COLUMN_CREATOR,event.getCreator());
                    values.put(RecappContract.EventEntry.COLUMN_ADDRESS,event.getAddress());
                    values.put(RecappContract.EventEntry.COLUMN_LAT, event.getLat());
                    values.put(RecappContract.EventEntry.COLUMN_LOG,event.getLng());
                    Bitmap image = (Bitmap)objects[6];
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    event.setImage(Utility.encodeImage(stream.toByteArray()));
                    values.put(RecappContract.EventEntry.COLUMN_IMAGE, stream.toByteArray());

                    getContentResolver().update(
                            RecappContract.EventEntry.CONTENT_URI,
                            values,
                            RecappContract.EventEntry._ID + " = ?",
                            new String[]{"" + eventId}
                    );
                    Pair<Context,Pair<Event,String>> pairUpdateEvent = new Pair<>(getApplicationContext(),new Pair<>(
                            event,"updateEvent"
                    ));
                    new EventEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairUpdateEvent);
                }else if(action.equals("delete")){

                    getContentResolver().delete(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            RecappContract.EventByUserEntry.COLUMN_KEY_EVENT + " = ?",
                            new String[]{"" + eventId}
                    );
                    EventByUser eventByUser = new EventByUser();
                    eventByUser.setEventId(eventId);
                    Pair<Context,Pair<EventByUser,String>> pairEvnetByUser = new Pair<>(getApplicationContext(), new Pair<>(eventByUser,
                            "deleteEventByUserEvent"));
                    new EventByUserEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairEvnetByUser);
                    getContentResolver().delete(
                            RecappContract.EventEntry.CONTENT_URI,
                            RecappContract.EventEntry._ID + " = ?",
                            new String[]{"" + eventId}
                    );
                    Event event = new Event();
                    event.setId(eventId);
                    Pair<Context,Pair<Event,String>> pairEvent = new Pair<>(getApplicationContext(), new Pair<>(
                            event,"deleteEvent"
                    ));
                    new EventEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairEvent);
                }
                if(user!=null) {
                    Intent intent = new Intent(EventUpdateActivity.this, UserDetail.class);
                    intent.putExtra("user", user);
                    intent.putExtra("type", "event");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(EventUpdateActivity.this, Company.class);
                    intent.putExtra("email", email);
                    intent.putExtra("id",id);
                    intent.putExtra("type", "event");
                    startActivity(intent);
                }
            }
        });
        getSupportFragmentManager().beginTransaction().
                replace(R.id.event_container, dialog)
                .commit();

    }




}
