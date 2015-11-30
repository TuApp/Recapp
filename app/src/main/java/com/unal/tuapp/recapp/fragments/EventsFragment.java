package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleEventsAdapter;
import com.unal.tuapp.recapp.adapters.RecycleEventsGoingAdapter;
import com.unal.tuapp.recapp.data.Event;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.EventEndPoint;

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
    public static SwipeRefreshLayout mySwipeRefresh;

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
        mySwipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.events_refresh);
        mySwipeRefresh.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                com.unal.tuapp.recapp.backend.model.eventApi.model.Event event = new com.unal.tuapp.recapp.backend.model.eventApi.model.Event();
                Pair<Context, Pair<com.unal.tuapp.recapp.backend.model.eventApi.model.Event, String>> pairEvent = new Pair<>(getContext(), new Pair<>(event, "getEvents"));
                new EventEndPoint(true).execute(pairEvent);
            }
        });


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
