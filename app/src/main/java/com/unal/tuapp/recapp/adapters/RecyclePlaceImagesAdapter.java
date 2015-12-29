package com.unal.tuapp.recapp.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.fragments.GalleryFragment;

import java.util.List;

/**
 * Created by fabianlm17 on 21/09/15.
 */
public class RecyclePlaceImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor placeImagesCursor =null;
    private static List<byte[]> placeImages;
    public static OnItemClickListener mItemClickListener;
    private int currentSelectedPositionImage;

    public static class PlaceImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView placeImage;

        public PlaceImagesViewHolder(View itemView){
            super(itemView);
            placeImage = (ImageView) itemView.findViewById(R.id.place_image);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                placeImage.setBackgroundColor(GalleryFragment.SELECTED_BORDER);
                long id = getAdapterPosition();
                mItemClickListener.onItemClick(view,id);
            }
        }




    }
    public byte[] getImage(int position){
        return  placeImages.get(position);
    }

    public int getCurrentSelectedPositionImage() {
        return currentSelectedPositionImage;
    }

    public void setCurrentSelectedPositionImage(int currentSelectedPositionImage) {
        this.currentSelectedPositionImage = currentSelectedPositionImage;
    }

    public interface OnItemClickListener{
        void onItemClick(View view,long position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public RecyclePlaceImagesAdapter(List<byte[]> placeImages){

        this.placeImages = placeImages;
    }
    @Override
    public int getItemCount() {

        return placeImages.size();
    }

    public void setPlaceImagesCursor(Cursor cursor){
        if(placeImagesCursor !=null){
            closeCursor();
        }
        placeImagesCursor = cursor;
    }
    public void closeCursor(){
        if(placeImagesCursor !=null) {
            placeImagesCursor.close();
        }
        placeImagesCursor = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View def = inflater.inflate(R.layout.place_image_item,viewGroup,false);
        viewHolder = new PlaceImagesViewHolder(def);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i){
        PlaceImagesViewHolder placeImagesViewHolder = (PlaceImagesViewHolder) viewHolder;
        byte[] image = placeImages.get(i);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length,options);
        placeImagesViewHolder.placeImage.setImageBitmap(bitmap);
        placeImagesViewHolder.placeImage.setPadding(1, 1, 1, 1);
        int COLOR = GalleryFragment.NON_SELECTED_BORDER;
        if(i == currentSelectedPositionImage)
            COLOR = GalleryFragment.SELECTED_BORDER;
        placeImagesViewHolder.placeImage.setBackgroundColor(COLOR);
    }

    public void swapData(List<byte[]> newPlaceImages){
        placeImages = newPlaceImages;
        notifyDataSetChanged();
    }

}
