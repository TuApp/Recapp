package com.unal.tuapp.recapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class PlacesFragment extends Fragment {
    private RecyclerView recyclerView;
    private View root;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_places,container,false);

        recyclerView = (RecyclerView) root.findViewById(R.id.places_recycle_view);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        recyclerView.setHasFixedSize(true);

        String [] placesArray ={"Bogota","Medellin","Cali","Pereira","San Andres",
                "Cartagena","Tolima","Guajira","Boyaca","Choco","Cundinamarca","Huila",
        "Magdalena","Quindio","Santander","Norte de Santander","Sucre"};
        List<String> places = new ArrayList<>(Arrays.asList(placesArray));

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        RecyclePlaceAdapter recyclePlaceAdapter = new RecyclePlaceAdapter(places);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclePlaceAdapter);
        alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(alphaAdapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_item),true,true));

        return root;
    }
}