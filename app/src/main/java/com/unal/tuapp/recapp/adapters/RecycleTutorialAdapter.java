package com.unal.tuapp.recapp.adapters;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.SubCategory;
import com.unal.tuapp.recapp.data.Tutorial;

import java.util.List;

/**
 * Created by andresgutierrez on 7/22/15.
 */
public class RecycleTutorialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static List<Tutorial> tutorials;
    private Cursor tutorialsCursor =null;
    public static OnItemClickListener mItemClickListener;

    public RecycleTutorialAdapter(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTittle;
        private TextView mDescription;
        private TextView mLink;
        private CardView mCardView;

        public TutorialViewHolder(final View itemView){
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.tutorial_card);
            mTittle = (TextView) itemView.findViewById(R.id.tutorial_tittle_item);
            mDescription = (TextView) itemView.findViewById(R.id.tutorial_description_item);
            mLink = (TextView) itemView.findViewById(R.id.tutorial_link_item);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(mItemClickListener!=null){
                long id = getAdapterPosition();
                TextView textView = (TextView) view.findViewById(R.id.tutorial_link_item);
                String link = textView.getText().toString();
                mItemClickListener.onItemClick(view,id, link);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tutorials.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tutorial_item,parent,false);
        viewHolder = new TutorialViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TutorialViewHolder tutorialsViewHolder = (TutorialViewHolder) holder;
        Tutorial tutorial = tutorials.get(position);
        tutorialsViewHolder.mTittle.setText(tutorial.getTittle());
        tutorialsViewHolder.mDescription.setText(tutorial.getDescription());
        tutorialsViewHolder.mLink.setText(tutorial.getLink());
    }

    public interface OnItemClickListener{
        void onItemClick(View view, long position, String link);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }

    public void swapData(List<Tutorial> tutorials){
        this.tutorials = tutorials;
        notifyDataSetChanged();
    }
    public void setTutorialCursor(Cursor cursor){
        if(tutorialsCursor !=null){
            closeCursor();
        }
        this.tutorialsCursor = cursor;
    }
    public void closeCursor(){
        tutorialsCursor.close();
        tutorialsCursor = null;

    }


}