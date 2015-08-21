package com.unal.tuapp.recapp;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public static class RemindersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RelativeLayout layoutReminder;
        private TextView nameReminder;
        private TextView descriptionReminder;
        private TextView dateReminder;
        private RelativeLayout layoutPlace;
        private ImageView imagePlace;
        private TextView namePlace;
        private TextView addressPlace;
        private ImageView deleteReminder;

        public RemindersViewHolder(View itemView){
            super(itemView);
            layoutReminder = (RelativeLayout)itemView.findViewById(R.id.layout_reminder);
            deleteReminder = (ImageView) itemView.findViewById(R.id.delete_reminder);
            nameReminder = (TextView) layoutReminder.findViewById(R.id.name_reminder);
            descriptionReminder = (TextView) layoutReminder.findViewById(R.id.description_reminder);
            dateReminder = (TextView) layoutReminder.findViewById(R.id.date_reminder);
            layoutPlace = (RelativeLayout) layoutReminder.findViewById(R.id.layout_reminder_place);
            imagePlace = (ImageView) layoutPlace.findViewById(R.id.image_place);
            namePlace = (TextView) layoutPlace.findViewById(R.id.name_place);
            addressPlace = (TextView) layoutPlace.findViewById(R.id.address_place);
            deleteReminder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mOnItemClickListener!=null){
                long id = reminders.get(getAdapterPosition()).getId();
                mOnItemClickListener.onItemClick(id);
            }
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
        Date now = new Date();
        if(reminder.getEndDate()<now.getTime()){
            remindersViewHolder.deleteReminder.setVisibility(View.VISIBLE);
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
