package com.unal.tuapp.recapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by andresgutierrez on 7/11/15.
 */
public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navDrawer;
    private DrawerLayout navigationDrawer;
    private GooglePlus mGooglePlus;
    private static final int PROFILE_PIC_SIZE = 600;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        setContentView(root);
        mGooglePlus = GooglePlus.getInstance(this,null,null);
        if(mGooglePlus.mGoogleApiClient.isConnected()){
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
            Account account = Plus.AccountApi;
            String personPhotoUrl = currentPerson.getImage().getUrl();

            //We try to request a image with major size, the new image will be of 600*600 pixels
            personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + PROFILE_PIC_SIZE;

            TextView name = (TextView) findViewById(R.id.user_name);
            name.setText(currentPerson.getDisplayName());

            TextView email = (TextView) findViewById(R.id.user_email);
            email.setText(account.getAccountName(mGooglePlus.mGoogleApiClient));
            de.hdodenhof.circleimageview.CircleImageView imageView;
            imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);

            new LoadProfileImage(root,imageView).execute(personPhotoUrl);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navDrawer = (NavigationView) findViewById(R.id.nav_drawer);
        navDrawer.setNavigationItemSelectedListener(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            navigationDrawer.openDrawer(GravityCompat.START);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.favorites:
                break;
            case R.id.appointments:
                break;
            case R.id.comments:
                break;
            case R.id.sign_out:
                if(mGooglePlus.mGoogleApiClient.isConnected()){
                    Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                    mGooglePlus.mGoogleApiClient.disconnect();
                    Intent intent = new Intent(NavigationDrawer.this, Recapp.class);
                    startActivity(intent);
                }
                break;
            case R.id.disconnect:
                if(mGooglePlus.mGoogleApiClient.isConnected()){
                    Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGooglePlus.mGoogleApiClient);
                    mGooglePlus.mGoogleApiClient.disconnect();
                    Intent intent = new Intent(NavigationDrawer.this, Recapp.class);
                    startActivity(intent);
                }
                break;
        }
        menuItem.setChecked(true);
        navigationDrawer.closeDrawers();
        return false;
    }


}
