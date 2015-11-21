package com.unal.tuapp.recapp.data;

import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by fabianlm17 on 21/10/15.
 */
public class Tutorial {
    private String tittle;
    private String description;
    private String link;
    private String previewURL;
    private Bitmap preview;

    public Tutorial(String tittle, String description, String link) {
        this.tittle = tittle;
        this.description = description;
        this.link = link;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public static ArrayList<Tutorial> allTutorials(Cursor cursor){
        ArrayList<Tutorial> tutorials = new ArrayList<Tutorial>();
        while (cursor.moveToNext()){
            tutorials.add(new Tutorial(cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO))));
        }

        return tutorials;
    }

}