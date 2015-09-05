package com.unal.tuapp.recapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 8/29/15.
 */
public class LimitArrayAdapterCategory extends BaseAdapter implements Filterable{
    private Filter filter;
    private final int LIMIT = 4;
    private Context context;
    private List<Category> data;
    private List<Category> allData;

    public LimitArrayAdapterCategory(Context context, List<Category> categories) {
        this.context = context;
        allData = categories;
        data = categories;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }



    @Override
    public int getCount() {
        return Math.min(LIMIT,data.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.category,parent,false);
            holder = new CategoryHolder();
            holder.icon = (ImageView) row.findViewById(R.id.category_icon);
            holder.name = (TextView) row.findViewById(R.id.category_name);
            row.setTag(holder);

        }else{
            holder = (CategoryHolder) row.getTag();
        }
        holder.name.setText(data.get(position).getName());
        holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(
                data.get(position).getImage(),0,data.get(position).getImage().length
        ));
        return row;

    }


    @Override
    public Filter getFilter(){
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String subStr = charSequence.toString().toLowerCase();
                if(subStr==null || subStr.length()==0){
                    results.values = data;
                    results.count = data.size();
                }else{
                    final List<Category> retList = new ArrayList<>();
                    for(Category category: allData){

                        if(category.getName().toLowerCase().contains(subStr)){
                            retList.add(category);
                        }
                    }
                    results.values = retList;
                    results.count = retList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.count>0){
                    data = (List<Category>) filterResults.values;
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
    static class CategoryHolder{
        ImageView icon;
        TextView name;
    }
    public void setData(List<Category> newData){
        allData = newData;
        notifyDataSetChanged();
    }

}
