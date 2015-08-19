package com.unal.tuapp.recapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.unal.tuapp.recapp.data.RecappContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private EditText date;
    private EditText time;
    private long id;

    public interface OnDialogListener{
        void onAction(String action);
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
            onDialogListener.onAction("dismiss");
        }
        if(item.getItemId()==R.id.save){
            onDialogListener.onAction("save");
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
        placeImage.setImageBitmap(BitmapFactory.decodeByteArray(data.getBlob(3), 0, (data.getBlob(3)).length));
        placeName.setText(data.getString(1));
        placeAddress.setText(data.getString(2));
        placeCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        placeCursor.close();
    }
}
