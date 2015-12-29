package com.unal.tuapp.recapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.fragments.GalleryFragment;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.data.Comment;

import java.util.List;

/**
 * Created by andresgutierrez on 8/12/15.
 */
public class RecycleCommentsUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static OnItemClickListener mItemClickListener;
    private static List<Comment> comments;
    private Cursor commentCursor=null;
    private static int commentPosition;
    private Context context;


    public RecycleCommentsUserAdapter(List<Comment> comments,Context context) {
        this.comments = comments;
        this.context = context;
    }



    public interface OnItemClickListener{
        void onItemClick(View view,long id,long idPlace);
    }
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener){
        mItemClickListener = onItemClickListener;
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private TextView comment;
        private RatingBar ratingBar;
        private de.hdodenhof.circleimageview.CircleImageView imageView;
        private TextView date;

        public CommentsViewHolder(View itemView){
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.comment_text);
            date = (TextView) itemView.findViewById(R.id.comment_date);
            ratingBar = (RatingBar) itemView.findViewById(R.id.comment_rating);
            imageView = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.comment_foto);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if(mItemClickListener!=null){
                commentPosition = getAdapterPosition();
                long id = comments.get(getAdapterPosition()).getId();
                long idPlace = comments.get(getAdapterPosition()).getIdPlace();

                mItemClickListener.onItemClick(view,id,idPlace);

            }
            return false;
        }
    }

    public int getCommentPositon() {
        return commentPosition;
    }

    public void setCommentPositon(int commentPositon) {
        RecycleCommentsUserAdapter.commentPosition = commentPositon;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.commet_item,parent,false);
        viewHolder = new CommentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;
        Comment comment = comments.get(position);
        commentsViewHolder.comment.setText(comment.getComment());
        // We need to change it with the image which is stored in the database
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        if(comment.getImageProfile()!=null){
            commentsViewHolder.imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(comment.getImageProfile(),0,comment.getImageProfile().length,options));
        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.image_available,options);
            commentsViewHolder.imageView.setImageBitmap(bitmap);
        }
        if(commentPosition==position){
            commentsViewHolder.itemView.setBackgroundColor(GalleryFragment.SELECTED_BORDER);
        }else{
            commentsViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
        //We need to change the value, dynamically
        commentsViewHolder.ratingBar.setRating((float)comment.getRating());
        commentsViewHolder.date.setText(Utility.getDate(comment.getDate()));
        LayerDrawable stars = (LayerDrawable)  commentsViewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

    }

    public void swapData(List<Comment> comments){
        this.comments = comments;
        notifyDataSetChanged();
    }
    public void setCommentCursor(Cursor cursor){
        if(commentCursor!=null){
            closeCursor();
        }
        this.commentCursor = cursor;
    }
    public void closeCursor(){
        commentCursor.close();
        commentCursor = null;

    }

}
