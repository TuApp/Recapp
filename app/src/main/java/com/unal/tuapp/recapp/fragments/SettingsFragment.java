package com.unal.tuapp.recapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;

/**
 * Created by AndresGutierrez on 31/01/2016.
 */
public  class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        CheckBoxPreference network = (CheckBoxPreference)  findPreference(getString(R.string.network_key));
        if(network.isChecked()){
            network.setSummary(R.string.network_summary_off);
        }else{
            network.setSummary(R.string.network_summary_on);
        }
        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.time_key));
        listPreference.setSummary(listPreference.getEntry());

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.network_key))){
            CheckBoxPreference network = (CheckBoxPreference)  findPreference(key);
            if(network.isChecked()){
                network.setSummary(R.string.network_summary_off);
            }else{
                network.setSummary(R.string.network_summary_on);
            }
        }else if(key.equals(getString(R.string.time_key))){
            ListPreference listPreference = (ListPreference) findPreference(key);
            listPreference.setSummary(listPreference.getEntry());

        }
    }
}
