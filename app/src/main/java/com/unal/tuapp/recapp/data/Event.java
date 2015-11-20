package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 10/11/15.
 */
public class Event {
    private long id;
    private String name;
    private String description;
    private String address;
    private long date;
    private String creator;
    private Double log;
    private Double lng;
    private byte[] image;

    public Event(String address, String creator, long date, String description, long id, byte[] image, Double log, Double lng, String name) {
        this.address = address;
        this.creator = creator;
        this.date = date;
        this.description = description;
        this.id = id;
        this.image = image;
        this.log = log;
        this.lng = lng;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static final ArrayList<Event> allEvents(Cursor cursor){
        ArrayList<Event> events = new ArrayList<>();
        while (cursor.moveToNext()){
            Event event = new Event(
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_CREATOR)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.EventEntry._ID)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_IMAGE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LOG)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LAT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_NAME))
            );
            events.add(event);
        }
        return events;
    }
}
