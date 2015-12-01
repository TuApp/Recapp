package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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
        HttpURLConnection urlConnection = null;
        Bitmap imageTemp = null;
        try {
            URL url =  new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            imageTemp = BitmapFactory.decodeStream(in);
        }catch (Exception e){}
        finally {
            urlConnection.disconnect();
        }
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
            if(stream!=null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    values.put(RecappContract.UserEntry.COLUMN_USER_IMAGE, stream.toByteArray());
                    Cursor cursor = view.getContext().getContentResolver().query(
                            RecappContract.UserEntry.buildUserEmail(email),
                            null,
                            null,
                            null,
                            null
                    );
                    if (cursor.moveToFirst()) {
                        User user = new User();
                        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(RecappContract.UserEntry._ID)));
                        user.setEmail(email);
                        user.setLastname(cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_LASTNAME)));
                        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME)));
                        user.setProfileImage(Utility.encodeImage(stream.toByteArray()));
                        //Log.e("algo1", ""+user.getId());
                        //Pair<Pair<Context,String>,Pair<User,String>> pair = new Pair<>(new Pair<>(view.getContext(),email),new Pair<>(user,"updateUser"));
                        //new UserEndPoint().execute(pair);
                    }
                    view.getContext().getContentResolver().update(
                            RecappContract.UserEntry.CONTENT_URI,
                            values,
                            RecappContract.UserEntry.COLUMN_EMAIL + " = ? ",
                            new String[]{email}
                    );
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }catch (Exception e){}

            }
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
