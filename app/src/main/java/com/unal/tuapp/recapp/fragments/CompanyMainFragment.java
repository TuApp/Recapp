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
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;

/**
 * Created by andresgutierrez on 11/4/15.
 */
public class CompanyMainFragment extends Fragment {
    private View root;
    private ImageView companyImage;
    private TextView companyName;
    private TextView companyDescription;
    private TextView companyAddress;
    private TextView companyWeb;
    private RatingBar companyRating;
    private final int PLACE = 7845;
    private Place place;
    //private String email;

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

        return root;
    }
    public void setPlace(Place place){
        this.place = place;
        companyAddress.setText(place.getAddress());
        companyWeb.setText(place.getWeb());
        companyDescription.setText(place.getDescription());
        companyRating.setRating((float) place.getRating());
        companyName.setText(place.getName());
        companyImage.setImageBitmap(
                BitmapFactory.decodeByteArray(place.getImageFavorite(),0,place.getImageFavorite().length)
        );
    }

}
