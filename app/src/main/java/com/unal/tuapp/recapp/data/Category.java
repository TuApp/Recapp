package com.unal.tuapp.recapp.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by andresgutierrez on 8/29/15.
 */
public class Category {
    private String name;
    private long id;
    private byte [] image;

    public Category(long id,String name,byte[] image){
        this.name=name;
        this.id = id;
        this.image = image;
    }

    public void setId(long id){
        this.id=id;
    }
    public long getId(){
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setImage(byte[] image){
        this.image = image;
    }
    public byte[] getImage(){
        return image;
    }

    public static ArrayList<Category> allCategories(Cursor cursor){
        ArrayList<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()){
            categories.add(new Category(
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.CategoryEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.CategoryEntry.COLUMN_NAME)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(RecappContract.CategoryEntry.COLUMN_IMAGE))
            ));
        }
        return categories;
    }

    @Override
    public String toString() {
        return name;
    }
}
