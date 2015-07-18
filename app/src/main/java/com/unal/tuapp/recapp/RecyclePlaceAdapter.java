package com.unal.tuapp.recapp;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class RecyclePlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> places;
    public static OnItemClickListener mItemClickListener;
    public final int FAVORITE=0,NORMAL=1;

    public static class  PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private CardView cardView;

        public PlaceViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.place_card);
            name = (TextView) itemView.findViewById(R.id.place_item);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                mItemClickListener.onItemClick(view,getAdapterPosition());
            }
        }


    }
    public static class PlaceViewHolderFavorite extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private CardView cardView;
        private ImageView imageView;

        public  PlaceViewHolderFavorite (View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.place_card_favorite);
            name = (TextView) itemView.findViewById(R.id.place_item_favorite);
            imageView = (ImageView) itemView.findViewById(R.id.card_image_favorite);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                mItemClickListener.onItemClick(view,getAdapterPosition());
            }
        }

    }
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public RecyclePlaceAdapter(List<String> places){

        this.places = places;
    }
    @Override
    public int getItemCount() {

        return places.size();
    }
    @Override
    public int getItemViewType(int position) {
        if(position<3){
            return FAVORITE;
        }else{
            return NORMAL;
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (i){
            case FAVORITE:
                View fav = inflater.inflate(R.layout.place_item_favorite,viewGroup,false);
                viewHolder = new PlaceViewHolderFavorite(fav);
                break;
            case NORMAL:
                View normal = inflater.inflate(R.layout.place_item,viewGroup,false);
                viewHolder = new PlaceViewHolder(normal);
                break;
            default:
                View def = inflater.inflate(R.layout.place_item_favorite,viewGroup,false);
                viewHolder = new PlaceViewHolderFavorite(def);
        }

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        switch (viewHolder.getItemViewType()){
            case FAVORITE:
                PlaceViewHolderFavorite placeViewHolderFavorite = (PlaceViewHolderFavorite) viewHolder;
                placeViewHolderFavorite.name.setText(places.get(i));
                placeViewHolderFavorite.imageView.setImageResource(R.drawable.background_material);
                break;
            case NORMAL:
                PlaceViewHolder placeViewHolder = (PlaceViewHolder) viewHolder;
                placeViewHolder.name.setText(places.get(i));
                break;
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }
}
