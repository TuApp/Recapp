package com.unal.tuapp.recapp.adapters;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.PlaceImages;

import java.util.List;

/**
 * Created by andresgutierrez on 11/8/15.
 */
public class RecycleImagesCompanyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PlaceImages> images;
    private Cursor companyImageCursor;

    public RecycleImagesCompanyAdapter(List<PlaceImages> images) {
        this.images = images;
    }

    public static class CompanyImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public CompanyImageViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.company_card_image);

        }
        
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.place_image_company_item,parent,false);
        viewHolder = new CompanyImageViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CompanyImageViewHolder imageViewHolder = (CompanyImageViewHolder) holder;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        imageViewHolder.imageView.setImageBitmap(
                BitmapFactory.decodeByteArray(images.get(position).getImage(),0,images.get(position).getImage().length,options)
        );
    }
    public void swapData(List<PlaceImages> images){
        this.images = images;
        notifyDataSetChanged();
    }
    public void setCompanyImageCursor(Cursor data){
        if(companyImageCursor!=null){
            closeCursor();
        }
        this.companyImageCursor = data;

    }
    public void closeCursor(){
        companyImageCursor.close();
    }
    public List<PlaceImages> getCompanyImages(){
        return images;
    }
}
