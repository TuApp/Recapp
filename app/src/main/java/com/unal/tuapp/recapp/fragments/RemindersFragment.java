package com.unal.tuapp.recapp.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleRemindersAdapter;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.Reminder;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.ReminderEndPoint;

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

        recyclerView.setAdapter(recycleRemindersAdapter);
        ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!recycleRemindersAdapter.getPlaces().get(viewHolder.getAdapterPosition()).isSwipe() ||
                        !Utility.isNetworkAvailable(getContext())) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.internet)
                            .setMessage(R.string.need_internet)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                    return 0;

                }
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
                final com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder reminder = new com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder();
                reminder.setId(deleteReminder.getId());
                Pair<Context,Pair<com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder,String>> pair = new Pair<>(getContext(),new Pair<>(reminder,"deleteReminder"));
                new ReminderEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);


                Snackbar.make(getActivity().findViewById(R.id.user_detail_coordination),"the event "+deleteReminder.getName() +" was deleted",Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                reminder.setId(deleteReminder.getId());
                                reminder.setUserId(deleteReminder.getUserId());
                                reminder.setPlaceId(deleteReminder.getPlaceId());
                                reminder.setEndDate(deleteReminder.getEndDate());
                                reminder.setNotification(deleteReminder.getNotification());
                                reminder.setDescription(deleteReminder.getDescription());
                                reminder.setName(deleteReminder.getName());
                                Pair<Context,Pair<com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder,String>> pair = new Pair<>(getContext(),new Pair<>(reminder,"addReminder"));
                                new ReminderEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);
                                ContentValues values = new ContentValues();
                                values.put(RecappContract.ReminderEntry.COLUMN_NAME,deleteReminder.getName());
                                values.put(RecappContract.ReminderEntry.COLUMN_DESCRIPTION,deleteReminder.getDescription());
                                values.put(RecappContract.ReminderEntry.COLUMN_NOTIFICATION,deleteReminder.getNotificiation());
                                values.put(RecappContract.ReminderEntry.COLUMN_END_DATE, deleteReminder.getEndDate());
                                values.put(RecappContract.ReminderEntry.COLUMN_USER_KEY,deleteReminder.getUserId());
                                values.put(RecappContract.ReminderEntry.COLUMN_PLACE_KEY,deleteReminder.getPlaceId());
                                values.put(RecappContract.ReminderEntry._ID,deleteReminder.getId());
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
