package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import com.unal.tuapp.recapp.data.RecappContract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


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
