package com.unal.tuapp.recapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by andresgutierrez on 9/28/15.
 */
public class EventActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_event,null);
        setContentView(root);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.event_container,new EventDialog())
                .commit();

    }


}
