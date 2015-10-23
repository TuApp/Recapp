package com.unal.tuapp.recapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.unal.tuapp.recapp.dialogs.EventDialog;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_event,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = getIntent().getExtras().getParcelable("user");

        dialog = new EventDialog();
        dialog.setOnEventListener(new EventDialog.OnEventListener() {
            @Override
            public void onAction(String action, Object... objects) {
                if(action.equals("save")){
                    //We should save the event
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.EventEntry.COLUMN_NAME,(String)objects[0]);
                    values.put(RecappContract.EventEntry.COLUMN_DESCRIPTION,(String)objects[1]);
                    values.put(RecappContract.EventEntry.COLUMN_DATE,((Date)objects[2]).getTime());
                    values.put(RecappContract.EventEntry.COLUMN_CREATOR,user.getEmail());
                    values.put(RecappContract.EventEntry.COLUMN_ADDRESS,(String)objects[3]);
                    values.put(RecappContract.EventEntry.COLUMN_LAT,(Double)objects[4]);
                    values.put(RecappContract.EventEntry.COLUMN_LOG,(Double)objects[5]);
                    Bitmap image = (Bitmap)objects[6];
                    ByteArrayOutputStream  stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    values.put(RecappContract.EventEntry.COLUMN_IMAGE, stream.toByteArray());

                    Uri uri = getContentResolver().insert(
                            RecappContract.EventEntry.CONTENT_URI,
                            values
                    );

                    //The user who creates the event also should attend to it
                    ContentValues values1 = new ContentValues();
                    values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_USER, user.getEmail());
                    values1.put(RecappContract.EventByUserEntry.COLUMN_KEY_EVENT, RecappContract.EventEntry.getIdFromUri(uri));
                    getContentResolver().insert(
                            RecappContract.EventByUserEntry.CONTENT_URI,
                            values1
                    );
                }
                Intent intent = new Intent(EventActivity.this,NavigationDrawer.class);
                startActivity(intent);
            }
        });
        getSupportFragmentManager().beginTransaction().
                replace(R.id.event_container,dialog )
                .commit();

    }


}
