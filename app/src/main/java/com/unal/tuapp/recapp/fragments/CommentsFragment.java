package com.unal.tuapp.recapp.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Recapp;
import com.unal.tuapp.recapp.adapters.RecycleCommentsUserAdapter;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CommentEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by andresgutierrez on 8/8/15.
 */
public class CommentsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private User user;
    private View root;
    private RecyclerView recyclerView;
    public RecycleCommentsUserAdapter recycleCommentsAdapter;
    private static final int COMMENT = 20;
    private ActionMode actionMode;
    private long idComment;
    private long idPlaceComment;
    private OnCommentListener onCommentListener;
    public static SwipeRefreshLayout mySwipeRefresh;

    public interface OnCommentListener{
         void onCommentDelete(boolean comment);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_context,menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.delete_comment:
                    deleteComment();
                    mode.finish();
            }
            return false;
        }



        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            recycleCommentsAdapter.setCommentPositon(-1);
            recycleCommentsAdapter.notifyDataSetChanged();

        }


    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_comments,container,false);
        mySwipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.comments_refresh);
        mySwipeRefresh.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                com.unal.tuapp.recapp.backend.model.commentApi.model.Comment comment = new com.unal.tuapp.recapp.backend.model.commentApi.model.Comment();
                Pair<Pair<Context, Pair<Long, Long>>, Pair<com.unal.tuapp.recapp.backend.model.commentApi.model.Comment, String>> pairComment = new Pair<>(new Pair<>(getContext(), new Pair<>(-1L, -1L)),
                        new Pair<>(comment, "getComments"));
                new CommentEndPoint(true).execute(pairComment);
            }
        });
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
        /*Pair<Pair<Context,Pair<Long,Long>>,Pair<com.unal.tuapp.recapp.backend.model.commentApi.model.Comment,String>> pair =
                new Pair<>(new Pair<>(getContext(),new Pair<>(user.getId(),-1L)),new Pair<>(new com.unal.tuapp.recapp.backend.model.commentApi.model.Comment(),"commentUser"));
        new CommentEndPoint().execute(pair);*/
        recyclerView = (RecyclerView) root.findViewById(R.id.user_comments);
        List<Comment> comments = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recycleCommentsAdapter = new RecycleCommentsUserAdapter(comments);
        recycleCommentsAdapter.setOnItemClickListener(new RecycleCommentsUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long id,long idPlace) {

                idComment = id;
                idPlaceComment = idPlace;
                if(actionMode==null){
                    actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
                }/*else{
                    actionMode.finish();
                    actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);

                }*/
                recycleCommentsAdapter.notifyDataSetChanged();
            }
        });
        recycleCommentsAdapter.setCommentPositon(-1);
        recycleCommentsAdapter.notifyDataSetChanged();

        //AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(recycleCommentsAdapter);
        //alphaInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter(recycleCommentsAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(COMMENT)==null){
            getLoaderManager().initLoader(COMMENT,null,this);
        }else{
            getLoaderManager().restartLoader(COMMENT,null,this);
        }
        //getLoaderManager().initLoader(COMMENT, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecappContract.CommentEntry.COLUMN_DATE + " DESC ";
        return new CursorLoader(
                getActivity(),
                RecappContract.CommentEntry.buildCommentUserUri(user.getId()),
                new String[]{RecappContract.CommentEntry.TABLE_NAME+"."+RecappContract.CommentEntry._ID,
                        RecappContract.CommentEntry.TABLE_NAME+"."+ RecappContract.CommentEntry.COLUMN_DATE,
                        RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_DESCRIPTION,
                        RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_RATING,
                        RecappContract.UserEntry.COLUMN_USER_IMAGE,
                        RecappContract.CommentEntry.COLUMN_PLACE_KEY,
                        RecappContract.CommentEntry.COLUMN_USER_KEY},
                null,
                null,
                sortOrder
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Comment> comments = Comment.allComment(data);
        recycleCommentsAdapter.swapData(comments);
        recycleCommentsAdapter.setCommentCursor(data);
        //recyclerView.setAdapter(recycleCommentsAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recycleCommentsAdapter.closeCursor();
    }

    public void deleteComment(){
        getActivity().getContentResolver().delete(
                RecappContract.CommentEntry.CONTENT_URI,
                RecappContract.CommentEntry._ID + " = ?",
                new String[]{"" + idComment}
        );
        com.unal.tuapp.recapp.backend.model.commentApi.model.Comment commentApi = new com.unal.tuapp.recapp.backend.model.commentApi.model.Comment();
        commentApi.setId(idComment);
        Pair<Pair<Context,Pair<Long,Long>>,Pair<com.unal.tuapp.recapp.backend.model.commentApi.model.Comment,String>> pair =
                new Pair<>(new Pair<>(getContext(),new Pair<>(-1L,-1L)),new Pair<>(commentApi,"deleteComment"));
        new CommentEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pair);
        Cursor cursorRating = getActivity().getContentResolver().query(
                RecappContract.CommentEntry.buildCommentPlaceUri(idPlaceComment),
                new String[]{"AVG(" + RecappContract.CommentEntry.TABLE_NAME + "."
                        + RecappContract.CommentEntry.COLUMN_RATING + ")"},
                RecappContract.PlaceEntry.TABLE_NAME + "." + RecappContract.PlaceEntry._ID,
                null,
                null
        );
        double newRating = 0;
        if(cursorRating.moveToFirst()) {
            newRating = cursorRating.getDouble(0);
        }
        Place place = new Place();
        place.setRating((float)newRating);
        place.setId(idPlaceComment);
        Pair<Context,Pair<Place,String>> pairPlace = new Pair<>(getContext(),new Pair<>(place,"updatePlaceRating"));
        new PlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairPlace);
        ContentValues values = new ContentValues();
        values.put(RecappContract.PlaceEntry.COLUMN_RATING, newRating);
        getActivity().getContentResolver().update(
                RecappContract.PlaceEntry.CONTENT_URI,
                values,
                RecappContract.PlaceEntry._ID + " = ?",
                new String[]{"" + idPlaceComment}
        );

        onCommentListener.onCommentDelete(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onCommentListener = (OnCommentListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }
}
