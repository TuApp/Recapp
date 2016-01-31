package com.unal.tuapp.recapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.fragments.SettingsFragment;

/**
 * Created by AndresGutierrez on 31/01/2016.
 */
public class Settings extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingsFragment())
                .commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentRecapp = new Intent(this,Recapp.class);
        startActivity(intentRecapp);
    }
}
