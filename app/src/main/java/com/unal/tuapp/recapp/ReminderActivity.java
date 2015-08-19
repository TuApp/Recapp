package com.unal.tuapp.recapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

/**
 * Created by andresgutierrez on 8/18/15.
 */
public class ReminderActivity extends AppCompatActivity implements ReminderDialog.OnDialogListener{
    private long id;
    private User user;
    private View root;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_reminder,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reminder_container,new ReminderDialog(),"Dialog")
                .commit();

    }

    @Override
    public void onAction(String action) {
        //We should save the new reminder and put it in the notification manager
        Intent intent = new Intent(ReminderActivity.this,Detail.class);
        intent.putExtra("id",id);
        intent.putExtra("user",user);
        startActivity(intent);
    }


}
