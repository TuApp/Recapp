package com.unal.tuapp.recapp;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
                        GoogleApiClient.ConnectionCallbacks,LocationListener,LoaderManager.LoaderCallbacks<Cursor> {
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private static View view;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    public static onMapListener mOnMapListener;
    float scale;
    private Location currentLocation;
    private long UPDATE_INTERVAL = 30000;
    private long UPDATE_FAST_INTERVAL = 5000;
    private Marker me;
    private LocationRequest locationRequest;
    private User user;
    private BitmapDescriptor icon=null;
    private int PLACE=100;
    private Cursor placeCursor;
    private Map<Marker,Place> markers;
    private Marker toDistace;
    private Button calculateDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scale = getActivity().getResources().getDisplayMetrics().density;
        buildGoogleApiClient();
        if(savedInstanceState!=null){
            user = savedInstanceState.getParcelable("user");
        }
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent !=null ){
                parent.removeView(view);
            }
        }
        try{
            view = inflater.inflate(R.layout.fragment_map,container,false);
            calculateDistance = (Button) view.findViewById(R.id.the_way);
            calculateDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(toDistace!=null){
                        //We should create an implicit intent to show the way
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+toDistace.getPosition().latitude+","+
                        toDistace.getPosition().longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if(mapIntent.resolveActivity(getActivity().getPackageManager())!=null){
                            startActivity(mapIntent);
                        }
                    }
                }
            });
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
                    if(!marker.equals(me)) {
                        mOnMapListener.onMap(markers.get(marker));
                    }
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (!marker.equals(me)) {
                        calculateDistance.setVisibility(View.VISIBLE);
                        toDistace = marker;
                    }
                    return false;
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    toDistace = null;
                    calculateDistance.setVisibility(View.GONE);
                }
            });
            map.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));


        }
        if(getLoaderManager().getLoader(PLACE)==null){
            getLoaderManager().initLoader(PLACE,null,this);
        }else{
            getLoaderManager().restartLoader(PLACE,null,this);
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
                map.setPadding(0, 0, 0, (int) (dp * scale + 0.5f) + (int) appBarLayout.getY());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    @Override
    public void onPause(){
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //googleMap.getUiSettings().setCompassEnabled(true);
        int dp = 60;
        if(map==null){
            map = googleMap;
        }
        map.setPadding(0, 0, 0, (int) (dp * scale + 0.5f) + (int) appBarLayout.getY());

    }

    public interface onMapListener{
        void onMap(Place place);
    }
    public void setOnMapListener(final onMapListener mOnMapListener){
        this.mOnMapListener = mOnMapListener;
    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(user!= null){
            Bitmap iconImage = BitmapFactory.decodeByteArray(user.getProfileImage(),0,user.getProfileImage().length);
            Bitmap iconImageScaled = Bitmap.createScaledBitmap(iconImage,70,70,true);
            icon = BitmapDescriptorFactory.fromBitmap(
                   iconImageScaled
            );
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if(me==null) {

            me = map.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("Me")
                    .icon(icon)
                    .flat(true));
        }else{
            me.setPosition(myLocation);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(16)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),3000,null);

        startLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if(user!= null){
            Bitmap iconImage = BitmapFactory.decodeByteArray(user.getProfileImage(),0,user.getProfileImage().length);
            Bitmap iconImageScaled = Bitmap.createScaledBitmap(iconImage,70,70,true);
            icon = BitmapDescriptorFactory.fromBitmap(
                    iconImageScaled
            );
        }
        me.setIcon(icon);
        //me.remove();
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        /*float zoom = map.getCameraPosition().zoom;
        if(zoom<10){
            zoom = 10f;
        }*/
        me.setPosition(myLocation);
        //Should we animate the camera?
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(zoom)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

    }

    protected void startLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(UPDATE_FAST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }
    public void setUser(User user){
        this.user = user;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("user",user);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Place> allPlaces = Place.allPlaces(data);
        placeCursor = data;
        addPlaces(allPlaces);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        placeCursor.close();
    }

    public void addPlaces(List<Place> places){
        if(markers!=null) {
            for (Marker marker : markers.keySet()) {
                marker.remove();
            }
        }
        markers = new HashMap<>();
        for (Place place:places) {
            markers.put(map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLat(), place.getLog())))
                    ,place);
        }

    }

    private class InfoWindowAdapterMarker implements GoogleMap.InfoWindowAdapter{
        private Context context;
        public InfoWindowAdapterMarker(Context context){
            this.context=context;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if(marker.equals(me)){
                return null;
            }
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popup = layoutInflater.inflate(R.layout.info_window_adapter, null);
            Place place = markers.get(marker);
            ImageView imageView = (ImageView)popup.findViewById(R.id.place_image);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                    place.getImageFavorite(), 0, place.getImageFavorite().length
            ));
            TextView name = (TextView) popup.findViewById(R.id.place_name);
            name.setText(place.getName());
            TextView address = (TextView) popup.findViewById(R.id.place_address);
            address.setText(place.getAddress());
            TextView rating = (TextView) popup.findViewById(R.id.place_rating);
            rating.setText("Rating: "+place.getRating());

            return popup;

        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}
