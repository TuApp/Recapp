package com.unal.tuapp.recapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.unal.tuapp.recapp.data.RecappContract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andresgutierrez on 7/11/15.
 */
public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
    private de.hdodenhof.circleimageview.CircleImageView imageView;
    private View view;
    private String email = null;

    public LoadProfileImage(View view,de.hdodenhof.circleimageview.CircleImageView imageView) {
        this.view = view;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap imageTemp = null;
        try {
            InputStream in = new java.net.URL(strings[0]).openStream();
            imageTemp = BitmapFactory.decodeStream(in);
        }catch (Exception e){}
        if(strings.length>1){
            email = strings[1];
        }
        return imageTemp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //Snackbar.make(view,"image",Snackbar.LENGTH_SHORT).show();
        if(email!=null){
            ContentValues values = new ContentValues();
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            values.put(RecappContract.UserEntry.COLUMN_USER_IMAGE,stream.toByteArray());
            view.getContext().getContentResolver().update(
                    RecappContract.UserEntry.CONTENT_URI,
                    values,
                    RecappContract.UserEntry.COLUMN_EMAIL + " = ? ",
                    new String[]{email}
            );
        }
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
