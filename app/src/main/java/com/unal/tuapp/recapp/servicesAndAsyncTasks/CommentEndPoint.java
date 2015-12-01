package com.unal.tuapp.recapp.servicesAndAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.unal.tuapp.recapp.backend.model.commentApi.model.CollectionResponseComment;
import com.unal.tuapp.recapp.backend.model.commentApi.model.Comment;
import com.unal.tuapp.recapp.backend.model.commentApi.model.CommentCollection;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.CommentsFragment;
import com.unal.tuapp.recapp.fragments.CompanyCommentsFragment;
import com.unal.tuapp.recapp.others.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class CommentEndPoint extends AsyncTask<Pair<Pair<Context,Pair<Long,Long>>,Pair<Comment,String>>,Void,Void> {
    private boolean swipe;

    public CommentEndPoint() {
    }

    public CommentEndPoint(boolean swipe) {
        this.swipe = swipe;
    }

    @Override
    protected Void doInBackground(Pair<Pair<Context, Pair<Long, Long>>, Pair<Comment, String>>... pairs) {
        try {
            switch (pairs[0].second.second){
                case "addComment":
                    Utility.getCommentApi().insert(pairs[0].second.first).execute();
                    break;
                case "deleteComment":
                    Utility.getCommentApi().remove(pairs[0].second.first.getId()).execute();
                    break;
                case "getComments":
                    CollectionResponseComment collectionResponseComment = Utility.getCommentApi().list().execute();
                    List<Comment> commentListAll;
                    String nextPage = "";
                    List<String> idsAll = new ArrayList<>();
                    String queryAll = RecappContract.CommentEntry._ID + " NOT IN ( ";
                    if(collectionResponseComment.getNextPageToken()!=null) {
                        while (!collectionResponseComment.getNextPageToken().equals(nextPage)) {
                            commentListAll = collectionResponseComment.getItems();
                            if (commentListAll != null) {
                                List<ContentValues> valuesList = new ArrayList<>();
                                for (Comment i : commentListAll) {
                                    queryAll += "?,";
                                    idsAll.add(i.getId() + "");
                                    ContentValues value = new ContentValues();
                                    value.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, i.getDescription());
                                    value.put(RecappContract.CommentEntry.COLUMN_DATE, i.getDate());
                                    value.put(RecappContract.CommentEntry.COLUMN_RATING, i.getRating());
                                    value.put(RecappContract.CommentEntry._ID, i.getId());
                                    value.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                                    value.put(RecappContract.CommentEntry.COLUMN_USER_KEY, i.getUserId());
                                    valuesList.add(value);
                                }
                                ContentValues values[] = new ContentValues[valuesList.size()];
                                valuesList.toArray(values);
                                pairs[0].first.first.getContentResolver().bulkInsert(
                                        RecappContract.CommentEntry.CONTENT_URI,
                                        values
                                );
                                nextPage = collectionResponseComment.getNextPageToken();
                                collectionResponseComment = Utility.getCommentApi().list().setCursor(nextPage).execute();

                            }
                        }
                        String idsListAll[] = new String[idsAll.size()];
                        queryAll = queryAll.substring(0, queryAll.length() - 1);
                        queryAll += ")";
                        idsAll.toArray(idsListAll);
                        if (idsAll.isEmpty()) {
                            queryAll = null;
                            idsListAll = null;
                        }
                        pairs[0].first.first.getContentResolver().delete(
                                RecappContract.CommentEntry.CONTENT_URI,
                                queryAll,
                                idsListAll
                        );
                    }
                    break;
                case "commentUser":
                    CommentCollection commentCollection = Utility.getCommentApi().listUser(pairs[0].first.second.first).execute();
                    List<Comment> commentList = commentCollection.getItems();
                    if(commentList!=null) {
                        List<ContentValues> contentValuesList = new ArrayList<>();
                        List<String> ids = new ArrayList<>();
                        String query = RecappContract.CommentEntry._ID + " NOT IN ( ";
                        for(Comment i: commentList){
                            query+="?,";
                            ids.add(""+i.getId());
                        }
                        query = query.substring(0,query.length()-1);
                        query +=" )";
                        if(!ids.isEmpty()) {
                            String idsList [] = new String[ids.size()];
                            ids.toArray(idsList);
                            pairs[0].first.first.getContentResolver().delete(
                                    RecappContract.CommentEntry.CONTENT_URI,
                                    query,
                                    idsList
                            );
                        }
                        for (Comment i : commentList) {
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, i.getDescription());
                            value.put(RecappContract.CommentEntry.COLUMN_DATE, i.getDate());
                            value.put(RecappContract.CommentEntry.COLUMN_RATING, i.getRating());
                            value.put(RecappContract.CommentEntry._ID, i.getId());
                            value.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                            value.put(RecappContract.CommentEntry.COLUMN_USER_KEY, i.getUserId());
                            contentValuesList.add(value);
                        }
                        ContentValues values[] = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(values);
                        pairs[0].first.first.getContentResolver().bulkInsert(
                                RecappContract.CommentEntry.CONTENT_URI,
                                values
                        );
                    }
                    break;
                case "commentPlace":
                    CommentCollection commentCollectionPlace = Utility.getCommentApi().listPlace(pairs[0].first.second.second).execute();
                    List<Comment> commentListPlace = commentCollectionPlace.getItems();
                    List<ContentValues> contentValuesListPlace = new ArrayList<>();

                    if(commentListPlace!=null) {
                        List<String> ids = new ArrayList<>();
                        String query = RecappContract.CommentEntry._ID + " NOT IN ( ";
                        for(Comment i: commentListPlace){
                            query+="?,";
                            ids.add(""+i.getId());
                        }
                        query = query.substring(0,query.length()-1);
                        query +=" )";
                        if(!ids.isEmpty()) {
                            String idsList [] = new String[ids.size()];
                            ids.toArray(idsList);
                            pairs[0].first.first.getContentResolver().delete(
                                    RecappContract.CommentEntry.CONTENT_URI,
                                    query,
                                    idsList
                            );
                        }
                        for (Comment i : commentListPlace) {
                            ContentValues value = new ContentValues();
                            value.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, i.getDescription());
                            value.put(RecappContract.CommentEntry.COLUMN_DATE, i.getDate());
                            value.put(RecappContract.CommentEntry.COLUMN_RATING, i.getRating());
                            value.put(RecappContract.CommentEntry._ID, i.getId());
                            value.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, i.getPlaceId());
                            value.put(RecappContract.CommentEntry.COLUMN_USER_KEY, i.getUserId());
                            contentValuesListPlace.add(value);
                        }
                        ContentValues valuesPlace[] = new ContentValues[contentValuesListPlace.size()];
                        contentValuesListPlace.toArray(valuesPlace);
                        pairs[0].first.first.getContentResolver().bulkInsert(
                                RecappContract.CommentEntry.CONTENT_URI,
                                valuesPlace
                        );
                    }
                    break;

            }

        }catch (IOException e){
            Log.e("error",e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(swipe){
            CommentsFragment.mySwipeRefresh.setRefreshing(false);
            CompanyCommentsFragment.class.getSimpleName();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
