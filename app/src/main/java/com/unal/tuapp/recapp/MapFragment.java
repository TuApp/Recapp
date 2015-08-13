package com.unal.tuapp.recapp;


import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{
    private LocationManager handler;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private static View view;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    public static onMapListener mOnMapListener;
    float scale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scale = getActivity().getResources().getDisplayMetrics().density;
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent !=null ){
                parent.removeView(view);
            }
        }
        try{
            view = inflater.inflate(R.layout.fragment_map,container,false);
        }catch (InflateException e){}
        //We use this to know if the toolbar is out of the screen
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_navigation);

        FragmentManager fm = getChildFragmentManager();
        mapFragment= (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (map == null) {
            map = mapFragment.getMap();
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    mOnMapListener.onMap(marker);

                }
            });


        }
        viewPager = (ViewPager)getActivity().findViewById(R.id.view_pager);
        /*
        *We use this to know when a new tab is selected after that we get the position of the toolbar
        * and upgrade the position of zoom buttons
        */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int dp = 60;
                map.setPadding(0,0,0, (int)(dp *  scale + 0.5f) + (int)appBarLayout.getY());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        int dp = 60;
        map.setPadding(0,0,0, (int)(dp *  scale + 0.5f) + (int)appBarLayout.getY());

    }

    public interface onMapListener{
        void onMap(Marker marker);
    }
    public void setOnMapListener(final onMapListener mOnMapListener){
        this.mOnMapListener = mOnMapListener;
    }
}
