package com.unal.tuapp.recapp.adapters;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Place;

import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class RecyclePlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor placeCursor=null;
    private static List<Place> places;
    public static OnItemClickListener mItemClickListener;
    public final int FAVORITE=0,NORMAL=1;

    public static class  PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private ImageView image;
        private CardView cardView;

        public PlaceViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.place_card);
            name = (TextView) itemView.findViewById(R.id.place_item);
            image = (ImageView) itemView.findViewById(R.id.card_image);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                long id = places.get(getAdapterPosition()).getId();
                mItemClickListener.onItemClick(view,id);
            }
        }


    }
    public static class PlaceViewHolderFavorite extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private CardView cardView;
        private ImageView imageView;
        private TextView address;

        public  PlaceViewHolderFavorite (View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.place_card_favorite);
            name = (TextView) itemView.findViewById(R.id.place_item_favorite);
            imageView = (ImageView) itemView.findViewById(R.id.card_image_favorite);
            address = (TextView) itemView.findViewById(R.id.place_address_favorite);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                long id = places.get(getAdapterPosition()).getId();
                mItemClickListener.onItemClick(view,id);
            }
        }

    }
    public interface OnItemClickListener{
        void onItemClick(View view,long position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public RecyclePlaceAdapter(List<Place> places){

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

    public void setPlaceCursor(Cursor cursor){
        if(placeCursor!=null){
            closeCursor();
        }
        placeCursor = cursor;
    }
    public void closeCursor(){
        if(placeCursor!=null) {
            placeCursor.close();
        }
        placeCursor = null;
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
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        switch (viewHolder.getItemViewType()){
            case FAVORITE:
                PlaceViewHolderFavorite placeViewHolderFavorite = (PlaceViewHolderFavorite) viewHolder;
                placeViewHolderFavorite.name.setText(places.get(i).getName());
                placeViewHolderFavorite.imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                        places.get(i).getImageFavorite(),0,places.get(i).getImageFavorite().length,options
                ));
                placeViewHolderFavorite.address.setText(places.get(i).getAddress());
                break;
            case NORMAL:
                PlaceViewHolder placeViewHolder = (PlaceViewHolder) viewHolder;
                placeViewHolder.name.setText(places.get(i).getName());

                placeViewHolder.image.setImageBitmap(BitmapFactory.decodeByteArray(
                        places.get(i).getImageFavorite(), 0, places.get(i).getImageFavorite().length,options
                ));
                break;
        }
    }
    public void swapData(List<Place> newPlace){
        places = newPlace;
        notifyDataSetChanged();
    }

}
