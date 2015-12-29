package com.unal.tuapp.recapp.fragments;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.view.ViewPager;


import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.GeofenceTransitionsIntentService;
import com.unal.tuapp.recapp.others.MyClusterRender;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
                        GoogleApiClient.ConnectionCallbacks,LocationListener,
                        ClusterManager.OnClusterClickListener<Place>,
                        ClusterManager.OnClusterInfoWindowClickListener<Place>,
                        ClusterManager.OnClusterItemClickListener<Place>,
                        ClusterManager.OnClusterItemInfoWindowClickListener<Place>{

    private static GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private static View view;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    public static onMapListener mOnMapListener;
    float scale;
    private Location currentLocation;
    private long UPDATE_INTERVAL = 30000;
    private long UPDATE_FAST_INTERVAL = 2000;
    private Marker me;
    private LocationRequest locationRequest;
    private User user;
    private BitmapDescriptor icon=null;
    private Cursor placeCursor;
    private ImageButton calculateDistance;
    private ImageButton myPosition;
    private static ClusterManager<Place> placeClusterManager;
    private Cluster<Place> clickedCluster;
    private Place clickedPlace;
    private static ArrayList<Geofence> mGeofenceList;
    private static String [] places;
    private static Intent intent;
    private static Context context;


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
        mGeofenceList = new ArrayList<>();

        try{
            view = inflater.inflate(R.layout.fragment_map,container,false);
            calculateDistance = (ImageButton) view.findViewById(R.id.the_way);
            myPosition = (ImageButton) view.findViewById(R.id.my_position);
            myPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (me != null) {
                        LatLng myLocation = new LatLng(me.getPosition().latitude,
                                me.getPosition().longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(myLocation)
                                .zoom(16)
                                .build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
                    }
                }
            });
            calculateDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickedPlace!=null){
                        //We should create an implicit intent to show the way
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+clickedPlace.getLat()+","+
                        clickedPlace.getLog());
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
            placeClusterManager = new ClusterManager<>(getActivity(),map);
            placeClusterManager.setRenderer(new MyClusterRender(getActivity(), map, placeClusterManager));
            map.setOnCameraChangeListener(placeClusterManager);
            map.setOnInfoWindowClickListener(placeClusterManager);
            map.setInfoWindowAdapter(placeClusterManager.getMarkerManager());

            placeClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));
            placeClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(null);
            map.setOnMarkerClickListener(placeClusterManager);
            placeClusterManager.setOnClusterClickListener(this);
            placeClusterManager.setOnClusterItemClickListener(this);
            placeClusterManager.setOnClusterInfoWindowClickListener(this);
            placeClusterManager.setOnClusterItemInfoWindowClickListener(this);

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    clickedPlace = null;
                    calculateDistance.setVisibility(View.GONE);
                }
            });
            //map.setInfoWindowAdapter(new InfoWindowAdapterMarker(getActivity()));


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
            if(user.getProfileImage()!=null) {
                Bitmap iconImage = BitmapFactory.decodeByteArray(user.getProfileImage(), 0, user.getProfileImage().length);
                Bitmap iconImageScaled = Bitmap.createScaledBitmap(iconImage, 80, 80, true);
                icon = BitmapDescriptorFactory.fromBitmap(
                        iconImageScaled
                );
            }
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(currentLocation!=null) {
            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            if (me == null) {

                me = map.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .title("Me"));
                if (icon != null) {
                    me.setIcon(icon);
                }
            } else {
                me.setPosition(myLocation);
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(16)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
        }
        if(mGeofenceList.size()>0) {

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeoFencingRequest(),
                    getGeofencingIntent()
            );
        }


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
        //me.remove();
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (me == null) {

            me = map.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("Me"));
        } else {
            me.setPosition(myLocation);
        }
        if(user!= null){
            if(user.getProfileImage()!=null) {
                Bitmap iconImage = BitmapFactory.decodeByteArray(user.getProfileImage(), 0, user.getProfileImage().length);
                Bitmap iconImageScaled = Bitmap.createScaledBitmap(iconImage, 80, 80, true);
                icon = BitmapDescriptorFactory.fromBitmap(
                        iconImageScaled
                );
                if(me!=null) {
                    me.setIcon(icon);
                }
            }
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(16)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
        //Should we animate the camera?


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
        outState.putParcelable("user", user);
    }

    public void addPlaces(List<Place> places){
        placeClusterManager.clearItems();
        if(mGeofenceList.size()>0 && mGoogleApiClient.isConnected()){
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencingIntent()
            );
        }
        mGeofenceList.clear();
        this.places = new String[places.size()];
        int i = 0;
        for (Place place:places) {
            this.places[i] = ""+place.getId()+"\n"+place.getName();
            placeClusterManager.addItem(place);
            mGeofenceList.add(new Geofence.Builder()
                            .setRequestId("" + place.getId())
                            .setCircularRegion(
                                    place.getLat(),
                                    place.getLog(),
                                    100
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
            );
            i++;

        }
        if(mGeofenceList.size()>0 && mGoogleApiClient.isConnected()){
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeoFencingRequest(),
                    getGeofencingIntent()
            );
        }

    }
    private GeofencingRequest getGeoFencingRequest(){
        GeofencingRequest.Builder geoFencing = new GeofencingRequest.Builder();
        geoFencing.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        geoFencing.addGeofences(mGeofenceList);
        return geoFencing.build();
    }
    private PendingIntent getGeofencingIntent(){
        if(intent==null){
            intent = new Intent(getActivity(),GeofenceTransitionsIntentService.class);
            context = getActivity();
        }
        intent.putExtra("places",places);
        return PendingIntent.getService(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private class InfoWindowAdapterMarker implements GoogleMap.InfoWindowAdapter{
        private Context context;
        private View popup;
        public InfoWindowAdapterMarker(Context context){
            this.context=context;
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.popup = layoutInflater.inflate(R.layout.info_window_adapter, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            if(marker.equals(me)){
                return null;
            }

            //Place place = markers.get(marker);
            if(clickedPlace!=null) {
                ImageView imageView = (ImageView) popup.findViewById(R.id.place_image);
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                        clickedPlace.getImageFavorite(), 0, clickedPlace.getImageFavorite().length
                ));
                TextView name = (TextView) popup.findViewById(R.id.place_name);
                name.setText(clickedPlace.getName());
                TextView address = (TextView) popup.findViewById(R.id.place_address);
                address.setText(clickedPlace.getAddress());
                TextView rating = (TextView) popup.findViewById(R.id.place_rating);
                rating.setText("Rating: " + clickedPlace.getRating());
            }

            return popup;

        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Place> cluster) {
        clickedCluster = cluster;
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Place> cluster) {
        //We don't do anything
    }

    @Override
    public boolean onClusterItemClick(Place place) {
        clickedPlace = place;
        calculateDistance.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Place place) {
        if(mOnMapListener!=null){
            mOnMapListener.onMap(place);
        }
    }
    public void setDate(List<Place> places,Cursor cursor){
        addPlaces(places);
        placeCursor = cursor;
        placeClusterManager.cluster();
    }
    public void closeData(){
        if(placeCursor!=null){
            placeCursor.close();
        }
    }


}
