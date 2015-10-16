package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.unal.tuapp.recapp.data.SubCategory;

import java.util.List;

/**
 * Created by andresgutierrez on 7/22/15.
 */
public class RecycleSubCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SubCategory> subCategories;
    private Cursor subCategoriesCursor =null;
    public static OnItemClickListener mItemClickListener;

    public RecycleSubCategoriesAdapter(List<SubCategory> subcategories) {
        this.subCategories = subcategories;
    }

    public static class SubcategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mSubCategory;
        private CardView mCardView;

        public SubcategoryViewHolder(final View itemView){
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.subcategory_card);
            mSubCategory = (TextView) itemView.findViewById(R.id.subcategory_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                //placeImage.setBackgroundColor(GalleryFragment.SELECTED_BORDER);
                long id = getAdapterPosition();
                mItemClickListener.onItemClick(view,id);
            }
        }
    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.subcategory_item,parent,false);
        viewHolder = new SubcategoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubcategoryViewHolder subcategoryViewHolder = (SubcategoryViewHolder) holder;
        SubCategory subCategory =subCategories.get(position);
        subcategoryViewHolder.mSubCategory.setText(subCategory.getName());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, long position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public void swapData(List<SubCategory> subCategories){
        this.subCategories = subCategories;
        notifyDataSetChanged();
    }
    public void setSubCategoriesCursor(Cursor cursor){
        if(subCategoriesCursor !=null){
            closeCursor();
        }
        this.subCategoriesCursor = cursor;
    }
    public void closeCursor(){
        subCategoriesCursor.close();
        subCategoriesCursor = null;

    }


}