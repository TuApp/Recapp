package com.unal.tuapp.recapp.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecyclePlaceImagesAdapter;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.PlaceImages;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.dialogs.MapDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/4/15.
 */
public class CompanyInformationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private View root;
    private Place place;
    private EditText companyName;
    private EditText companyDescription;
    private EditText companyAddress;
    private EditText companyWeb;
    private EditText companyPosition;
    private ImageView companyImage;
    private Button update;
    private final int PLACE_IMAGES =  7321;
    private static RecyclerView companyImages;
    private static RecyclePlaceImagesAdapter companyImagesAdapater;
    private long id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_information_company,container,false);
        if(getActivity().getIntent().getExtras()!=null){
            id = getActivity().getIntent().getExtras().getLong("id");
        }
        companyName = (EditText) root.findViewById(R.id.company_name);
        companyDescription = (EditText) root.findViewById(R.id.company_description);
        companyAddress = (EditText) root.findViewById(R.id.company_address);
        companyWeb = (EditText) root.findViewById(R.id.company_web);
        companyPosition = (EditText) root.findViewById(R.id.company_position);
        companyPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapDialog dialog = MapDialog.newInstance(new LatLng(place.getLat(),place.getLog()));
                dialog.show(getFragmentManager(),"map");
                dialog.setOnPlaceChange(new MapDialog.onPlaceChange() {
                    @Override
                    public void onPlaceChange(LatLng place) {
                        companyPosition.setText("Lat :"+place.latitude+" Lng: "+place.longitude);
                        CompanyInformationFragment.this.place.setLat(place.latitude);
                        CompanyInformationFragment.this.place.setLog(place.longitude);
                    }
                });
            }
        });
        update = (Button) root.findViewById(R.id.company_button_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = companyName.getText().toString();
                String description = companyDescription.getText().toString();
                String address = companyAddress.getText().toString();
                String web = companyWeb.getText().toString();
                Double lat = place.getLat();
                Double lng = place.getLog();
                byte[] image = place.getImageFavorite();
                if(!name.equals("") && !description.equals("")
                        && !address.equals("") && !web.equals("")) {
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.PlaceEntry.COLUMN_ADDRESS, address);
                    values.put(RecappContract.PlaceEntry.COLUMN_DESCRIPTION, description);
                    values.put(RecappContract.PlaceEntry.COLUMN_WEB, web);
                    values.put(RecappContract.PlaceEntry.COLUMN_NAME, name);
                    values.put(RecappContract.PlaceEntry.COLUMN_LAT, lat);
                    values.put(RecappContract.PlaceEntry.COLUMN_LOG, lng);
                    values.put(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE, image);
                    getActivity().getContentResolver().update(
                            RecappContract.PlaceEntry.CONTENT_URI,
                            values,
                            RecappContract.PlaceEntry._ID + " = ?",
                            new String[]{"" + CompanyInformationFragment.this.id}
                    );
                    Toast.makeText(getActivity(), "We update the data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        companyImage = (ImageView) root.findViewById(R.id.company_image);
        companyImages = (RecyclerView) root.findViewById(R.id.company_recycler_images);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        companyImages.setLayoutManager(linearLayoutManager);
        companyImagesAdapater = new RecyclePlaceImagesAdapter(new ArrayList<byte[]>());
        companyImages.setAdapter(companyImagesAdapater);
        companyImagesAdapater.setOnItemClickListener(new RecyclePlaceImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                companyImagesAdapater.setCurrentSelectedPositionImage((int)position);
                place.setImageFavorite(companyImagesAdapater.getImage((int)position));
                companyImage.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                                companyImagesAdapater.getImage((int)position),0,
                                companyImagesAdapater.getImage((int)position).length
                        )
                );
                companyImagesAdapater.notifyDataSetChanged();
            }
        });


        if(getLoaderManager().getLoader(PLACE_IMAGES)==null){
            getLoaderManager().initLoader(PLACE_IMAGES,null,this);
        }else{
            getLoaderManager().restartLoader(PLACE_IMAGES,null,this);
        }

        return root;
    }

    public void setPlace(Place place){
        this.place = place;
        companyName.setText(place.getName());
        companyDescription.setText(place.getDescription());
        companyAddress.setText(place.getAddress());
        companyWeb.setText(place.getWeb());
        companyPosition.setText("Lat : "+place.getLat()+" Lng: "+place.getLog());
        companyImage.setImageBitmap(
                BitmapFactory.decodeByteArray(place.getImageFavorite(),0,place.getImageFavorite().length)
        );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return  new CursorLoader(
                getActivity(),
                RecappContract.PlaceImageEntry.buildPlaceImagePlaceUri(this.id),
                new String[]{RecappContract.PlaceImageEntry.COLUMN_IMAGE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<byte[]> placeImages = PlaceImages.allImages(data);
        companyImagesAdapater.swapData(placeImages);
        companyImagesAdapater.setPlaceImagesCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        companyImagesAdapater.closeCursor();
    }
}
