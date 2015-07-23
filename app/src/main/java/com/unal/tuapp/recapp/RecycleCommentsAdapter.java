package com.unal.tuapp.recapp;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andresgutierrez on 7/22/15.
 */
public class RecycleCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Comment> comments;

    public RecycleCommentsAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        private TextView comment;
        private RatingBar ratingBar;
        private de.hdodenhof.circleimageview.CircleImageView imageView;

        public CommentsViewHolder(View itemView){
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.comment_text);
            ratingBar = (RatingBar) itemView.findViewById(R.id.comment_rating);
            imageView = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.comment_foto);
        }
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
        if(comment.getImageProfile()!=null){
            commentsViewHolder.imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(comment.getImageProfile(),0,0));
        }else {
            commentsViewHolder.imageView.setImageResource(R.drawable.background_material);
        }
        //We need to change the value, dynamically
        commentsViewHolder.ratingBar.setRating(comment.getRating());
        LayerDrawable stars = (LayerDrawable)  commentsViewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);







    }


}
