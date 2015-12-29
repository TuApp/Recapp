package com.unal.tuapp.recapp.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.RecappContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andresgutierrez on 8/18/15.
 */
public class ReminderDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View root;
    private OnDialogListener onDialogListener;
    private final int PLACE=500;
    private Cursor placeCursor;
    private ImageView placeImage;
    private TextView placeName;
    private TextView placeAddress;
    private EditText eventName;
    private EditText eventDescription;
    private CheckBox eventNotification;
    private EditText date;
    private EditText time;
    private long id;


    public interface OnDialogListener{
        void onAction(String action,Object...values);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.reminder_dialog,container,false);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().
                setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.newEvent);
        eventName = (EditText) root.findViewById(R.id.event_name);
        eventDescription = (EditText) root.findViewById(R.id.event_description);
        eventNotification = (CheckBox) root.findViewById(R.id.event_notification);
        RelativeLayout placeLayout = (RelativeLayout) root.findViewById(R.id.event_place);
        LinearLayout dateLayout = (LinearLayout) root.findViewById(R.id.date_event);
        date = (EditText) dateLayout.findViewById(R.id.date);
        time = (EditText) dateLayout.findViewById(R.id.time);
        date.setOnClickListener(new View.OnClickListener() {
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
                        date.setText(new SimpleDateFormat("EEE, MMM d, yyyy").format(calendar.getTime()));

                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();

            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        time.setText(hour+":"+minute);
                    }
                },mHour,mMinute,true);
                timePickerDialog.show();
            }
        });


        placeImage = (ImageView) placeLayout.findViewById(R.id.event_place_image);
        placeName = (TextView) placeLayout.findViewById(R.id.event_place_name);
        placeAddress = (TextView) placeLayout.findViewById(R.id.event_place_address);
        if(getLoaderManager().getLoader(PLACE)==null){
            getLoaderManager().initLoader(PLACE, null, this);
        }else{
            getLoaderManager().restartLoader(PLACE,null,this);
        }
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reminder_dialog,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(!eventName.getText().toString().equals("") || !eventDescription.getText().toString().equals("")||
                    !date.getText().toString().equals("") || !time.getText().toString().equals("")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        getActivity()
                );
                alertDialog.setTitle("Dismiss")
                        .setMessage("Are you sure that you want to discard the reminder")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onDialogListener.onAction("dismiss");
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
            }else{
                onDialogListener.onAction("dismiss");
            }
        }
        if(item.getItemId()==R.id.save){
            String name = eventName.getText().toString();
            String description = eventDescription.getText().toString();
            boolean notification = false;
            if(eventNotification.isChecked()){
                notification = true;
            }
            if(!name.equals("") && !description.equals("") &&
                    !date.getText().toString().equals("") && !time.getText().toString().equals("")) {
                //We should save the reminder
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd,yyyy HH:mm");
                String dateEvent = date.getText().toString()+" "+time.getText().toString();
                try {
                    Date event = dateFormat.parse(dateEvent);
                    Date now = new Date();

                    if(event.compareTo(now)>0){ // We can create the event
                        onDialogListener.onAction("save",name,description,notification,
                                event,placeName.getText().toString(),placeAddress.getText().toString(),
                                ((BitmapDrawable)placeImage.getDrawable()).getBitmap());
                    }else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                getActivity()
                        );
                        alertDialog.setTitle("Invalid date")
                                .setMessage("The date should be greater than now")
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
                } catch (ParseException e) {

                }

            }else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        getActivity()
                );
                alertDialog.setTitle("Invalid saved")
                        .setMessage("All fields are required")
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onDialogListener = (OnDialogListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.PlaceEntry.buildPlaceUri(this.id),
                new String[]{RecappContract.PlaceEntry._ID,RecappContract.PlaceEntry.COLUMN_NAME,
                        RecappContract.PlaceEntry.COLUMN_ADDRESS,RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        placeImage.setImageBitmap(BitmapFactory.decodeByteArray(data.getBlob(3), 0, (data.getBlob(3)).length,options));
        placeName.setText(data.getString(1));
        placeAddress.setText(data.getString(2));
        placeCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        placeCursor.close();
    }
}
