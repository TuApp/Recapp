package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by fabianlm17 on 15/10/15.
 */
public class FilterCategoriesDialogFragment extends DialogFragment{
    private ImageView mBatteries;
    private ImageView mTires;
    private ImageView mElectronic;

    public FilterCategoriesDialogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.categories_filter, container);
        mBatteries = (ImageView) view.findViewById(R.id.mivBatteries);
        mTires = (ImageView) view.findViewById(R.id.mivTires);
        mElectronic = (ImageView) view.findViewById(R.id.mivElectronic);
        mBatteries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                NavigationDrawer callingActivity = (NavigationDrawer) getActivity();
                callingActivity.showSubCategoriesDialog(1);
            }
        });
        mTires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                NavigationDrawer callingActivity = (NavigationDrawer) getActivity();
                callingActivity.showSubCategoriesDialog(2);
            }
        });
        mElectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                NavigationDrawer callingActivity = (NavigationDrawer) getActivity();
                callingActivity.showSubCategoriesDialog(3);
            }
        });
        getDialog().setTitle(R.string.category);
        return view;
    }

}
