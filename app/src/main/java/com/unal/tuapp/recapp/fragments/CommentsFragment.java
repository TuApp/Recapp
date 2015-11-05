package com.unal.tuapp.recapp.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleCommentsUserAdapter;
import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

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
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
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
                        RecappContract.CommentEntry.COLUMN_PLACE_KEY},
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
