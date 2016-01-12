package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 12/29/15.
 */
public class Statistics {
    private Long points;
    private String name;
    private String lastName;
    private String email;
    private byte[] image;

    public Statistics(String name,String lastName,String email, byte[] image,Long points) {
        this.email = email;
        this.image = image;
        this.lastName = lastName;
        this.name = name;
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public static ArrayList<Statistics> allStatistics(Cursor cursor){
        ArrayList<Statistics> statistics= new ArrayList<>();
        while (cursor.moveToNext()){
            Statistics statistic = new Statistics(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getBlob(3),
                    cursor.getLong(4)
            );
            statistics.add(statistic);

        }
        return statistics;
    }
}
