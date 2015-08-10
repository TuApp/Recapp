package com.unal.tuapp.recapp;



import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.Account;
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
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
            Account account = Plus.AccountApi;
            String personPhotoUrl = currentPerson.getImage().getUrl();

            //We try to request a image with major size, the new image will be of 600*600 pixels
            personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + mGooglePlus.PROFILE_PIC_SIZE;

            TextView name = (TextView) findViewById(R.id.user_name);
            name.setText(currentPerson.getDisplayName());

            TextView email = (TextView) findViewById(R.id.user_email);
            email.setText(account.getAccountName(mGooglePlus.mGoogleApiClient));
            de.hdodenhof.circleimageview.CircleImageView imageView;
            imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);

            new LoadProfileImage(root,imageView).execute(personPhotoUrl);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.title_activity_detail));
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent)
        );

        setSupportActionBar(toolbar);
        navDrawer = (NavigationView) findViewById(R.id.nav_drawer);
        //We need to do this because now we're not in home

        navigationDrawer = (DrawerLayout) findViewById(R.id.detail);
        navDrawer.getMenu().setGroupCheckable(R.id.main,false,false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        Intent intentHome = new Intent(Detail.this,NavigationDrawer.class);
                        startActivity(intentHome);
                        break;
                    case R.id.favorites:
                        Intent intentFavorite = new Intent(Detail.this,UserDetail.class);
                        intentFavorite.putExtra("user",user);
                        intentFavorite.putExtra("type","favorite");
                        startActivity(intentFavorite);
                        break;
                    case R.id.appointments:
                        Intent intentReminder = new Intent(Detail.this,UserDetail.class);
                        intentReminder.putExtra("user",user);
                        intentReminder.putExtra("type","reminder");
                        startActivity(intentReminder);
                        break;
                    case R.id.comments:
                        Intent intentComment = new Intent(Detail.this,UserDetail.class);
                        intentComment.putExtra("user",user);
                        intentComment.putExtra("type","comment");
                        startActivity(intentComment);
                        break;
                    case R.id.sign_out:
                        if(mGooglePlus.mGoogleApiClient.isConnected()){
                            Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                            mGooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(Detail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                    case R.id.disconnect:
                        if(mGooglePlus.mGoogleApiClient.isConnected()){
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DetailFragment detailFragment = new DetailFragment();

        fragmentTransaction.replace(R.id.detail_container,detailFragment);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

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
