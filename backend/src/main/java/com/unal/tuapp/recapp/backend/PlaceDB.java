package com.unal.tuapp.recapp.backend;

import java.awt.Image;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by yeisondavid on 30/09/2015.
 */
public class PlaceDB implements Comparable<PlaceDB> {
    long id;
    private static long nextId = 0;
    public static long getnextID() { return nextId++; }
    String name;
    String description;
    String address;
    double lat;
    double log;
    double rating;
    byte[] imageFavorite;
    String web;
    Map<Long, CommentDB> comments;
    public PlaceDB(String address, String description, long id,
                 double log, double lat, String name, double rating,byte[] imageFavorite,String web) {
        comments = new HashMap<Long, CommentDB>();
        this.address = address;
        this.description = description;
        this.id = id;
        this.imageFavorite = imageFavorite;
        this.log = log;
        this.lat = lat;
        this.name = name;
        this.rating = rating;
        this.web = web;
    }

    @Override
    public int compareTo(PlaceDB arg) {
        return new Long(id).compareTo(arg.id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
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

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Map<Long, CommentDB> getComments() {
        return comments;
    }

    public void setComments(Map<Long, CommentDB> comments) {
        this.comments = comments;
    }
}
