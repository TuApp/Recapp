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

import com.unal.tuapp.recapp.data.SubCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 8/29/15.
 */
public class LimitArrayAdapterSubCategory extends BaseAdapter implements Filterable {
    private final int LIMIT = 4;
    private Filter filter;
    private Context context;
    private List<SubCategory> data;
    private List<SubCategory> allData;

    public LimitArrayAdapterSubCategory(Context context,List<SubCategory> data){
        this.context = context;
        this.allData = data;
        this.data = data;
    }

    @Override
    public Object getItem(int i){
        return data.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }
    @Override
    public int getCount() {
        return Math.min(LIMIT, data.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SubCategoryHolder holder = null;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.category,parent,false);
            holder = new SubCategoryHolder();
            holder.icon = (ImageView) row.findViewById(R.id.category_icon);
            holder.name = (TextView) row.findViewById(R.id.category_name);
            row.setTag(holder);

        }else{
            holder = (SubCategoryHolder) row.getTag();
        }
        holder.name.setText(data.get(position).getName());
        holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(
                data.get(position).getImage(), 0, data.get(position).getImage().length
        ));
        return row;

    }
    static class SubCategoryHolder{
        ImageView icon;
        TextView name;
    }

    @Override
    public Filter getFilter(){
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String subStr = charSequence.toString().toLowerCase();
                if(subStr!=null && subStr.length()==0){
                    filterResults.values = data;
                    filterResults.count = data.size();
                }else{
                    final List<SubCategory> retList = new ArrayList<>();
                    for (SubCategory subCategory:allData){
                        if(subCategory.getName().toLowerCase().contains(subStr)){
                            retList.add(subCategory);
                        }
                    }
                    filterResults.values = retList;
                    filterResults.count = retList.size();

                }
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults!=null && filterResults.count>0){
                    data = (List<SubCategory>) filterResults.values;
                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }

            }

        };
        return filter;

    }
    public void setData(List<SubCategory> newData){
        allData = newData;
        notifyDataSetChanged();
    }
}
