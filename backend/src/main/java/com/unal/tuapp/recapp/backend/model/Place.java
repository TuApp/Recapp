package com.unal.tuapp.recapp.backend.model;



import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.awt.Image;

import javax.swing.ImageIcon;


@Entity
public class Place {
    @Id private Long id;
    private String name;
    private Float lat;
    private Float lng;
    private String description;
    private String address;
    private Float rating;
    private String imageFavorite;//This is base 64
    private String email;
    private String password;
    private String web;
    private Long points;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageFavorite() {
        return imageFavorite;
    }

    public void setImageFavorite(String imageFavorite) {
        this.imageFavorite = imageFavorite;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
