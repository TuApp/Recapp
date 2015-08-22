package com.unal.tuapp.recapp;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.Reminder;

import java.util.Date;
import java.util.List;

/**
 * Created by andresgutierrez on 8/9/15.
 */
public class RecycleRemindersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static OnItemClickListener mOnItemClickListener;
    private static List<Reminder> reminders;
    private Cursor reminderCursor;

    public interface OnItemClickListener{
        void onItemClick(long reminderId);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public RecycleRemindersAdapter(List<Reminder> reminders){
        this.reminders = reminders;

    }

    public static class RemindersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        private CardView front;
        private CardView back;
        private RelativeLayout layoutReminder;
        private TextView nameReminder;
        private TextView descriptionReminder;
        private TextView dateReminder;
        private RelativeLayout layoutPlace;
        private ImageView imagePlace;
        private TextView namePlace;
        private TextView addressPlace;
        private ImageView deleteReminder;
        private ImageView deleteReminderPlace;
        private RatingBar placeRating;

        public RemindersViewHolder(View itemView){
            super(itemView);
            front = (CardView) itemView.findViewById(R.id.reminder_layout);
            back = (CardView) itemView.findViewById(R.id.reminder_place_layout);
            //Layout for the front of the card
            layoutReminder = (RelativeLayout)front.findViewById(R.id.layout_reminder);
            deleteReminder = (ImageView) layoutReminder.findViewById(R.id.delete_reminder);
            nameReminder = (TextView) layoutReminder.findViewById(R.id.name_reminder);
            descriptionReminder = (TextView) layoutReminder.findViewById(R.id.description_reminder);
            dateReminder = (TextView) layoutReminder.findViewById(R.id.date_reminder);

            //Layout for the back of the card
            layoutPlace = (RelativeLayout) back.findViewById(R.id.layout_place);
            imagePlace = (ImageView) layoutPlace.findViewById(R.id.image_place);
            namePlace = (TextView) layoutPlace.findViewById(R.id.name_place);
            addressPlace = (TextView) layoutPlace.findViewById(R.id.address_place);
            deleteReminderPlace = (ImageView) layoutPlace.findViewById(R.id.delete_reminder_back);
            placeRating = (RatingBar) layoutPlace.findViewById(R.id.rating_place);

            deleteReminder.setOnClickListener(this);
            deleteReminderPlace.setOnClickListener(this);
            front.setOnLongClickListener(this);
            back.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(mOnItemClickListener!=null){
                long id = reminders.get(getAdapterPosition()).getId();
                mOnItemClickListener.onItemClick(id);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(view.getId()==R.id.reminder_layout){//Front
                front.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }else{
                front.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
            return true;
        }
    }
    @Override
    public int getItemCount(){
        return reminders.size();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reminder_item,parent,false);
        viewHolder = new RemindersViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,int position){
        RemindersViewHolder remindersViewHolder = (RemindersViewHolder) viewHolder;
        Reminder reminder = reminders.get(position);
        remindersViewHolder.nameReminder.setText(reminder.getName());
        remindersViewHolder.descriptionReminder.setText(reminder.getDescription());
        remindersViewHolder.dateReminder.setText("" + Utility.getDate(reminder.getEndDate()));
        remindersViewHolder.imagePlace.setImageBitmap(BitmapFactory.decodeByteArray(
                reminder.getPlace().getImageFavorite(), 0, reminder.getPlace().getImageFavorite().length
        ));
        remindersViewHolder.namePlace.setText(reminder.getPlace().getName());
        remindersViewHolder.addressPlace.setText(reminder.getPlace().getAddress());
        remindersViewHolder.placeRating.setRating((float) reminder.getPlace().getRating());
        LayerDrawable layerDrawable =(LayerDrawable) remindersViewHolder.placeRating.getProgressDrawable();
        layerDrawable.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
        layerDrawable.getDrawable(1).setColorFilter(Color.LTGRAY,PorterDuff.Mode.SRC_ATOP);
        layerDrawable.getDrawable(2).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        Date now = new Date();
        if(reminder.getEndDate()<now.getTime()){
            remindersViewHolder.deleteReminder.setVisibility(View.VISIBLE);
            remindersViewHolder.deleteReminderPlace.setVisibility(View.VISIBLE);
        }

    }
    public void swapDate(List<Reminder> reminders){
        this.reminders = reminders;
        notifyDataSetChanged();
    }
    public void setReminderCursor(Cursor cursor){
        this.reminderCursor = cursor;
    }
    public void closeCursor(){
        this.reminderCursor.close();
    }
}
