package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Company;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

/**
 * Created by andresgutierrez on 11/24/15.
 */
public class CompanyPointsFragment extends Fragment {
    private static View root;
    private static CardView cardPoint;
    private static ImageView card;
    private static ImageView userImage;
    private static TextView userName;
    private static TextView userLastname;
    private static TextView userEmail;
    private static TextView userPoint;
    private static SeekBar points;
    private static TextView minPoints;
    private static TextView maxPoints;
    private static Button addPoints;
    private static int pointsValue;
    private static long userId;
    private static long placeId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_points_company,container,false);
        card = (ImageView) root.findViewById(R.id.company_point);
        cardPoint = (CardView) root.findViewById(R.id.company_point_card);
        userImage = (ImageView) root.findViewById(R.id.user_image);
        userName = (TextView) root.findViewById(R.id.user_name);
        userLastname = (TextView) root.findViewById(R.id.user_lastname);
        userEmail = (TextView) root.findViewById(R.id.user_email);
        userPoint = (TextView) root.findViewById(R.id.user_points);
        minPoints = (TextView) root.findViewById(R.id.point_min);
        maxPoints =  (TextView) root.findViewById(R.id.point_max);
        points = (SeekBar) root.findViewById(R.id.point_company);
        addPoints = (Button) root.findViewById(R.id.company_add_points);


        return root;
    }

    public void showPoint(User user){
        if(user!=null){
            userId = user.getId();
            card.setVisibility(View.GONE);
            cardPoint.setVisibility(View.VISIBLE);
            userImage.setImageBitmap(BitmapFactory.decodeByteArray(Utility.decodeImage(user.getProfileImage()), 0,
                    Utility.decodeImage(user.getProfileImage()).length));
            userName.setText(user.getName());
            userLastname.setText(user.getLastname());
            userEmail.setText(user.getEmail());
            userPoint.setText(user.getPoints()+"");
            addPoints.setVisibility(View.VISIBLE);
            addPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    com.unal.tuapp.recapp.backend.model.placeApi.model.Place pointsPlace = new com.unal.tuapp.recapp.backend.model.placeApi.model.Place();
                    pointsPlace.setId(placeId);
                    pointsPlace.setPoints((long) pointsValue);
                    Pair<Context,Pair<Place,String>> pair =
                            new Pair<>(getContext(),new Pair<>(pointsPlace,"removePoints"));
                    new PlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);

                    com.unal.tuapp.recapp.backend.model.userApi.model.User userPoint = new com.unal.tuapp.recapp.backend.model.userApi.model.User();
                    userPoint.setId(userId);
                    userPoint.setPoints((long) pointsValue);
                    Pair<Pair<Context,String>,Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User,String>> pairUser =
                            new Pair<>(new Pair<>(getContext(),"nothing"),new Pair<>(userPoint,"addPointsUser"));
                    new UserEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUser);
                    hideCompanyPoint();
                    Toast.makeText(getContext(),"Put again the card to check your points",Toast.LENGTH_LONG).show();

                }
            });
        }
    }
    public void hideCompanyPoint(){
        points.setVisibility(View.GONE);
        maxPoints.setVisibility(View.GONE);
        minPoints.setVisibility(View.GONE);
        addPoints.setVisibility(View.GONE);
    }
    public void showCompanyPoint(Place place){
        if(place!=null){
            placeId = place.getId();
            addPoints.setVisibility(View.VISIBLE);
            minPoints.setText("min Points: 0");
            maxPoints.setText("max Points:" +place.getPoints()+"");
            maxPoints.setVisibility(View.VISIBLE);
            minPoints.setVisibility(View.VISIBLE);
            points.setVisibility(View.VISIBLE);
            points.setMax(Integer.parseInt("" + place.getPoints()));
            points.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    pointsValue = seekBar.getProgress();
                    Toast.makeText(getActivity(),""+seekBar.getProgress(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}

