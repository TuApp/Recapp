package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 8/6/15.
 */
public class PlaceImages {
    private long id;
    private byte image[];
    public PlaceImages(long id,byte[] image){
        this.id = id;
        this.image = image;
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

    public static ArrayList<byte[]> allImages(Cursor cursor){
        ArrayList<byte[]> placeImages = new ArrayList<>();
        while (cursor.moveToNext()){
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.PlaceImageEntry.COLUMN_IMAGE));
            placeImages.add(image);
        }
        return placeImages;
    }
    public static ArrayList<PlaceImages> allImagesPlaces(Cursor cursor){
        ArrayList<PlaceImages> placeImages = new ArrayList<>();
        while (cursor.moveToNext()){
            placeImages.add(new PlaceImages(
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.PlaceImageEntry.TABLE_NAME+"."+RecappContract.PlaceImageEntry._ID)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.PlaceImageEntry.COLUMN_IMAGE))));
        }
        return placeImages;
    }
}
