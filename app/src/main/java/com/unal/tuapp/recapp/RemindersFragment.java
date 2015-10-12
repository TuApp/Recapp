package com.unal.tuapp.recapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.Reminder;
import com.unal.tuapp.recapp.data.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by andresgutierrez on 8/9/15.
 */
public class RemindersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private User user;
    private View root;
    private RecyclerView recyclerView;
    private RecycleRemindersAdapter recycleRemindersAdapter;
    private static Reminder deleteReminder;
    private static int positionReminder;
    private static int REMINDER = 25;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reminders,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.user_reminders);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        final List<Reminder> reminders = new ArrayList<>();
        recycleRemindersAdapter = new RecycleRemindersAdapter(reminders);
        /*recycleRemindersAdapter.setOnItemClickListener(new RecycleRemindersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long reminderId) {
                getActivity().getContentResolver().delete(
                        RecappContract.ReminderEntry.CONTENT_URI,
                        RecappContract.ReminderEntry._ID + " = ?",
                        new String[]{"" + reminderId}
                );
            }
        });*/
        recyclerView.setAdapter(recycleRemindersAdapter);
        ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!recycleRemindersAdapter.getPlaces().get(viewHolder.getAdapterPosition()).isSwipe()) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                positionReminder = viewHolder.getAdapterPosition();
                deleteReminder = recycleRemindersAdapter.getPlaces().remove(viewHolder.getAdapterPosition());
                getActivity().getContentResolver().delete(
                        RecappContract.ReminderEntry.CONTENT_URI,
                        RecappContract.ReminderEntry._ID + "= ?",
                        new String[]{""+deleteReminder.getId()}
                );
                recycleRemindersAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());


                Snackbar.make(getActivity().findViewById(R.id.user_detail_coordination),"the event "+deleteReminder.getName() +" was deleted",Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ContentValues values = new ContentValues();
                                values.put(RecappContract.ReminderEntry.COLUMN_NAME,deleteReminder.getName());
                                values.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION,deleteReminder.getDescription());
                                values.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION,deleteReminder.getNotificiation());
                                values.put(RecappContract.ReminderEntry.COLUMN_END_DATE, deleteReminder.getEndDate());
                                values.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,deleteReminder.getUserId());
                                values.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY,deleteReminder.getPlaceId());
                                Uri uri = getActivity().getContentResolver().insert(
                                        RecappContract.ReminderEntry.CONTENT_URI,
                                        values

                                );
                                deleteReminder.setId(Long.parseLong(uri.getLastPathSegment()));
                                recycleRemindersAdapter.getPlaces().add(positionReminder,deleteReminder);
                                recycleRemindersAdapter.notifyItemInserted(positionReminder);
                            }
                        })
                        .show();

            }
        });
        swipeToDismiss.attachToRecyclerView(recyclerView);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(REMINDER)==null){
            getLoaderManager().initLoader(REMINDER,null,this);
        }else{
            getLoaderManager().restartLoader(REMINDER,null,this);
        }
        //getLoaderManager().initLoader(REMINDER,null,this);



    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecappContract.ReminderEntry.TABLE_NAME+"."+
                RecappContract.ReminderEntry.COLUMN_END_DATE + " DESC ";
        return new CursorLoader(
                getActivity(),
                RecappContract.ReminderEntry.buildReminderUserUri(user.getId()),
                new String[]{RecappContract.PlaceEntry.COLUMN_NAME,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_RATING,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_ADDRESS,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_DESCRIPTION,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_LAT,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_LOG,
                        RecappContract.PlaceEntry.TABLE_NAME+"."+RecappContract.PlaceEntry.COLUMN_WEB,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry._ID,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry.COLUMN_NAME,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry.COLUMN_DESCRIPTION,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry.COLUMN_NOTIFICATION,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry.COLUMN_END_DATE,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+ RecappContract.ReminderEntry.COLUMN_PLACE_KEY,
                        RecappContract.ReminderEntry.TABLE_NAME+"."+RecappContract.ReminderEntry.COLUMN_USER_KEY},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Reminder> reminders = Reminder.allReminder(data);
        recycleRemindersAdapter.swapData(reminders);
        recycleRemindersAdapter.setReminderCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //List<Reminder> reminders = new ArrayList<>();
        //recycleRemindersAdapter.swapDate(reminders);
        recycleRemindersAdapter.closeCursor();

    }
}
