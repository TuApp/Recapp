package com.unal.tuapp.recapp.data;

import android.database.Cursor;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

/**
 * Created by andresgutierrez on 8/5/15.
 */
public class Place implements ClusterItem {

    private long id;
    private String name;
    private String description;
    private String address;
    private double lat;
    private double log;
    private double rating;
    private byte[] imageFavorite;


    public Place(String address, String description, long id,
                 double log, double lat, String name, double rating,byte[] imageFavorite) {
        this.address = address;
        this.description = description;
        this.id = id;
        this.imageFavorite = imageFavorite;
        this.log = log;
        this.lat = lat;
        this.name = name;
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public byte[] getImageFavorite() {
        return imageFavorite;
    }

    public void setImageFavorite(byte[] imageFavorite) {
        this.imageFavorite = imageFavorite;
    }

    public static ArrayList<Place> allPlaces(Cursor cursor){
        ArrayList<Place> places = new ArrayList<Place>();
        while (cursor.moveToNext()){
            places.add(new Place(cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry._ID)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LOG)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_RATING)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE))));
        }

        return places;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat,log);
    }
}
