package com.unal.tuapp.recapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.unal.tuapp.recapp.R;

/**
 * Created by AndresGutierrez on 31/01/2016.
 */
public class Help extends AppCompatActivity {

    private View root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_help,null);
        setContentView(root);

    }

}
