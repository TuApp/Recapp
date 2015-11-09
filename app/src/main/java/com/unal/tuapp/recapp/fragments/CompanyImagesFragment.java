package com.unal.tuapp.recapp.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleImagesCompanyAdapter;
import com.unal.tuapp.recapp.data.PlaceImages;
import com.unal.tuapp.recapp.data.RecappContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/8/15.
 */
public class CompanyImagesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View root;
    private RecyclerView companyImages;
    private RecycleImagesCompanyAdapter companyImagesAdapter;
    private final int COMPANY_IMAGE = 1188;
    private long placeId;
    private String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_images_company,container,false);
        if(getActivity().getIntent().getExtras()!=null){
            email = getActivity().getIntent().getExtras().getString("email");
            placeId = getActivity().getIntent().getExtras().getLong("id");
        }
        companyImages = (RecyclerView) root.findViewById(R.id.company_recycler_images);
        companyImages.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyImagesAdapter = new RecycleImagesCompanyAdapter(new ArrayList<PlaceImages>());
        companyImages.setAdapter(companyImagesAdapter);
        ItemTouchHelper swipeToDismiss = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (companyImagesAdapter.getCompanyImages().size()==1) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final PlaceImages image = companyImagesAdapter.getCompanyImages().remove(viewHolder.getAdapterPosition());
                getActivity().getContentResolver().delete(
                        RecappContract.PlaceImageEntry.CONTENT_URI,
                        RecappContract.PlaceImageEntry._ID +" = ?",
                        new String[]{""+image.getId()}
                );
                companyImagesAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(root,"The image was deleted",Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ContentValues values = new ContentValues();
                                values.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE,image.getImage());
                                values.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY, placeId);
                                getActivity().getContentResolver().insert(
                                        RecappContract.PlaceImageEntry.CONTENT_URI,
                                        values
                                );
                                //image.setId(RecappContract.PlaceImageEntry.getIdFromUri(uri));
                                //Log.e("algo",""+position);
                                //companyImagesAdapter.getCompanyImages().add(position, image);
                                //companyImagesAdapter.notifyDataSetChanged();

                            }
                        }).show();

            }
        });
        swipeToDismiss.attachToRecyclerView(companyImages);
        if(getLoaderManager().getLoader(COMPANY_IMAGE)==null){
            getLoaderManager().initLoader(COMPANY_IMAGE,null,this);
        }else{
            getLoaderManager().restartLoader(COMPANY_IMAGE,null,this);
        }
        return root;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.PlaceImageEntry.buildPlaceImagePlaceUri(placeId),
                new String[]{RecappContract.PlaceImageEntry.COLUMN_IMAGE,
                RecappContract.PlaceImageEntry.TABLE_NAME+"."+ RecappContract.PlaceImageEntry._ID},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<PlaceImages> images = PlaceImages.allImagesPlaces(data);
        companyImagesAdapter.setCompanyImageCursor(data);
        companyImagesAdapter.swapData(images);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        companyImagesAdapter.closeCursor();
    }
}
