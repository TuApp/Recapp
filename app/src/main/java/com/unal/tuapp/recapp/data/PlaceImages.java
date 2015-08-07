package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 8/6/15.
 */
public class PlaceImages {
    private List<byte[]> images;
    public PlaceImages(){}

    public List<byte[]> getImages(){
        return images;
    }
    public void setImages(Cursor cursor){
        images = new ArrayList<>();
        while (cursor.moveToNext()){
            images.add(cursor.getBlob(cursor.getColumnIndexOrThrow(
                    RecappContract.PlaceImageEntry.COLUMN_IMAGE
            )));
        }

    }
}
