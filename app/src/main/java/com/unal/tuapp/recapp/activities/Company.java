package com.unal.tuapp.recapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.CompanyMainFragment;
import com.unal.tuapp.recapp.fragments.RecappFragment;

/**
 * Created by andresgutierrez on 11/3/15.
 */
public class Company extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private View root;
    private DrawerLayout navigationDrawer;
    private NavigationView navDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private de.hdodenhof.circleimageview.CircleImageView companyImage;
    private TextView companyName;
    private final int PLACE = 2569;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_company, null);
        setContentView(root);
        if(getIntent().getExtras()!=null){
            email = getIntent().getExtras().getString("email");
        }

        companyImage = (de.hdodenhof.circleimageview.CircleImageView) root.findViewById(R.id.favorite_image);
        companyName = (TextView) root.findViewById(R.id.company_name);

        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationDrawer = (DrawerLayout) root.findViewById(R.id.navigation_drawer_company);
        navDrawer = (NavigationView) root.findViewById(R.id.nav_drawer_company);
        drawerToggle = new ActionBarDrawerToggle(this,navigationDrawer,toolbar,R.string.drawer_open,R.string.drawer_close){};
        navigationDrawer.setDrawerListener(drawerToggle);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        break;
                    case R.id.information:
                        break;
                    case R.id.comments:
                        break;
                    case R.id.events:
                        break;
                    case R.id.images:
                        break;
                    case R.id.sign_out:
                        Intent intent = new Intent(Company.this,Recapp.class);
                        startActivity(intent);
                        break;
                }
                menuItem.setChecked(true);
                navigationDrawer.closeDrawers();
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.company_container,new CompanyMainFragment())
                .commit();

        if(getSupportLoaderManager().getLoader(PLACE)==null){
            getSupportLoaderManager().initLoader(PLACE,null,this);
        }else{
            getSupportLoaderManager().restartLoader(PLACE,null,this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                RecappContract.PlaceEntry.buildPlaceEmailUri(email),
                new String[]{RecappContract.PlaceEntry.COLUMN_NAME, RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            companyName.setText(data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_NAME)));
            companyImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)),
                            0,data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)).length)
            );

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
