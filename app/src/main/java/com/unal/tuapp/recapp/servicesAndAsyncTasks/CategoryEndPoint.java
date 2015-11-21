package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.categoryApi.model.Category;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.CollectionResponseCategory;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class CategoryEndPoint extends AsyncTask<Pair<Context,Pair<Category,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<Category, String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "getCategories":
                    CollectionResponseCategory collectionResponseCategory = Utility.getCategoryApi().list().execute();
                    List<Category> categoryList ;
                    String nextPage = "";
                    if(collectionResponseCategory.getNextPageToken()!=null) {
                        while (!collectionResponseCategory.getNextPageToken().equals(nextPage)) {
                            categoryList = collectionResponseCategory.getItems();
                            if (categoryList != null) {
                                List<ContentValues> valuesList = new ArrayList<>();
                                for (Category i : categoryList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.CategoryEntry._ID, i.getId());
                                    value.put(RecappContract.CategoryEntry.COLUMN_NAME, i.getName());
                                    value.put(RecappContract.CategoryEntry.COLUMN_IMAGE, Utility.decodeImage(i.getImage()));
                                    valuesList.add(value);
                                }
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.CategoryEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseCategory.getNextPageToken();
                                collectionResponseCategory = Utility.getCategoryApi().list().setCursor(nextPage).execute();
                            }
                        }
                    }
                    break;
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
