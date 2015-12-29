package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.tutorialApi.model.CollectionResponseTutorial;
import com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.TutorialFragment;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class TutorialEndPoint extends AsyncTask<Pair<Context,Pair<Tutorial,String>>,Void,Void> {
    private boolean swipe;

    public TutorialEndPoint() {
    }

    public TutorialEndPoint(boolean swipe) {
        this.swipe = swipe;
    }

    @Override
    protected Void doInBackground(Pair<Context, Pair<Tutorial, String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "getTutorials":
                    CollectionResponseTutorial collectionResponseTutorial = Utility.getTutorialApi().list().execute();
                    List<Tutorial> tutorialList ;
                    String nextPage = "";
                    if(collectionResponseTutorial.getNextPageToken()!=null) {
                        while (!collectionResponseTutorial.getNextPageToken().equals(nextPage)) {
                            tutorialList = collectionResponseTutorial.getItems();
                            List<ContentValues> valuesList = new ArrayList<>();
                            if (tutorialList != null) {
                                for (Tutorial i : tutorialList) {
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.TutorialEntry._ID, i.getId());
                                    value.put(RecappContract.TutorialEntry.COLUMN_NAME, i.getName());
                                    value.put(RecappContract.TutorialEntry.COLUMN_DESCRIPTION, i.getDescription());
                                    value.put(RecappContract.TutorialEntry.COLUMN_LINK_VIDEO, i.getLink());
                                    value.put(RecappContract.COLUMN_IS_SEND,1);
                                    valuesList.add(value);
                                }
                                nextPage = collectionResponseTutorial.getNextPageToken();
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.getContentResolver().bulkInsert(
                                        RecappContract.TutorialEntry.CONTENT_URI,
                                        values
                                );
                                collectionResponseTutorial = Utility.getTutorialApi().list().setCursor(nextPage).execute();
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
        if(swipe){
            if(TutorialFragment.mySwipeRefresh!=null)
                TutorialFragment.mySwipeRefresh.setRefreshing(false);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
