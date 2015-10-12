package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class EventsFragment extends Fragment {
    private View root;
    private static RecyclerView events;
    private static RecyclerView eventsGoing;
    private static RecycleEventsAdapter eventsAdapter;
    private static RecycleEventsGoingAdapter eventsGoingAdapter;

    private static OnEventListener onEventListener;

    public interface OnEventListener {
        void onAction(long id);
    }
    public void setOnEventListener(OnEventListener onEventListener){
        this.onEventListener = onEventListener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_events,container,false);
        events = (RecyclerView) root.findViewById(R.id.event_recycler);
        eventsGoing = (RecyclerView) root.findViewById(R.id.event_going_recycler);


        LinearLayoutManager eventManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager eventGoingManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        events.setLayoutManager(eventManager);
        eventsGoing.setLayoutManager(eventGoingManager);

        List<Event> eventList = new ArrayList<>();
        List<Event> eventGoingList = new ArrayList<>();

        eventsAdapter = new RecycleEventsAdapter(eventList);
        eventsGoingAdapter = new RecycleEventsGoingAdapter(eventGoingList);

        eventsAdapter.setOnEventListener(new RecycleEventsAdapter.OnEventListener() {
            @Override
            public void onAction(long id) {
                if (onEventListener != null) {
                    onEventListener.onAction(id);
                }
            }
        });

        eventsGoingAdapter.setOnEventListener(new RecycleEventsGoingAdapter.OnEventListener() {
            @Override
            public void onAction(long id) {
                if (onEventListener != null) {
                    onEventListener.onAction(id);
                }
            }
        });


        events.setAdapter(eventsAdapter);
        eventsGoing.setAdapter(eventsGoingAdapter);

        return root;
    }

    public void setDataEvents(List<Event> events,Cursor cursor){
        eventsAdapter.swapData(events);
        eventsAdapter.setEventsCursor(cursor);
    }
    public void setDataEventsGoing(List<Event> events,Cursor cursor){
        eventsGoingAdapter.swapData(events);
        eventsGoingAdapter.setEventsCursor(cursor);
    }
    public void closeData(){
        eventsAdapter.closeCursor();
        eventsGoingAdapter.closeCursor();
    }


}
