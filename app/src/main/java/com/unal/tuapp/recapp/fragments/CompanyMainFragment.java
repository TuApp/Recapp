package com.unal.tuapp.recapp.fragments;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.RecappContract;

/**
 * Created by andresgutierrez on 11/4/15.
 */
public class CompanyMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View root;
    private ImageView companyImage;
    private TextView companyName;
    private TextView companyDescription;
    private TextView companyAddress;
    private TextView companyWeb;
    private RatingBar companyRating;
    private final int PLACE = 7845;
    private String email;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main_company,container,false);
        companyImage = (ImageView) root.findViewById(R.id.image_company);
        companyName = (TextView) root.findViewById(R.id.name_company);
        companyDescription = (TextView) root.findViewById(R.id.description_company);
        companyAddress = (TextView) root.findViewById(R.id.address_company);
        companyWeb = (TextView) root.findViewById(R.id.web_company);
        companyRating = (RatingBar) root.findViewById(R.id.rating_company);
        if(getActivity().getIntent().getExtras()!=null){
            email = getActivity().getIntent().getExtras().getString("email");
        }

        if(getLoaderManager().getLoader(PLACE)==null){
            getLoaderManager().initLoader(PLACE,null,this);
        }else{
            getLoaderManager().restartLoader(PLACE,null,this);
        }
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.PlaceEntry.buildPlaceEmailUri(email),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            companyImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)),
                            0, data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)).length)
            );
            companyName.setText(
                    data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_NAME))
            );
            companyDescription.setText(
                    data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_DESCRIPTION))
            );
            companyAddress.setText(
                    data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_ADDRESS))
            );
            companyWeb.setText(
                    data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_WEB))
            );
            companyRating.setRating((float)data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_RATING)));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
