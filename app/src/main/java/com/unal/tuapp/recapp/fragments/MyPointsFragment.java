package com.unal.tuapp.recapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;

/**
 * Created by andresgutierrez on 11/23/15.
 */
public class MyPointsFragment extends Fragment {
    private static View root;
    private static ImageView card;
    private static TextView point;
    private static TextView myPoints;
    private static CardView cardPoints;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_my_points,container,false);
        card = (ImageView) root.findViewById(R.id.card);
        point = (TextView) root.findViewById(R.id.points);
        myPoints = (TextView) root.findViewById(R.id.my_points);
        cardPoints = (CardView) root.findViewById(R.id.card_points);
        return root;
    }

    public void showPoint(User userPoint){

        if(userPoint!=null) {
            myPoints.setVisibility(View.VISIBLE);
            point.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
            myPoints.setText(userPoint.getPoints()+"");
            cardPoints.setVisibility(View.VISIBLE);
        }else{
            myPoints.setVisibility(View.GONE);
            point.setVisibility(View.GONE);
            cardPoints.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
        }
    }
}
