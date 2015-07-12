package com.unal.tuapp.recapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andresgutierrez on 7/11/15.
 */
public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
    private de.hdodenhof.circleimageview.CircleImageView imageView;
    private View view;

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
        return imageTemp;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //Snackbar.make(view,"image",Snackbar.LENGTH_SHORT).show();
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
