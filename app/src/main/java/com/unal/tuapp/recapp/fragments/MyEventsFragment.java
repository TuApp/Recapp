package com.unal.tuapp.recapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleEventsAdapter;
import com.unal.tuapp.recapp.data.Event;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.others.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 10/12/15.
 */
public class MyEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView myEvents;
    private RecycleEventsAdapter myEventsAdapter;
    private View root;
    private User user;
    private static int EVENT= 1872;
    private static OnEventListener onEventListener;

    public interface OnEventListener{
        void onAction(long id);
    }

    public void setOnEventListener(OnEventListener onEventListener){
        this.onEventListener = onEventListener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        root = inflater.inflate(R.layout.fragment_my_events,container,false);
        myEvents = (RecyclerView) root.findViewById(R.id.user_myEvents);
        List<Event> events = new ArrayList<>();
        myEventsAdapter = new RecycleEventsAdapter(events);
        RecyclerView.LayoutManager eventManager = Utility.getLayoutManager(getActivity(),getResources().getConfiguration().screenWidthDp);
        myEventsAdapter.setOnEventListener(new RecycleEventsAdapter.OnEventListener() {
            @Override
            public void onAction(long id) {
                if(onEventListener!=null){
                    onEventListener.onAction(id);
                }
            }
        });
        myEvents.setLayoutManager(eventManager);
        myEvents.setAdapter(myEventsAdapter);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(EVENT)==null){
            getLoaderManager().initLoader(EVENT,null,this);
        }else{
            getLoaderManager().restartLoader(EVENT,null,this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == EVENT) {
            return new CursorLoader(
                    getActivity(),
                    RecappContract.EventEntry.CONTENT_URI,
                    null,
                    RecappContract.EventEntry.COLUMN_CREATOR+ " = ? ",
                    new String[]{user.getEmail()},
                    RecappContract.EventEntry.COLUMN_DATE + " ASC "
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==EVENT){
            List<Event> events = Event.allEvents(data);
            myEventsAdapter.swapData(events);
            myEventsAdapter.setEventsCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myEventsAdapter.closeCursor();
    }
}
