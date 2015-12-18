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
import android.widget.TextView;

import com.unal.tuapp.recapp.activities.NavigationDrawer;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleSubCategoriesAdapter;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.SubCategory;
import com.unal.tuapp.recapp.others.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabianlm17 on 15/10/15.
 */
public class FilterSubCategoriesDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private long idCategory;
    private RecyclerView recyclerView;
    private RecycleSubCategoriesAdapter adapter;
    public static onSubcategoryListener mOnSubcategoryListener;

    private static final int SUBCATEGORIES = 155;

    public FilterSubCategoriesDialogFragment(){

    }

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subcategories_filter, container);
        getDialog().setTitle(R.string.sub_category);
        getDialog().setCancelable(true);
        RecyclerView.LayoutManager linearLayoutManager = Utility.getLayoutManager(getActivity(), getResources().getConfiguration().screenWidthDp);
        recyclerView = (RecyclerView) view.findViewById(R.id.subcategories_recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<SubCategory> subCategories = new ArrayList<>();
        adapter = new RecycleSubCategoriesAdapter(subCategories);
        adapter.setOnItemClickListener(new RecycleSubCategoriesAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, long position) {
                NavigationDrawer callingActivity = (NavigationDrawer) getActivity();
                TextView mtvSubcategory = (TextView) view.findViewById(R.id.category_item);
                String subcategory = mtvSubcategory.getText().toString();
                callingActivity.addItemToMenu(idCategory,position, subcategory);
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        if(getLoaderManager().getLoader(SUBCATEGORIES)==null)
            getLoaderManager().initLoader(SUBCATEGORIES,null,this);
        else
            getLoaderManager().restartLoader(SUBCATEGORIES, null, this);
        return view;
    }

    public interface onSubcategoryListener {
        void onSubcategory(View view, long category, long position);
    }

    public void setOnPlaceListener(final onSubcategoryListener mOnSubcategoryListener){
        this.mOnSubcategoryListener = mOnSubcategoryListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = RecappContract.CategoryEntry.TABLE_NAME+"."+ RecappContract.CategoryEntry._ID;
        selection += " IN ( ? )";
        return new CursorLoader(
                getContext(),
                RecappContract.SubCategoryEntry.buildSubCategoryCategoryUri(),
                new String[]{RecappContract.SubCategoryEntry.TABLE_NAME+"."+ RecappContract.SubCategoryEntry._ID,
                        RecappContract.SubCategoryEntry.TABLE_NAME+"."+ RecappContract.SubCategoryEntry.COLUMN_NAME,
                        RecappContract.CategoryEntry.TABLE_NAME+"."+ RecappContract.CategoryEntry.COLUMN_IMAGE},
                selection,
                new String[]{idCategory+""},
                null

        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<SubCategory> subcategories = SubCategory.allSubCategories(data);
        adapter = new RecycleSubCategoriesAdapter(subcategories);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.closeCursor();
    }
}
