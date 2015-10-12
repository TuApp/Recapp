package com.unal.tuapp.recapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by andresgutierrez on 10/9/15.
 */
public class MapDialog extends DialogFragment {
    private View root;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Marker place;
    private onPlaceChange onPlaceChange;
    private Double lat;
    private Double lng;

    public static MapDialog newInstance(LatLng lgLn){
        MapDialog mapDialog = new MapDialog();
        if(lgLn!=null) {
            Bundle args = new Bundle();
            args.putDouble("lat", lgLn.latitude);
            args.putDouble("lng", lgLn.longitude);
            mapDialog.setArguments(args);
        }

        return mapDialog;
    }
    public interface onPlaceChange{
        void onPlaceChange(LatLng place);
    }

    public MapDialog.onPlaceChange getOnPlaceChange() {
        return onPlaceChange;
    }

    public void setOnPlaceChange(MapDialog.onPlaceChange onPlaceChange) {
        this.onPlaceChange = onPlaceChange;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.map_dialog,container,false);
        //getDialog().setTitle(R.string.place);
        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.map_dialog_container, mapFragment);
        transaction.commit();
        if(getArguments()!=null) {
            lat = getArguments().getDouble("lat");
            lng = getArguments().getDouble("lng");
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                map = mapFragment.getMap();
                if(map!=null){
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.getUiSettings().setZoomControlsEnabled(true);
                    map.getUiSettings().setZoomGesturesEnabled(true);

                    if(getArguments()!=null){
                        place = map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(new LatLng(lat,lng))
                                .build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1500,null);

                    }

                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if(place!=null){//If place is different of null it's because there is a place so we should delete it and create the new place after that
                                place.remove();
                            }
                            place = map.addMarker(new MarkerOptions().position(latLng));
                            CameraPosition  cameraPosition = CameraPosition.builder()
                                    .target(latLng)
                                    .build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1500,null);
                            if(onPlaceChange!=null){
                                onPlaceChange.onPlaceChange(latLng);
                            }

                        }
                    });
                    handler.removeCallbacksAndMessages(null);

                }else{
                    handler.postDelayed(this,500);
                }
            }
        },500);

        return root;
    }
    public void changePlace(double lat,double lng){
        if(place!=null){
            place.remove();
        }
        if(map!=null) {
            place = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(new LatLng(lat, lng))
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1500, null);
        }
    }


}
