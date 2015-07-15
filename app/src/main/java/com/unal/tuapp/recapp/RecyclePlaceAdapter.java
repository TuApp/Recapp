package com.unal.tuapp.recapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class RecyclePlaceAdapter extends RecyclerView.Adapter<RecyclePlaceAdapter.PlaceViewHolder> {
    private List<String> places;
    public static class  PlaceViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        public PlaceViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.place_item);
        }
    }
    public RecyclePlaceAdapter(List<String> places){
        this.places = places;
    }
    @Override
    public int getItemCount() {
        return places.size();
    }
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_item,viewGroup,false);
        PlaceViewHolder placeViewHolder = new PlaceViewHolder(view);
        return placeViewHolder;
    }
    @Override
    public void onBindViewHolder(PlaceViewHolder placeViewHolder, int i){
        placeViewHolder.name.setText(places.get(i));
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }
}