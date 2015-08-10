package com.unal.tuapp.recapp.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andresgutierrez on 8/5/15.
 */
public class User implements Parcelable {
    private long id;
    private String email;
    private double log;
    private double lat;
    private String name;
    private String lastName;
    private byte[] profileImage;
    private Cursor cursor;

    public User() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(lastName);
        parcel.writeDouble(lat);
        parcel.writeDouble(log);
        parcel.writeInt(profileImage.length);
        parcel.writeByteArray(profileImage);
    }

    private User(Parcel parcel) {
        id = parcel.readLong();
        email = parcel.readString();
        name = parcel.readString();
        lastName = parcel.readString();
        lat = parcel.readDouble();
        log = parcel.readDouble();
        int size = parcel.readInt();
        profileImage = new byte[size];
        parcel.readByteArray(profileImage);

    }

    public static final Creator<User> CREATOR
            = new Creator<User>(){
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };
}
