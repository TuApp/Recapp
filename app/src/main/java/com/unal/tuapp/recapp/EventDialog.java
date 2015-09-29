package com.unal.tuapp.recapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andresgutierrez on 9/28/15.
 */
public class EventDialog extends DialogFragment {
    private View root;
    private ImageView eventImage;
    private EditText eventName;
    private EditText eventDescription;
    private EditText eventDate;
    private EditText eventTime;
    private ImageView gallery;
    private ImageView camera;
    private String imagePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.event_dialog,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(
                android.R.drawable.ic_menu_close_clear_cancel
        );
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.newEvent);

        eventImage = (ImageView) root.findViewById(R.id.event_dialog_image);
        eventName = (EditText) root.findViewById(R.id.event_dialog_title);
        eventDescription = (EditText) root.findViewById(R.id.event_dialog_description);
        eventDate = (EditText) root.findViewById(R.id.event_dialog_date);
        eventTime = (EditText) root.findViewById(R.id.event_dialog_time);
        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialog = inflater.inflate(R.layout.custom_dialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setTitle(R.string.choose)
                        .setView(dialog);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                gallery = (ImageView) dialog.findViewById(R.id.gallery);
                camera = (ImageView) dialog.findViewById(R.id.camera);
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent choosePicture = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePicture.setType("image/*");
                        startActivityForResult(choosePicture,2);
                        alertDialog.dismiss();
                    }
                });
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File photo = null;
                        try {
                            photo = createImageFile();
                        }catch (Exception e){}
                        if(photo!=null) {
                            imagePath = photo.getAbsolutePath();
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                startActivityForResult(takePicture, 1);
                            }
                        }
                        alertDialog.dismiss();
                    }
                });


            }
        });
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reminder_dialog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home==item.getItemId()){
            Intent intent = new Intent(getActivity(),NavigationDrawer.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth(),eventImage.getHeight(),true);
            eventImage.setImageBitmap(bitmapScaled);
            eventImage.invalidate();
        }
        if(requestCode==2 && resultCode==Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), data.getData());
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth(),eventImage.getHeight(),true);
                eventImage.setImageBitmap(bitmapScaled);
            }catch (Exception e){

            }

        }

    }

    public File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFilename = "JPEG_"+timeStamp+"_";
        File storeDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        File image = File.createTempFile(
                imageFilename,
                ".jpg",
                storeDir
        );
        return image;
    }
}
