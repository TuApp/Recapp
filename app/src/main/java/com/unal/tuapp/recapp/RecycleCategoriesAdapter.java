package com.unal.tuapp.recapp;

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

import com.unal.tuapp.recapp.data.Category;

import java.util.List;

/**
 * Created by andresgutierrez on 7/22/15.
 */
public class RecycleCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Category> categories;
    private Cursor categoriesCursor =null;
    public static OnItemClickListener mItemClickListener;

    public RecycleCategoriesAdapter(List<Category> subcategories) {
        this.categories = subcategories;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CardView mCardView;
        private ImageView mCategoryImage;
        private TextView mCategoryText;
        private TextView mCategoryId;

        public CategoryViewHolder(final View itemView){
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
                id = Long.parseLong(mCategoryId.getText().toString());
                mItemClickListener.onItemClick(view,id);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_item, parent, false);
        viewHolder = new CategoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder subcategoryViewHolder = (CategoryViewHolder) holder;
        Category category =categories.get(position);
        byte[] image = category.getImage();
        subcategoryViewHolder.mCategoryImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width =  subcategoryViewHolder.mCategoryImage.getMeasuredWidth();
        int height =  subcategoryViewHolder.mCategoryImage.getMeasuredHeight();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, width*4, height*4, true);
        subcategoryViewHolder.mCategoryImage.setImageBitmap(bitmapScaled);
        subcategoryViewHolder.mCategoryText.setText(category.getName());
        subcategoryViewHolder.mCategoryId.setText(category.getId()+"");

    }


    public interface OnItemClickListener{
        void onItemClick(View view, long position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public void swapData(List<Category> categories){
        this.categories = categories;
        notifyDataSetChanged();
    }
    public void setCategoriesCursor(Cursor cursor){
        if(categoriesCursor !=null){
            closeCursor();
        }
        this.categoriesCursor = cursor;
    }
    public void closeCursor(){
        categoriesCursor.close();
        categoriesCursor = null;

    }


}