package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleCommentsUserAdapter;
import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CommentEndPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/5/15.
 */
public class CompanyCommentsFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View root;
    private RecyclerView companyComments;
    private RecycleCommentsUserAdapter companyCommentsAdapter;
    private final int PLACE_COMMENT = 1376;
    public static SwipeRefreshLayout mySwipeRefresh;
    private long id;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_comment_company,container,false);
        if(getActivity().getIntent().getExtras()!=null){
            id = getActivity().getIntent().getExtras().getLong("id");
        }
        mySwipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.company_comments_refresh);
        mySwipeRefresh.setColorSchemeResources(
                R.color.blue,       //This method will rotate
                R.color.red,        //colors given to it when
                R.color.yellow,     //loader continues to
                R.color.green);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Utility.isNetworkAvailable(getContext())) {
                    com.unal.tuapp.recapp.backend.model.commentApi.model.Comment comment = new com.unal.tuapp.recapp.backend.model.commentApi.model.Comment();
                    Pair<Pair<Context, Pair<Long, Long>>, Pair<com.unal.tuapp.recapp.backend.model.commentApi.model.Comment, String>> pairComment = new Pair<>(new Pair<>(getContext(), new Pair<>(-1L, -1L)),
                            new Pair<>(comment, "getComments"));
                    new CommentEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairComment);
                }else{
                    mySwipeRefresh.setRefreshing(false);
                }
            }
        });
        companyComments = (RecyclerView) root.findViewById(R.id.company_comments);
        companyComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyCommentsAdapter = new RecycleCommentsUserAdapter(new ArrayList<Comment>(),getContext());
        companyCommentsAdapter.setCommentPositon(-1);
        companyComments.setAdapter(companyCommentsAdapter);
        if(getLoaderManager().getLoader(PLACE_COMMENT)==null){
            getLoaderManager().initLoader(PLACE_COMMENT,null,this);
        }else{
            getLoaderManager().restartLoader(PLACE_COMMENT,null,this);
        }
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.CommentEntry.buildCommentPlaceUserUri(this.id),
                null,
                null,
                null,
                RecappContract.CommentEntry.COLUMN_DATE + " DESC "
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Comment> comments = Comment.allComment(data);
        companyCommentsAdapter.setCommentCursor(data);
        companyCommentsAdapter.swapData(comments);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        companyCommentsAdapter.closeCursor();
    }
}
