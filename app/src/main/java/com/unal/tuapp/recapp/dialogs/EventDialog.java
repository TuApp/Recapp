package com.unal.tuapp.recapp.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.data.RecappContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by andresgutierrez on 9/28/15.
 */
public class  EventDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
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
    private long eventId;
    private static int EVENT = 3872;


    public interface OnEventListener{
        void onAction(String action,Object...objects);
    }
    public void setOnEventListener(OnEventListener onEventListener){
        this.onEventListener = onEventListener;
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
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            eventId = extras.getLong("event",-1);
            if(eventId!=-1){
                if(getLoaderManager().getLoader(EVENT)==null){
                    getLoaderManager().initLoader(EVENT,null,this);
                }else{
                    getLoaderManager().initLoader(EVENT,null,this);
                }
            }
        }
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
                        if(list!=null && list.size()>0) {
                            eventPlace.setText(list.get(0).getAddressLine(0) + "\nLat: " + place.latitude + " Lng: " + place.longitude);
                            address = list.get(0).getAddressLine(0);
                        }else{
                            eventPlace.setText("Lat: " + place.latitude + " Lng: " + place.longitude);
                        }
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
        inflater.inflate(R.menu.event_dialog, menu);
        if(eventId!=-1){
            menu.findItem(R.id.delete_event).setVisible(true);
        }
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
                        if(!isNewImage){
                            image = BitmapFactory.decodeResource(getResources(),R.drawable.image_available);
                        }else{
                            image = this.image;
                        }
                        if(address==null){
                            address = eventPlace.getText().toString().split("Lat")[0].trim();
                        }
                        onEventListener.onAction("save",name,description,event,address,latLng.latitude,latLng.longitude,image);
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
        if(item.getItemId()==R.id.delete_event){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Delete")
                    .setMessage("Do you want to delete the event")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onEventListener.onAction("delete");
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth()/2,eventImage.getHeight(),true);
            image = bitmapScaled;
            eventImage.setImageBitmap(bitmapScaled);
            eventImage.invalidate();
        }
        if(requestCode==2 && resultCode==Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), data.getData());
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,eventImage.getWidth()/2,eventImage.getHeight(),true);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==EVENT){
            return new CursorLoader(
                    getActivity(),
                    RecappContract.EventEntry.buildEventUri(eventId),
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==EVENT){
            if(data.moveToFirst()){
                eventName.setText(data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_NAME)));
                eventDescription.setText(data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DESCRIPTION)));
                String dateTime = Utility.getDateTime(data.getLong(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_DATE)));
                String []time = dateTime.split("-");
                eventDate.setText(time[0]);
                eventTime.setText(time[1]);
                double lat = data.getDouble(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LAT));
                double lng = data.getDouble(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_LOG));
                String address = data.getString(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_ADDRESS));
                eventPlace.setText(address + "\nLat: " + lat + " Lng: " + lng);
                byte [] image = data.getBlob(data.getColumnIndexOrThrow(RecappContract.EventEntry.COLUMN_IMAGE));
                this.image = BitmapFactory.decodeByteArray(image,0,image.length);
                eventImage.setImageBitmap(this.image);
                latLng = new LatLng(lat,lng);
                isNewImage = true;

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
