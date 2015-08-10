package com.unal.tuapp.recapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInRightAnimationAdapter;

/**
 * Created by andresgutierrez on 8/8/15.
 */
public class CommentsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    private User user;
    private View root;
    private RecyclerView recyclerView;
    private RecycleCommentsAdapter recycleCommentsAdapter;
    private static final int COMMENT = 20;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        root = inflater.inflate(R.layout.fragment_comments,container,false);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
        }
        recyclerView = (RecyclerView) root.findViewById(R.id.user_comments);
        List<Comment> comments = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recycleCommentsAdapter = new RecycleCommentsAdapter(comments);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(recycleCommentsAdapter);
        alphaInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter(new SlideInRightAnimationAdapter(alphaInAnimationAdapter));

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
                null,
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

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //List<Comment> comments = new ArrayList<>();
        //recycleCommentsAdapter.swapData(comments);
        recycleCommentsAdapter.closeCursor();
    }
}
