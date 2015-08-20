package com.unal.tuapp.recapp.data;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 8/9/15.
 */
public class Reminder {
    private long id;
    private String name;
    private String description;
    private long notification;
    private long endDate;
    private Place place;

    public Reminder(String description, long endDate, long id, String name, Place place, long notification) {
        this.description = description;
        this.endDate = endDate;
        this.id = id;
        this.name = name;
        this.place = place;
        this.notification = notification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
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

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public long getNotificiation() {
        return notification;
    }

    public void setNotification(long notification) {
        this.notification = notification;
    }

    public static ArrayList<Reminder> allReminder(Cursor data){
        ArrayList<Reminder> reminders = new ArrayList<>();
        while (data.moveToNext()){
            Place place = new Place(data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME+"."+
                    RecappContract.PlaceEntry.COLUMN_ADDRESS)), data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME+"."+
                    RecappContract.PlaceEntry.COLUMN_DESCRIPTION)),data.getLong(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME + "." +
                    RecappContract.PlaceEntry._ID)),data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME + "." +
                    RecappContract.PlaceEntry.COLUMN_LOG)),data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME + "." +
                    RecappContract.PlaceEntry.COLUMN_LAT)),data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME + "." +
                    RecappContract.PlaceEntry.COLUMN_NAME)),data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME + "." +
                    RecappContract.PlaceEntry.COLUMN_RATING)),data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.TABLE_NAME+"."+
                    RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)));
            Reminder reminder= new Reminder(
                    data.getString(data.getColumnIndexOrThrow(RecappContract.ReminderEntry.TABLE_NAME+"."+
                            RecappContract.ReminderEntry.COLUMN_DESCRIPTION)),
                    data.getLong(data.getColumnIndexOrThrow(RecappContract.ReminderEntry.TABLE_NAME + "." +
                            RecappContract.ReminderEntry.COLUMN_END_DATE)),
                    data.getLong(data.getColumnIndexOrThrow(RecappContract.ReminderEntry.TABLE_NAME+"."+
                            RecappContract.ReminderEntry._ID)),
                    data.getString(data.getColumnIndexOrThrow(RecappContract.ReminderEntry.TABLE_NAME+"."+
                            RecappContract.ReminderEntry.COLUMN_NAME)),
                    place,
                    data.getLong(data.getColumnIndexOrThrow(RecappContract.ReminderEntry.TABLE_NAME+"."+
                            RecappContract.ReminderEntry.COLUMN_NOTIFICATION))
            );
            Log.e("algo",reminder.getDescription());
            reminders.add(reminder);
        }
        return reminders;
    }
}
