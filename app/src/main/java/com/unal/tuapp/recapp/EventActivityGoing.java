package com.unal.tuapp.recapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

/**
 * Created by andresgutierrez on 10/11/15.
 */
public class EventActivityGoing extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private View root;
    private User user;
    private long idEvent;
    private static final int EVENT = 911;
    private static final int EVENT_BY_USER = 912;

    private ImageView image;
    private TextView name;
    private TextView description;
    private TextView date;
    private TextView address;
    private Switch mSwitch;
    private Button save;

    private boolean wasGoing;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_event_going,null);
        setContentView(root);
        wasGoing = false;
        Bundle extras = getIntent().getExtras();

        user = extras.getParcelable("user");
        idEvent = extras.getLong("event");


        image = (ImageView) root.findViewById(R.id.event_going_image);
        name = (TextView) root.findViewById(R.id.event_going_title);
        description = (TextView) root.findViewById(R.id.event_going_description);
        date = (TextView) root.findViewById(R.id.event_going_date);
        address = (TextView) root.findViewById(R.id.event_going_place);
        mSwitch = (Switch) root.findViewById(R.id.event_going_going);



        save = (Button) root.findViewById(R.id.event_going_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wasGoing && !mSwitch.isChecked()){
                    //We should eliminate the user from the table because now he/she doesn't want to go to the event
                    getContentResolver().delete(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            RecappContract.EventByUserEntry.COLUMN_KEY_USER +" = ? AND " + RecappContract.EventByUserEntry.COLUMN_KEY_EVENT+" = ?",
                            new String[]{user.getEmail(),""+idEvent}
                    );
                }else if(!wasGoing && mSwitch.isChecked()){
                    //We should add the user to the table because now he/she want to go to the event
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT,idEvent);
                    values.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER,user.getEmail());
                    getContentResolver().insert(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            values
                    );
                }
                Intent intent = new Intent(EventActivityGoing.this,NavigationDrawer.class);
                startActivity(intent);
            }
        });

        if(getSupportLoaderManager().getLoader(EVENT)==null){
            getSupportLoaderManager().initLoader(EVENT,null,this);
        }else{
            getSupportLoaderManager().restartLoader(EVENT,null,this);
        }
        if(getSupportLoaderManager().getLoader(EVENT_BY_USER)==null){
            getSupportLoaderManager().initLoader(EVENT_BY_USER,null,this);
        }else{
            getSupportLoaderManager().restartLoader(EVENT_BY_USER,null,this);
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == EVENT) {
            return new CursorLoader(
                    this,
                    RecappContract.EventEntry.buildEventUri(this.idEvent),
                    null,
                    null,
                    null,
                    null
            );
        }
        if(id == EVENT_BY_USER){
            return new CursorLoader(
                    this,
                    RecappContract.EventByUserEntry.CONTENT_URI,
                    null,
                    RecappContract.EventByUserEntry.COLUMN_KEY_USER + " = ? AND "+ RecappContract.EventByUserEntry.COLUMN_KEY_EVENT +" = ?",
                    new String[]{user.getEmail(),""+idEvent},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == EVENT){
            if(data.moveToFirst()){
                name.setText(data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_NAME)));
                description.setText(data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DESCRIPTION)));
                date.setText(Utility.getDate(data.getLong(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DATE))));
                address.setText(
                        data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_ADDRESS)) + "\n" +
                                "Lat: " + data.getDouble(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LAT)) +
                                " Lng: " + data.getDouble(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LOG))
                );
                byte [] img = data.getBlob(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_IMAGE));
                image.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));
            }
        }
        if(loader.getId()== EVENT_BY_USER){
            if(data.moveToFirst()){
                wasGoing = true;
                mSwitch.setChecked(true);
            }else{
                mSwitch.setChecked(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
