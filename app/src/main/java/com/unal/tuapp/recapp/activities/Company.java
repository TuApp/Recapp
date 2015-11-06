package com.unal.tuapp.recapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.CompanyCommentsFragment;
import com.unal.tuapp.recapp.fragments.CompanyEventsFragment;
import com.unal.tuapp.recapp.fragments.CompanyInformationFragment;
import com.unal.tuapp.recapp.fragments.CompanyMainFragment;

import java.util.List;

/**
 * Created by andresgutierrez on 11/3/15.
 */
public class Company extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private FloatingActionButton companyEvent;
    private View root;
    private DrawerLayout navigationDrawer;
    private NavigationView navDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private de.hdodenhof.circleimageview.CircleImageView companyImage;
    private TextView companyName;
    private final int PLACE = 2569;
    private String email;
    private long placeId;
    private Place place;
    private Fragment companyHome;
    private Fragment companyInformation;
    private Fragment companyComments;
    private Fragment companyEventFragment;
    private String menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_company, null);
        setContentView(root);
        menu = "home";
        if(savedInstanceState!=null){
            menu = savedInstanceState.getString("type");
        }
        if(getIntent().getExtras()!=null){
            email = getIntent().getExtras().getString("email");
            placeId = getIntent().getExtras().getLong("id");
        }
        companyEvent = (FloatingActionButton) root.findViewById(R.id.company_create_event);
        companyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Company.this,EventActivity.class);
                if(place!=null){
                    intent.putExtra("email",place.getEmail());
                    intent.putExtra("id",place.getId());
                }
                startActivity(intent);
            }
        });
        companyImage = (de.hdodenhof.circleimageview.CircleImageView) root.findViewById(R.id.favorite_image);
        companyName = (TextView) root.findViewById(R.id.company_name);
        companyHome = new CompanyMainFragment();
        companyInformation = new CompanyInformationFragment();
        companyComments = new CompanyCommentsFragment();
        companyEventFragment =  new CompanyEventsFragment();
        ((CompanyEventsFragment) companyEventFragment).setOnEventCompanyListener(new CompanyEventsFragment.OnEventCompanyListener() {
            @Override
            public void onAction(long id) {
                Intent intent = new Intent(Company.this,EventUpdateActivity.class);
                if(place!=null){
                    intent.putExtra("email",place.getEmail());
                    intent.putExtra("id",place.getId());
                    intent.putExtra("event",id);
                }
                startActivity(intent);
            }
        });
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationDrawer = (DrawerLayout) root.findViewById(R.id.navigation_drawer_company);
        navDrawer = (NavigationView) root.findViewById(R.id.nav_drawer_company);
        drawerToggle = new ActionBarDrawerToggle(this,navigationDrawer,toolbar,R.string.drawer_open,R.string.drawer_close){};
        navigationDrawer.setDrawerListener(drawerToggle);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            if (companyHome.isAdded()) {
                                ft.show(companyHome);

                            }
                            ft.hide(companyInformation);
                            ft.hide(companyComments);
                            ft.hide(companyEventFragment);
                            ft.commit();
                            companyEvent.hide();
                            menu = "home";
                            break;
                        case R.id.information:
                            if (companyInformation.isAdded()) {
                                ft.show(companyInformation);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyComments);
                            ft.hide(companyEventFragment);
                            ft.commit();
                            companyEvent.hide();
                            menu = "information";
                            break;
                        case R.id.comments:
                            if(companyComments.isAdded()){
                                ft.show(companyComments);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyEventFragment);
                            ft.commit();
                            companyEvent.hide();
                            menu = "comments";
                            break;
                        case R.id.events:
                            if(companyEventFragment.isAdded()){
                                ft.show(companyEventFragment);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyComments);
                            ft.commit();
                            companyEvent.show();
                            menu = "events";
                            break;
                        case R.id.images:
                            break;
                        case R.id.sign_out:
                            Intent intent = new Intent(Company.this, Recapp.class);
                            startActivity(intent);
                            break;
                    }
                    menuItem.setChecked(true);
                    navigationDrawer.closeDrawers();
                    return true;
                }
            });
            switch (menu){
                case "home":
                    fragmentTransaction.replace(R.id.company_container, companyHome,"home");
                    fragmentTransaction.add(R.id.company_container, companyInformation, "information");
                    fragmentTransaction.add(R.id.company_container, companyComments,"comments");
                    fragmentTransaction.add(R.id.company_container, companyEventFragment,"events");
                    fragmentTransaction.hide(companyEventFragment);
                    fragmentTransaction.hide(companyInformation);
                    fragmentTransaction.hide(companyComments);
                    fragmentTransaction.commit();
                    companyEvent.hide();
                    navDrawer.getMenu().findItem(R.id.home).setChecked(true);
                    break;
                case "information":
                    fragmentTransaction.replace(R.id.company_container, companyInformation,"information");
                    fragmentTransaction.add(R.id.company_container, companyHome, "home");
                    fragmentTransaction.add(R.id.company_container, companyComments, "comments");
                    fragmentTransaction.add(R.id.company_container, companyEventFragment,"events");
                    fragmentTransaction.hide(companyEventFragment);
                    fragmentTransaction.hide(companyHome);
                    fragmentTransaction.hide(companyComments);
                    fragmentTransaction.commit();
                    companyEvent.hide();
                    navDrawer.getMenu().findItem(R.id.information).setChecked(true);

                    break;
                case "comments":
                    fragmentTransaction.replace(R.id.company_container, companyComments, "comments");
                    fragmentTransaction.add(R.id.company_container, companyInformation, "information");
                    fragmentTransaction.add(R.id.company_container, companyHome, "home");
                    fragmentTransaction.add(R.id.company_container, companyEventFragment, "events");
                    fragmentTransaction.hide(companyEventFragment);
                    fragmentTransaction.hide(companyInformation);
                    fragmentTransaction.hide(companyHome);
                    fragmentTransaction.commit();
                    companyEvent.hide();
                    navDrawer.getMenu().findItem(R.id.comments).setChecked(true);

                    break;
                case "events":
                    fragmentTransaction.replace(R.id.company_container, companyEventFragment, "events");
                    fragmentTransaction.add(R.id.company_container, companyInformation, "information");
                    fragmentTransaction.add(R.id.company_container, companyHome, "home");
                    fragmentTransaction.add(R.id.company_container, companyComments,"comments");
                    fragmentTransaction.hide(companyComments);
                    fragmentTransaction.hide(companyInformation);
                    fragmentTransaction.hide(companyHome);
                    fragmentTransaction.commit();
                    companyEvent.show();
                    navDrawer.getMenu().findItem(R.id.events).setChecked(true);

                    break;

            }

            if(getSupportLoaderManager().getLoader(PLACE)==null){
                getSupportLoaderManager().initLoader(PLACE,null,this);
            }else{
                getSupportLoaderManager().restartLoader(PLACE,null,this);
            }

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recapp, menu);
        return true;
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
                    RecappContract.PlaceEntry.buildPlaceUri(placeId),
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            List<Place> places = Place.allPlaces(data);
            if(!places.isEmpty()){
                place = places.get(0);
                place.setEmail(email);
                companyName.setText(place.getName());
                companyImage.setImageBitmap(
                        BitmapFactory.decodeByteArray(place.getImageFavorite(), 0, place.getImageFavorite().length)
                );
                ((CompanyMainFragment)companyHome).setPlace(place);
                ((CompanyInformationFragment)companyInformation).setPlace(place);
            }

        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
            outState.putString("type",menu);
        }
    @Override
    public void onBackPressed() {}
}
