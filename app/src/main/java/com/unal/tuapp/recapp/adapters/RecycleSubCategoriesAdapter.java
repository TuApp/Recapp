package com.unal.tuapp.recapp.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
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
        private CardView mCardView;
        private ImageView mCategoryImage;
        private TextView mCategoryText;
        private TextView mCategoryId;

        public SubcategoryViewHolder(final View itemView){
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.category_card);
            mCategoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            mCategoryText = (TextView) itemView.findViewById(R.id.category_item);
            mCategoryId = (TextView) itemView.findViewById(R.id.category_id);

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
        View view = inflater.inflate(R.layout.category_item,parent,false);
        viewHolder = new SubcategoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubcategoryViewHolder subcategoryViewHolder = (SubcategoryViewHolder) holder;
        SubCategory subCategory =subCategories.get(position);
        subcategoryViewHolder.mCategoryText.setText(subCategory.getName());
        byte[] image = subCategory.getImage();
        subcategoryViewHolder.mCategoryImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width =  subcategoryViewHolder.mCategoryImage.getMeasuredWidth();
        int height =  subcategoryViewHolder.mCategoryImage.getMeasuredHeight();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        if(bitmap!=null) {
            Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, width * 4, height * 4, true);
            subcategoryViewHolder.mCategoryImage.setImageBitmap(bitmapScaled);
        }
        subcategoryViewHolder.mCategoryImage.setImageResource(R.drawable.ic_home);
        //subcategoryViewHolder.mCategoryImage.setImageBitmap(bitmapScaled);
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