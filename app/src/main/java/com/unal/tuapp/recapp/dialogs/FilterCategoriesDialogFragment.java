package com.unal.tuapp.recapp.dialogs;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.activities.NavigationDrawer;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleCategoriesAdapter;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.Category;

import java.util.ArrayList;
import java.util.List;

public class FilterCategoriesDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView recyclerView;
    private RecycleCategoriesAdapter adapter;
    public static onCategoryListener mOnCategoryListener;

    private static final int CATEGORIES = 165;

    public FilterCategoriesDialogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_filter, container);
        getDialog().setTitle(R.string.category);
        getDialog().setCancelable(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.categories_recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Category> categories = new ArrayList<>();
        adapter = new RecycleCategoriesAdapter(categories);
        adapter.setOnItemClickListener(new RecycleCategoriesAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, long position) {

                NavigationDrawer callingActivity = (NavigationDrawer) getActivity();
                callingActivity.showSubCategoriesDialog(position);
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        if(getLoaderManager().getLoader(CATEGORIES)==null)
            getLoaderManager().initLoader(CATEGORIES,null,this);
        else
            getLoaderManager().restartLoader(CATEGORIES, null, this);
        return view;
    }

    public interface onCategoryListener {
        void onCategory(View view, long category, long position);
    }

    public void setOnPlaceListener(final onCategoryListener mOnCategoryListener){
        this.mOnCategoryListener = mOnCategoryListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                RecappContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Category> categories = Category.allCategories(data);
        adapter.swapData(categories);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.closeCursor();
    }
}
