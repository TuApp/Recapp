package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.CollectionResponseSubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class SubCategoryByTutorialEndPoint extends AsyncTask<Pair<Context,Pair<SubCategoryByTutorial,String>>,Void,Void> {
    @Override
    protected Void doInBackground(Pair<Context, Pair<SubCategoryByTutorial, String>>... pairs) {
        try {
            switch (pairs[0].second.second) {
                case "getSubCategoryByTutorial":
                    CollectionResponseSubCategoryByTutorial collectionResponseSubCategoryByTutorial =
                            Utility.getSubCategoryByTutorialApi().list().execute();
                    List<SubCategoryByTutorial> subCategoryByTutorialList ;
                    String nextPage = "";
                    if(collectionResponseSubCategoryByTutorial.getNextPageToken()!=null) {
                        while (!collectionResponseSubCategoryByTutorial.getNextPageToken().equals(nextPage)) {
                            subCategoryByTutorialList = collectionResponseSubCategoryByTutorial.getItems();
                            List<ContentValues> valuesList = new ArrayList<>();
                            if (subCategoryByTutorialList != null) {
                                for (SubCategoryByTutorial i : subCategoryByTutorialList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.SubCategoryByTutorialEntry._ID, i.getId());
                                    value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_SUBCATEGORY_KEY, i.getSubCategoryId());
                                    value.put(RecappContract.SubCategoryByTutorialEntry.COLUMN_TUTORIAL_KEY, i.getTutorialId());
                                    valuesList.add(value);
                                }
                                ContentValues values[] = new ContentValues[subCategoryByTutorialList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.SubCategoryByTutorialEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseSubCategoryByTutorial.getNextPageToken();
                                collectionResponseSubCategoryByTutorial = Utility.getSubCategoryByTutorialApi().list()
                                        .setCursor(nextPage).execute();
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
