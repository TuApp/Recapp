package com.unal.tuapp.recapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.PlaceImages;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.fragments.GalleryFragment;

import java.util.List;

public class Gallery extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private GooglePlus mGooglePlus;
    private View root;
    private static final int PLACE_IMAGE = 12;
    private User user;
    private long id;
    private GalleryFragment galleryFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_gallery, null);
        setContentView(root);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        if(getSupportLoaderManager().getLoader(PLACE_IMAGE)==null)
            getSupportLoaderManager().initLoader(PLACE_IMAGE,null,this);
        else
            getSupportLoaderManager().restartLoader(PLACE_IMAGE, null, this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        galleryFragment = new GalleryFragment();
        fragmentTransaction.replace(R.id.gallery_container, galleryFragment);
        fragmentTransaction.commit();

    }
    @Override
    protected void onPause(){
        super.onPause();

        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home){
            Intent intentHome = new Intent(Gallery.this, NavigationDrawer.class);
            startActivity(intentHome);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return  new CursorLoader(
                this,
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
        galleryFragment.setData(placeImages,data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        galleryFragment.closeData();
    }

}
