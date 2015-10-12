package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.Event;

import java.util.List;

/**
 * Created by andresgutierrez on 10/11/15.
 */
public class RecycleEventsGoingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor eventsCursor = null;
    private static List<Event> events;
    private static OnEventListener onEventListener;

    public interface OnEventListener{
        void onAction(long id);
    }

    public void setOnEventListener(OnEventListener onEventListener){
        this.onEventListener = onEventListener;
    }

    public RecycleEventsGoingAdapter(List<Event> events){
        this.events = events;
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private TextView description;
        private TextView address;
        private ImageView image;
        private TextView date;

        public EventsViewHolder(View item){
            super(item);
            name = (TextView) item.findViewById(R.id.event_item_name);
            description = (TextView) item.findViewById(R.id.event_item_description);
            address  = (TextView) item.findViewById(R.id.event_item_address);
            image = (ImageView) item.findViewById(R.id.event_item_image);
            date = (TextView) item.findViewById(R.id.event_item_date);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onEventListener!=null){
                Event event = events.get(getAdapterPosition());
                onEventListener.onAction(event.getId());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_item, parent, false);
        viewHolder = new EventsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EventsViewHolder viewHolder = (EventsViewHolder) holder;
        Event event = events.get(position);
        viewHolder.name.setText(event.getName());
        viewHolder.description.setText(event.getDescription());
        viewHolder.address.setText(event.getAddress());
        viewHolder.image.setImageBitmap(BitmapFactory.decodeByteArray(event.getImage(), 0, event.getImage().length));
        viewHolder.date.setText(Utility.getDate(event.getDate()));
    }
    public void swapData(List<Event> events){
        this.events = events;
        notifyDataSetChanged();
    }
    public void setEventsCursor(Cursor cursor){
        this.eventsCursor = cursor;
    }
    public void closeCursor(){
        if(eventsCursor!=null) {
            eventsCursor.close();
        }
    }
    public List<Event> getEvents(){
        return this.events;
    }
}
