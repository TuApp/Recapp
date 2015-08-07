package com.unal.tuapp.recapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.RecappDBHelper;
import com.unal.tuapp.recapp.data.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;


/**
 * Created by andresgutierrez on 7/11/15.
 */
public class NavigationDrawer extends AppCompatActivity  {
    private int totalFilter;
    private String emailUser;
    private NavigationView navDrawer;
    private NavigationView navFilterDrawer;
    private TabLayout tabLayout;
    private DrawerLayout navigationDrawer;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private GooglePlus mGooglePlus;
    private View root;
    private ActionBarDrawerToggle drawerToggle;
    private Button add;
    private final String TAG = NavigationDrawer.class.getSimpleName();
    private final String FILE = "filters.txt";
    private RecappDBHelper recappDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        setContentView(root);
        recappDBHelper = RecappDBHelper.getInstance(getApplicationContext());
        totalFilter = 0;
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
            emailUser = account.getAccountName(mGooglePlus.mGoogleApiClient);
            email.setText(emailUser);
            de.hdodenhof.circleimageview.CircleImageView imageView;
            imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);
            addUser(emailUser,
                    currentPerson.getName().getGivenName(),currentPerson.getName().getFamilyName());
            new LoadProfileImage(root,imageView).execute(personPhotoUrl,account.getAccountName(mGooglePlus.mGoogleApiClient));
        }
        add = (Button) root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) root.findViewById(R.id.filter_text);
                if(!editText.getText().toString().trim().equals("")){
                    Menu menu = navFilterDrawer.getMenu();
                    menu.add(0,totalFilter,Menu.NONE,editText.getText().toString());
                    totalFilter++;
                    editText.setText("");


                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setUpToolbar();
        setUpViewPager();
        //setUpTabLayout();
        navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navDrawer = (NavigationView) findViewById(R.id.nav_drawer);
        drawerToggle = new ActionBarDrawerToggle(this,navigationDrawer,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerSlide(View view,float slideOffset){
                super.onDrawerSlide(view,slideOffset);
                View temp = getCurrentFocus();
                if(temp!=null){
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputManager.isAcceptingText()){
                        inputManager.hideSoftInputFromWindow(temp.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        //ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter)viewPager.getAdapter();
                        //Fragment fragment = viewPagerAdapter.getItem(viewPager.getCurrentItem());

                        /*if(slideOffset==0.0) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .detach(fragment)
                                    .attach(fragment)
                                    .commit();
                        }*/
                    }

                }



            }
        };

        navigationDrawer.setDrawerListener(drawerToggle);
        navFilterDrawer = (NavigationView) findViewById(R.id.filter_nav_drawer);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.favorites:
                        View view = root.findViewById(R.id.view_pager);
                        Cursor user = getContentResolver().query(
                                RecappContract.UserEntry.buildUserEmail("agutierrezt930410@gmail.com"),
                                null,
                                null,
                                null,
                                null
                        );
                        if(user.moveToFirst()) {
                            Snackbar.make(view, user.getString(user.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME))
                                    , Snackbar.LENGTH_LONG).show();
                            //Log.e("algo",""+user.getBlob(user.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE)));
                        }
                        user.close();
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
                            //animation = false;

                        }
                        break;
                    case R.id.disconnect:
                        if(mGooglePlus.mGoogleApiClient.isConnected()){
                            Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                            Plus.AccountApi.revokeAccessAndDisconnect(mGooglePlus.mGoogleApiClient);
                            mGooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(NavigationDrawer.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                }
                menuItem.setChecked(true);
                navigationDrawer.closeDrawers();
                return false;
            }
        });
        navFilterDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                navFilterDrawer.getMenu().removeItem(menuItem.getItemId());
                return false;
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {
            View view = root.findViewById(R.id.view_pager);


            Snackbar.make(view, "algo", Snackbar.LENGTH_LONG).show();
        }
        if(id == R.id.action_navigation){
            navigationDrawer.openDrawer(navFilterDrawer);
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
        // Pass any configuration change to the drawer toggles

    }

    @Override
    public void onPause(){
        super.onPause();
        saveFilters();


    }
    @Override
    public void onResume(){
        super.onResume();
        loadFilters();
    }


    public void setUpToolbar(){
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setUpViewPager(){
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.places)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.map)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.events)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tutorial)));

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        PlacesFragment placesFragment = new PlacesFragment();
        placesFragment.setOnPlaceListener(new PlacesFragment.onPlaceListener() {
            @Override
            public void onPlace(View view, long positon) {
                //Log.e("algo",""+positon);
                Cursor userCursor = getContentResolver().query(
                        RecappContract.UserEntry.buildUserEmail(emailUser),
                        new String[]{RecappContract.UserEntry._ID, RecappContract.UserEntry.COLUMN_USER_IMAGE},
                        null,
                        null,
                        null
                );
                User user = new User();
                if (userCursor.moveToFirst()) {

                    user.setProfileImage(userCursor.getBlob(userCursor.
                            getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE)));
                }

                Intent intent = new Intent(NavigationDrawer.this, Detail.class);
                intent.putExtra("id", positon);
                intent.putExtra("user", user);

                startActivity(intent);
            }
        });
        MapFragment mapFragment = new MapFragment();
        mapFragment.setOnMapListener(new MapFragment.onMapListener() {
            @Override
            public void onMap(Marker marker) {
                Intent intent = new Intent(NavigationDrawer.this, Detail.class);
                startActivity(intent);
            }
        });

        viewPagerAdapter.addFragment(placesFragment, getResources().getString(R.string.places));
        viewPagerAdapter.addFragment(mapFragment, getResources().getString(R.string.map));
        viewPagerAdapter.addFragment(new EventsFragment(), getResources().getString(R.string.events));
        viewPagerAdapter.addFragment(new TutorialFragment(), getResources().getString(R.string.tutorial));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void saveFilters(){
        try{
            FileOutputStream f = this.openFileOutput(FILE,  this.MODE_PRIVATE | this.MODE_APPEND);
            BufferedWriter output = new BufferedWriter(new PrintWriter(f));
            Menu menu = navFilterDrawer.getMenu();

            for(int i=0; i<menu.size(); i++){
                output.write(String.valueOf(menu.getItem(i)));
                output.newLine();
            }

            output.close();

        }catch (Exception e){}

    }
    public void loadFilters(){
        try{
            totalFilter = 0;
            FileInputStream f =  this.openFileInput(FILE);
            BufferedReader input = new BufferedReader(new InputStreamReader(f));
            String text;
            Menu menu = navFilterDrawer.getMenu();
            menu.clear();
            while((text=input.readLine())!=null){
                menu.add(0,totalFilter,Menu.NONE,text);
                totalFilter++;

            }
            input.close();
            deleteFile(FILE);
        }catch (Exception e){}

    }
    public Toolbar getToolbar(){
        return toolbar;
    }
    @Override
    public void onBackPressed() {
    }

    public void addUser(String email,String name, String lastname){
        Cursor userCursor = getContentResolver().query(
                RecappContract.UserEntry.buildUserEmail(email),
                new String[]{RecappContract.UserEntry._ID},
                null,
                null,
                null
        );
        if(!userCursor.moveToFirst()){ //New User so we can add him/her
            ContentValues values = new ContentValues();
            values.put(RecappContract.UserEntry.COLUMN_EMAIL,email);
            values.put(RecappContract.UserEntry.COLUMN_USER_NAME,name);
            values.put(RecappContract.UserEntry.COLUMN_USER_LASTNAME,lastname);
            getContentResolver().insert(RecappContract.UserEntry.CONTENT_URI,values);
            //Log.e("We do it", "add person");
        }
        userCursor.close();


    }


}
