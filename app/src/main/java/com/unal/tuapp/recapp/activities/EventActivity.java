package com.unal.tuapp.recapp.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
 * Created by andresgutierrez on 9/28/15.
 */
public class EventActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private View root;
    private User user;
    private EventDialog dialog;
    private String email;
    private long id;

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

        dialog = new EventDialog();
        dialog.setOnEventListener(new EventDialog.OnEventListener() {
            @Override
            public void onAction(String action, Object... objects) {

                if (action.equals("save")) {

                    //We should save the event
                    Event event = new Event();
                    event.setId(System.currentTimeMillis());
                    event.setName((String) objects[0]);
                    event.setDescription((String) objects[1]);
                    event.setStartDate(((Date) objects[2]).getTime());
                    event.setCreator(email);
                    event.setAddress((String) objects[3]);
                    event.setLat(Float.parseFloat("" + objects[4]));
                    event.setLng(Float.parseFloat(""+objects[5]));


                    ContentValues values = new ContentValues();
                    values.put(RecappContract.EventEntry.COLUMN_NAME, event.getName());
                    values.put(RecappContract.EventEntry.COLUMN_DESCRIPTION, event.getDescription());
                    values.put(RecappContract.EventEntry.COLUMN_DATE, event.getStartDate());
                    values.put(RecappContract.EventEntry.COLUMN_CREATOR, event.getCreator());
                    values.put(RecappContract.EventEntry.COLUMN_ADDRESS, event.getAddress());
                    values.put(RecappContract.EventEntry.COLUMN_LAT, event.getLat());
                    values.put(RecappContract.EventEntry.COLUMN_LOG, event.getLng());
                    values.put(RecappContract.EventEntry._ID, event.getId());
                    Bitmap image = (Bitmap) objects[6];
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    event.setImage(Utility.encodeImage(stream.toByteArray()));
                    values.put(RecappContract.EventEntry.COLUMN_IMAGE, stream.toByteArray());
                    Uri uri = getContentResolver().insert(
                            RecappContract.EventEntry.CONTENT_URI,
                            values
                    );
                    if(Utility.isNetworkAvailable(EventActivity.this)) {
                        Pair<Context, Pair<Event, String>> pairEvent = new Pair<>(getApplicationContext(), new Pair<>(event, "addEvent"));
                        new EventEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairEvent);
                    }
                    if (user != null) {
                            //The user who creates the event also should attend to it
                        EventByUser eventByUser = new EventByUser();
                        eventByUser.setId(System.currentTimeMillis());
                        eventByUser.setEmail(user.getEmail());
                        eventByUser.setEventId(RecappContract.EventEntry.getIdFromUri(uri));
                        ContentValues values1 = new ContentValues();
                        values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER, eventByUser.getEmail());
                        values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, eventByUser.getEventId());
                        values1.put(RecappContract.EventByUserEntry._ID, eventByUser.getId());
                        getContentResolver().insert(
                                RecappContract.EventByUserEntry.CONTENT_URI,
                                values1
                        );
                        if(Utility.isNetworkAvailable(EventActivity.this)) {
                            Pair<Context, Pair<EventByUser, String>> pairEventByUser = new Pair<>(getApplicationContext(),
                                    new Pair<>(eventByUser, "addEventByUser"));
                            new EventByUserEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairEventByUser);
                        }
                    }
                }
                if (user != null) {
                    Intent intent = new Intent(EventActivity.this, NavigationDrawer.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(EventActivity.this, Company.class);
                    intent.putExtra("email", email);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }});

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.event_container, dialog)
                    .commit();

        }


    }
