package com.unal.tuapp.recapp;



import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;


public class Detail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Toolbar toolbar;
    private NavigationView navDrawer;
    private DrawerLayout navigationDrawer;
    private GooglePlus mGooglePlus;
    private View root;
    private ImageView detail;
    private static final int PLACE = 5;
    private User  user;
    private long id;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton reminder;
    private DetailFragment detailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_detail, null);
        setContentView(root);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        getSupportLoaderManager().initLoader(PLACE,null,this);
        mGooglePlus = GooglePlus.getInstance(this, null, null);

        if(mGooglePlus.mGoogleApiClient.isConnected()){

            //Account account = Plus.AccountApi;


            TextView name = (TextView) findViewById(R.id.user_name);
            name.setText(user.getName()+" "+user.getLastName());

            TextView email = (TextView) findViewById(R.id.user_email);
            email.setText(user.getEmail());
            de.hdodenhof.circleimageview.CircleImageView imageView;
            imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);
            if(user.getProfileImage()!=null) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getProfileImage(), 0,
                        user.getProfileImage().length));
            }else if(Utility.isNetworkAvailable(this)){
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();
                //We try to request a image with major size, the new image will be of 600*600 pixels
                //The user doesn't have a image so we try to download one and put it to the user
                personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + mGooglePlus.PROFILE_PIC_SIZE;
                new LoadProfileImage(root,imageView).execute(personPhotoUrl,user.getEmail());
            }


        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.title_activity_detail));
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent)
        );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navDrawer = (NavigationView) findViewById(R.id.nav_drawer);
        //We need to do this because now we're not in home

        navigationDrawer = (DrawerLayout) findViewById(R.id.detail);
        navDrawer.getMenu().setGroupCheckable(R.id.main,false,false);



        //navigationView.getMenu().setGroupCheckable(R.id.main, false, false);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this,navigationDrawer,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerSlide(View view,float slideOffset){
                super.onDrawerSlide(view,slideOffset);
                View temp = getCurrentFocus();
                if(temp!=null){
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputManager.isAcceptingText()){
                        inputManager.hideSoftInputFromWindow(temp.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                }

            }
        };
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(0);
        navigationDrawer.setDrawerListener(actionBarDrawerToggle);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(Detail.this, NavigationDrawer.class);
                        intentHome.putExtra("email",user.getEmail());
                        startActivity(intentHome);
                        break;
                    case R.id.favorites:
                        Intent intentFavorite = new Intent(Detail.this, UserDetail.class);
                        intentFavorite.putExtra("user", user);
                        intentFavorite.putExtra("type", "favorite");
                        startActivity(intentFavorite);
                        break;
                    case R.id.appointments:
                        Intent intentReminder = new Intent(Detail.this, UserDetail.class);
                        intentReminder.putExtra("user", user);
                        intentReminder.putExtra("type", "reminder");
                        startActivity(intentReminder);
                        break;
                    case R.id.comments:
                        Intent intentComment = new Intent(Detail.this, UserDetail.class);
                        intentComment.putExtra("user", user);
                        intentComment.putExtra("type", "comment");
                        startActivity(intentComment);
                        break;
                    case R.id.sign_out:
                        if (mGooglePlus.mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                            mGooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(Detail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                    case R.id.disconnect:
                        if (mGooglePlus.mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                            Plus.AccountApi.revokeAccessAndDisconnect(mGooglePlus.mGoogleApiClient);
                            mGooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(Detail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                }
                //menuItem.setChecked(true);
                navigationDrawer.closeDrawers();
                return false;
            }
        });
        reminder = (FloatingActionButton) root.findViewById(R.id.reminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reminder.setVisibility(view.GONE);
                //We should to create other activity
                Intent intent = new Intent(Detail.this,ReminderActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("user",user);
                startActivity(intent);

            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        detailFragment = new DetailFragment();
        fragmentTransaction.replace(R.id.detail_container, detailFragment);
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
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home){
            Intent intentHome = new Intent(Detail.this, NavigationDrawer.class);
            intentHome.putExtra("email",user.getEmail());
            startActivity(intentHome);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return  new CursorLoader(
                this,
                RecappContract.PlaceEntry.buildPlaceUri(this.id),
                new String[]{RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        detail = (ImageView) findViewById(R.id.detail_image);
        data.moveToFirst();
        detail.setImageBitmap(BitmapFactory.decodeByteArray(
                data.getBlob(0),0,data.getBlob(0).length
        ));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
