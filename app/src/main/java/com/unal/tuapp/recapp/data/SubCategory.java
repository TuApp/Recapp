package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by andresgutierrez on 8/29/15.
 */
public class SubCategory {
    private long id;
    private String name;
    private byte[] image;

    public SubCategory(long id, String name,byte[] image) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
    public static ArrayList<SubCategory> allSubCategories(Cursor cursor){
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        while (cursor.moveToNext()){
            subCategories.add(new SubCategory(
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.SubCategoryEntry.TABLE_NAME + "." + RecappContract.SubCategoryEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.SubCategoryEntry.TABLE_NAME + "." + RecappContract.SubCategoryEntry.COLUMN_NAME)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.CategoryEntry.TABLE_NAME + "." + RecappContract.CategoryEntry.COLUMN_IMAGE))
            ));
        }
        return subCategories;
    }

    @Override
    public String toString() {
        return  name;
    }
}
