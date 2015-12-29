package com.unal.tuapp.recapp.activities;



import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.LoadProfileImage;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.fragments.DetailFragment;


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
    private InterstitialAd mInterstitialAd;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String emailUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_detail, null);
        setContentView(root);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id_gallery));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(Detail.this, Gallery.class);
                intent.putExtra("id", id);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        getSupportLoaderManager().initLoader(PLACE,null,this);
        mGooglePlus = GooglePlus.getInstance(this, null, null);
        TextView name = (TextView) findViewById(R.id.user_name);
        TextView email = (TextView) findViewById(R.id.user_email);
        de.hdodenhof.circleimageview.CircleImageView imageView;
        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);
        if(mGooglePlus.mGoogleApiClient.isConnected()){
            //Account account = Plus.AccountApi;
            if(user!=null){
                name.setText(user.getName()+" "+user.getLastName());
                email.setText(user.getEmail());
                if(user.getProfileImage()!=null) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getProfileImage(), 0,
                            user.getProfileImage().length,options));
                }
                else if(Utility.isNetworkAvailable(this)){
                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
                    String personPhotoUrl = currentPerson.getImage().getUrl();
                    //We try to request a image with major size, the new image will be of 600*600 pixels
                    //The user doesn't have a image so we try to download one and put it to the user
                    personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + mGooglePlus.PROFILE_PIC_SIZE;
                    new LoadProfileImage(root,imageView).execute(personPhotoUrl,user.getEmail());
                }
            }else if(Utility.isNetworkAvailable(this)){
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
                Account account = Plus.AccountApi;
                String personPhotoUrl = currentPerson.getImage().getUrl();
                //We try to request a image with major size, the new image will be of 600*600 pixels
                personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + mGooglePlus.PROFILE_PIC_SIZE;


                name.setText(currentPerson.getDisplayName());

                emailUser = account.getAccountName(mGooglePlus.mGoogleApiClient);
                email.setText(emailUser);

                try{
                    Utility.addUser(this,emailUser,
                            currentPerson.getName().getGivenName(), currentPerson.getName().getFamilyName());
                }catch (Exception e){}
                new LoadProfileImage(root, imageView).execute(personPhotoUrl, account.getAccountName(mGooglePlus.mGoogleApiClient));
            }


        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        //collapsingToolbarLayout.setTitle(getString(R.string.title_activity_detail));
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent)
        );
        //toolbar.setTitle(getString(R.string.title_activity_detail));
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
                    case R.id.events:
                        Intent intentEvent = new Intent(Detail.this,UserDetail.class);
                        intentEvent.putExtra("user",user);
                        intentEvent.putExtra("type","event");
                        startActivity(intentEvent);
                        break;
                    case R.id.points:
                        Intent intentPoint = new Intent(Detail.this,UserDetail.class);
                        intentPoint.putExtra("user",user);
                        intentPoint.putExtra("type","points");
                        startActivity(intentPoint);
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
        detailFragment.setOnPlaceImagesListener(new DetailFragment.onPlaceImagesListener() {
            @Override
            public void onPlaceImages(View view, long position) {
                if(mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else{
                    Intent intent = new Intent(Detail.this, Gallery.class);
                    intent.putExtra("id", id);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

            }
        });
        fragmentTransaction.replace(R.id.detail_container, detailFragment);
        fragmentTransaction.commit();

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
        if(data.moveToFirst()){
            final BitmapFactory.Options options= new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap image  = BitmapFactory.decodeByteArray(
                    data.getBlob(0), 0, data.getBlob(0).length,options);
            //Bitmap scaledImage = Bitmap.createScaledBitmap(image,100,100,true);

            Palette.from(image).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int muttedDark = palette.getDarkMutedColor(getResources().getColor(R.color.my_primary_dark));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(muttedDark);
                    }
                    collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(getResources().getColor(R.color.my_primary)));
                }
            });

            detail.setImageBitmap(image);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
