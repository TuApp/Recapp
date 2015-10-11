package com.unal.tuapp.recapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private EditText eventPlace;
    private ImageView gallery;
    private ImageView camera;
    private String imagePath;
    private MapDialog mapDialog;
    private boolean isNewImage;
    private Bitmap image;
    private OnEventListener onEventListener;
    private LatLng latLng;
    private String name;
    private String description;
    private String date;
    private String time;
    private String place;
    private String address;
    private Double lat;
    private Double lng;


    public interface OnEventListener{
        void onAction(String action,Object...objects);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.event_dialog,container,false);
        isNewImage = false;
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
        eventPlace = (EditText) root.findViewById(R.id.event_dialog_place);
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
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day);
                        eventDate.setText(new SimpleDateFormat("EEE, MM d, yyyy ").format(calendar.getTime()));

                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        eventTime.setText(hour+":"+minute);
                    }
                },mHour,mMinute,true);
                timePickerDialog.show();
            }
        });
        eventPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapDialog = MapDialog.newInstance(latLng);
                mapDialog.show(getFragmentManager(), "map");
                mapDialog.setOnPlaceChange(new MapDialog.onPlaceChange() {
                    @Override
                    public void onPlaceChange(LatLng place) {
                        latLng = place;
                        Geocoder gc = new Geocoder(getActivity());

                        List<Address> list = null;
                        try {
                            list = gc.getFromLocation(place.latitude, place.longitude, 3);
                        } catch (Exception e) {}
                        eventPlace.setText(list.get(0).getAddressLine(0) + "\nLat: "+place.latitude+" Lng: "+place.longitude);
                        address = list.get(0).getAddressLine(0);
                        lat = place.latitude;
                        lng = place.longitude;
                    }
                });


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
            if(!eventName.getText().toString().equals("") || !eventDescription.getText().toString().equals("")
                    || !eventDate.getText().toString().equals("") || !eventTime.getText().toString().equals("")
                    || !eventPlace.getText().toString().equals("")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Dismiss")
                            .setMessage("Are you sure that you want to discard the event")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //We should return to the main activity but don't need to save anything
                                    onEventListener.onAction("dismiss");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

            }else {
                onEventListener.onAction("dismiss");
            }
        }
        if(item.getItemId()==R.id.save){//We should save the event futhermore, the same user who creates the event also should attend to it automatically
            String name = eventName.getText().toString();
            String description = eventDescription.getText().toString();
            String date = eventDate.getText().toString();
            String time = eventTime.getText().toString();
            String place = eventPlace.getText().toString();
            if(!name.equals("") && !description.equals("") && !date.equals("")
                    && !time.equals("") && !place.equals("")){
                //We check for a valid date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MM dd,yyyy HH:mm");
                String dateEvent = date+" "+time;
                try{
                    Date event = simpleDateFormat.parse(dateEvent);
                    Date now  = new Date();
                    if(event.compareTo(now)>0){//This date is valid
                        //We should check if the user change the image because if the user didn't change it, we should use the default image
                        Bitmap image;
                        if(isNewImage){
                            image = BitmapFactory.decodeResource(getResources(),R.drawable.image_available);
                        }else{
                            image = this.image;
                        }
                        onEventListener.onAction("save",name,description,event,address,lat,lng,image);
                    }else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Invalid date").setMessage("The date should be greater than now")
                            .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();

                    }
                }catch (Exception e){

                }
            }else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Invalid save")
                        .setMessage("All the fields are required")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth(),eventImage.getHeight(),true);
            image = bitmapScaled;
            eventImage.setImageBitmap(bitmapScaled);
            eventImage.invalidate();
        }
        if(requestCode==2 && resultCode==Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), data.getData());
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth(),eventImage.getHeight(),true);
                image = bitmapScaled;
                eventImage.setImageBitmap(bitmapScaled);
            }catch (Exception e){

            }

        }
        isNewImage = true;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onEventListener = (OnEventListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //There is a issue with the textView
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            name = savedInstanceState.getString("name");
            description = savedInstanceState.getString("description");
            date = savedInstanceState.getString("date");
            time = savedInstanceState.getString("time");
            place = savedInstanceState.getString("place");
            if(savedInstanceState.getBoolean("isImage")) {
                image = BitmapFactory.decodeByteArray(savedInstanceState.getByteArray("image"),
                        savedInstanceState.getByteArray("image").length, 0);
            }
        }
    }



    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", eventName.getText().toString());
        outState.putString("description", eventDescription.getText().toString());
        outState.putString("date", eventDate.getText().toString());
        outState.putString("time", eventTime.getText().toString());
        outState.putString("place", eventPlace.getText().toString());
        if(image!=null) {
            outState.putBoolean("isImage",true);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 50, bs);
            outState.putByteArray("image", bs.toByteArray());
        }else{
            outState.putBoolean("isImage",false);
        }
    }*/


}
