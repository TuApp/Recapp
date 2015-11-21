package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.CollectionResponseSubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.SubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.SubCategoryCollection;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class SubCategoryEndPoint extends AsyncTask<Pair<Context,Pair<SubCategory,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<SubCategory, String>>... pairs) {
        try{
            switch (pairs[0].second.second){
                case "getSubCategories":
                    CollectionResponseSubCategory collectionResponseSubCategory = Utility.getSubCategoryApi().list().execute();
                    List<SubCategory> subCategoryList ;

                    String nextPage = "";
                    if(collectionResponseSubCategory.getNextPageToken()!=null) {
                        while (!collectionResponseSubCategory.getNextPageToken().equals(nextPage)) {
                            subCategoryList = collectionResponseSubCategory.getItems();
                            List<ContentValues> valuesList = new ArrayList<>();
                            if (subCategoryList != null) {
                                for (SubCategory i : subCategoryList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.SubCategoryEntry._ID, i.getId());
                                    value.put(RecappContract.SubCategoryEntry.COLUMN_NAME, i.getName());
                                    value.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY, i.getCategoryId());
                                    valuesList.add(value);
                                }
                                nextPage = collectionResponseSubCategory.getNextPageToken();
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.SubCategoryEntry.CONTENT_URI,
                                        values
                                );
                                collectionResponseSubCategory = Utility.getSubCategoryApi().list().setCursor(nextPage).execute();
                            }
                        }
                    }

                    break;
                case "getSubCategoriesCategory":
                    SubCategoryCollection collectionResponseSubCategoryCategory = Utility.getSubCategoryApi().listCategory(
                            pairs[0].second.first.getCategoryId()
                    ).execute();
                    List<SubCategory> subCategoryListCategory = collectionResponseSubCategoryCategory.getItems();
                    if(subCategoryListCategory!=null){
                        List<ContentValues> valuesList = new ArrayList<>();
                        for (SubCategory i : subCategoryListCategory){
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.SubCategoryEntry._ID,i.getId());
                            value.put(RecappContract.SubCategoryEntry.COLUMN_NAME,i.getName());
                            value.put(RecappContract.SubCategoryEntry.COLUMN_CATEGORY_KEY,i.getCategoryId());
                            valuesList.add(value);
                        }
                        ContentValues values [] = new ContentValues[valuesList.size()];
                        valuesList.toArray(values);
                        pairs[0].first.getContentResolver().bulkInsert(
                                RecappContract.SubCategoryEntry.CONTENT_URI,
                                values
                        );
                    }
            }

        }catch (IOException e){

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
