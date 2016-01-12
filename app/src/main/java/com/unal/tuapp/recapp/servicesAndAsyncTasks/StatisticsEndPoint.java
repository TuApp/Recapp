package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.statisticsApi.model.CollectionResponseStatistics;
import com.unal.tuapp.recapp.backend.model.statisticsApi.model.Statistics;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 12/29/15.
 */
public class StatisticsEndPoint extends AsyncTask<Pair<Context,Pair<Statistics,String>>,Void,Void> {
    private Activity activity;
    private ProgressDialog progressDialog;

    public StatisticsEndPoint() {
    }

    public StatisticsEndPoint(Activity activity) {
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity!=null){
            progressDialog.setMessage("We are uploading the user's information");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(activity!=null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected Void doInBackground(Pair<Context, Pair<Statistics, String>>... pairs) {
        switch (pairs[0].second.second){
            case "getStatistics":
                try {
                    CollectionResponseStatistics collectionResponseStatistics =
                            Utility.getStatisticsApi().list().execute();
                    List<Statistics> statisticsList;
                    List<String> ids = new ArrayList<>();
                    String query = RecappContract.StatisticsEntry._ID + " NOT IN ( ";
                    List<ContentValues> valuesList = new ArrayList<>();

                    String nextPage = "";

                    if (collectionResponseStatistics.getNextPageToken() != null) {
                        while (!collectionResponseStatistics.getNextPageToken().equals(nextPage)) {
                            statisticsList = collectionResponseStatistics.getItems();
                            if (statisticsList != null) {
                                for (Statistics i : statisticsList) {
                                    ids.add(i.getId() + "");
                                    query += "?,";
                                    ContentValues values = new ContentValues();
                                    values.put(RecappContract.COLUMN_IS_SEND, 1);
                                    values.put(RecappContract.StatisticsEntry._ID, i.getId());
                                    values.put(RecappContract.StatisticsEntry.COLUMN_KEY_USER, i.getUserId());
                                    values.put(RecappContract.StatisticsEntry.COLUMN_DATE, i.getDate());
                                    values.put(RecappContract.StatisticsEntry.COLUMN_POINT, i.getPoints());
                                    valuesList.add(values);
                                }
                                nextPage = collectionResponseStatistics.getNextPageToken();
                                collectionResponseStatistics = Utility.getStatisticsApi().list()
                                        .setCursor(nextPage).execute();

                            }

                            ContentValues values[] = new ContentValues[valuesList.size()];
                            valuesList.toArray(values);
                            pairs[0].first.getContentResolver().bulkInsert(
                                    RecappContract.StatisticsEntry.CONTENT_URI,
                                    values
                            );

                        }
                    }

                }catch(IOException e){}
                break;
            case "addStatistics":
                try {
                    Utility.getStatisticsApi().insert(pairs[0].second.first).execute();
                }catch (IOException e){}
                break;
        }
        return null;
    }
}
