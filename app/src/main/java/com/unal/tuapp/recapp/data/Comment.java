package com.unal.tuapp.recapp.data;

/**
 * Created by andresgutierrez on 8/5/15.
 */

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;


public class Comment  implements Parcelable {
    private byte[] imageProfile;
    private String comment;
    private double rating;
    private long id;
    private long date;
    private long idPlace;
    private long idUser;

    public Comment(){}

    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id=id;
    }
    public long getIdPlace(){
        return idPlace;
    }
    public void setIdPlace(long id){
        this.idPlace=id;
    }

    public long getDate(){
        return date;
    }
    public void setDate(long date){
        this.date = date;
    }



    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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
        parcel.writeDouble(rating);
        parcel.writeLong(id);
        parcel.writeLong(date);
        parcel.writeLong(idPlace);
    }
    private Comment (Parcel parcel){
        int size = parcel.readInt();
        imageProfile = new byte[size];
        parcel.readByteArray(this.imageProfile);
        comment = parcel.readString();
        rating = parcel.readFloat();
        id = parcel.readLong();
        date = parcel.readLong();
        idPlace = parcel.readLong();

    }
    public static final Creator<Comment> CREATOR
            = new Creator<Comment>(){
        @Override
        public Comment createFromParcel(Parcel parcel) {
            return new Comment(parcel);
        }

        @Override
        public Comment[] newArray(int i) {
            return new Comment[i];
        }
    };
    public static ArrayList<Comment> allComment(Cursor cursor){
        ArrayList<Comment> comments = new ArrayList<>();
        while (cursor.moveToNext()){
            Comment comment = new Comment();
            comment.setId(cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry._ID)));
            comment.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_DATE)));
            comment.setComment(cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_DESCRIPTION)));
            comment.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_RATING)));
            comment.setImageProfile(cursor.getBlob(
                    cursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE)
            ));
            comment.setIdPlace(cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_PLACE_KEY)));
            comment.setIdUser(cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.CommentEntry.COLUMN_USER_KEY)));

            comments.add(comment);
        }
        return comments;
    }
}

