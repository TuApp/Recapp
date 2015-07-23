package com.unal.tuapp.recapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andresgutierrez on 7/22/15.
 */
public class Comment  implements Parcelable {
    private byte[] imageProfile;
    private String comment;
    private float rating;

    public Comment(){}


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public byte[] getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(byte[] imageProfile) {
        this.imageProfile = imageProfile;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageProfile.length);
        parcel.writeByteArray(imageProfile);
        parcel.writeString(comment);
        parcel.writeFloat(rating);
    }
    private Comment (Parcel parcel){
        int size = parcel.readInt();
        imageProfile = new byte[size];
        parcel.readByteArray(this.imageProfile);
        comment = parcel.readString();
        rating = parcel.readFloat();

    }
    public static final Parcelable.Creator<Comment> CREATOR
            = new Parcelable.Creator<Comment>(){
        @Override
        public Comment createFromParcel(Parcel parcel) {
            return new Comment(parcel);
        }

        @Override
        public Comment[] newArray(int i) {
            return new Comment[i];
        }
    };
}
