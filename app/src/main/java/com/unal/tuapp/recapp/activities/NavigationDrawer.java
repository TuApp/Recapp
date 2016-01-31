package com.unal.tuapp.recapp.activities;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.data.News;
import com.unal.tuapp.recapp.fragments.NewsFragment;
import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.LoadProfileImage;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.adapters.ViewPagerAdapter;
import com.unal.tuapp.recapp.data.Category;
import com.unal.tuapp.recapp.data.Event;
import com.unal.tuapp.recapp.data.MySuggestionProvider;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.SubCategory;
import com.unal.tuapp.recapp.data.Tutorial;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.fragments.EventsFragment;
import com.unal.tuapp.recapp.dialogs.FilterCategoriesDialogFragment;
import com.unal.tuapp.recapp.dialogs.FilterSubCategoriesDialogFragment;
import com.unal.tuapp.recapp.fragments.MapFragment;
import com.unal.tuapp.recapp.fragments.PlacesFragment;
import com.unal.tuapp.recapp.fragments.TutorialFragment;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by andresgutierrez on 7/11/15.
 */
public class NavigationDrawer extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int totalFilter;
    private String emailUser;
    private User user;
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
    private ImageButton resetFilters;
    private final String TAG = NavigationDrawer.class.getSimpleName();
    private final String FILE = "filters.txt";
    private static final int USER = 10;
    private static final int PLACE = 345;
    private static final int PLACE_FILTER = 467;
    private static final int EVENT = 579;
    private static final int EVENT_GOING = 591;
    private static final int TUTORIAL = 610;
    private static final int TUTORIAL_FILTER = 620;
    private Cursor userCursor;
    private MapFragment mapFragment;
    private PlacesFragment placesFragment;
    private EventsFragment eventsFragment;
    private TutorialFragment tutorialsFragment;
    private NewsFragment newsFragment;

    private List<String> filtersConstraint;
    private String[] selectionArgs=null;
    private String selectionPlaces =null;
    private String query;
    private boolean savedInstance;

    private static SearchView searchView;
    private static String deepLink;
    private TextView email;
    private TextView name;
    private de.hdodenhof.circleimageview.CircleImageView imageView;
    private FloatingActionButton eventCreate;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAdNews;
    private List<News> news;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        setContentView(root);
        /*Pair<Pair<Context,String>,Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User,String>> pair = new Pair<>(new Pair<>(getApplicationContext(),"nothing"),new Pair<>(new com.unal.tuapp.recapp.backend.model.userApi.model.User(),"allUser"));
        new UserEndPoint().execute(pair);*/
        mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(NavigationDrawer.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id_tutorial));
        mInterstitialAd.loadAd(adRequest);

        AdRequest adRequestNews = new AdRequest.Builder()
                .build();
        mInterstitialAdNews = new InterstitialAd(NavigationDrawer.this);
        mInterstitialAdNews.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id_news));
        mInterstitialAdNews.loadAd(adRequestNews);

        eventCreate = (FloatingActionButton) root.findViewById(R.id.event_create);
        query = "";
        totalFilter = 0;
        mGooglePlus = GooglePlus.getInstance(this, null, null);
        filtersConstraint = new ArrayList<>();
        savedInstance = false;
        if(savedInstanceState!=null){
            savedInstance = true;
        }
        if(getIntent().hasExtra("email")) {
            emailUser = getIntent().getExtras().getString("email");
        }
        email = (TextView) findViewById(R.id.user_email);
        name = (TextView) findViewById(R.id.user_name);

        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);
        if(getSupportLoaderManager().getLoader(USER)==null) {
            getSupportLoaderManager().initLoader(USER, null, this);
        }else{
            getSupportLoaderManager().restartLoader(USER, null, this);
        }

        if(getSupportLoaderManager().getLoader(PLACE)==null){
            getSupportLoaderManager().initLoader(PLACE,null,this);
        }else{
            getSupportLoaderManager().restartLoader(PLACE,null,this);
        }
        if(getSupportLoaderManager().getLoader(EVENT)==null){
            getSupportLoaderManager().initLoader(EVENT,null,this);
        }else{
            getSupportLoaderManager().restartLoader(EVENT,null,this);
        }
        if(getSupportLoaderManager().getLoader(EVENT_GOING)==null){
            getSupportLoaderManager().initLoader(EVENT_GOING,null,this);
        }else{
            getSupportLoaderManager().restartLoader(EVENT_GOING,null,this);
        }
        if(getSupportLoaderManager().getLoader(TUTORIAL) == null) {
            getSupportLoaderManager().initLoader(TUTORIAL,null,this);
        }else{
            getSupportLoaderManager().restartLoader(TUTORIAL,null,this);
        }
        if(mGooglePlus.mGoogleApiClient.isConnected()){
            if(Utility.isNetworkAvailable(this)) {
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



            resetLoaderFilter();



        }
        add = (Button) root.findViewById(R.id.add_category);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoriesDialog();
            }
        });

        resetFilters = (ImageButton) findViewById(R.id.resetFilters);
        resetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navFilterDrawer.getMenu().clear();
                filtersConstraint.clear();
                selectionPlaces = null;
                selectionArgs = null;
                if (getSupportLoaderManager().getLoader(PLACE) == null) {
                    getSupportLoaderManager().initLoader(PLACE, null, NavigationDrawer.this);
                } else {
                    getSupportLoaderManager().restartLoader(PLACE, null, NavigationDrawer.this);
                }
                if (getSupportLoaderManager().getLoader(TUTORIAL) == null) {
                    getSupportLoaderManager().initLoader(TUTORIAL, null, NavigationDrawer.this);
                } else {
                    getSupportLoaderManager().restartLoader(TUTORIAL, null, NavigationDrawer.this);
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setUpToolbar();
        setUpViewPager();
        eventCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationDrawer.this, EventActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    eventCreate.setVisibility(View.VISIBLE);
                } else {
                    eventCreate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                        Intent intentFavorite = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentFavorite.putExtra("user",user);
                        intentFavorite.putExtra("type","favorite");
                        startActivity(intentFavorite);
                        break;
                    case R.id.appointments:
                        Intent intentReminder = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentReminder.putExtra("user",user);
                        intentReminder.putExtra("type","reminder");
                        startActivity(intentReminder);
                        break;
                    case R.id.comments:
                        Intent intentComment = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentComment.putExtra("user",user);
                        intentComment.putExtra("type","comment");
                        startActivity(intentComment);
                        break;
                    case R.id.events:
                        Intent intentEvent = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentEvent.putExtra("user",user);
                        intentEvent.putExtra("type","event");
                        startActivity(intentEvent);
                        break;
                    case R.id.points:
                        Intent intentPoint = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentPoint.putExtra("user",user);
                        intentPoint.putExtra("type", "points");
                        startActivity(intentPoint);
                        break;
                    case R.id.contest:
                        Intent intentContest = new Intent(NavigationDrawer.this,UserDetail.class);
                        intentContest.putExtra("user",user);
                        intentContest.putExtra("type", "contest");
                        startActivity(intentContest);
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
                    case R.id.help:
                        Intent intentHelp = new Intent(NavigationDrawer.this,Help.class);
                        startActivity(intentHelp);
                        break;
                    case R.id.action_settings:
                        Intent intentSettings = new Intent(NavigationDrawer.this,Settings.class);
                        startActivity(intentSettings);
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
                filtersConstraint.remove(menuItem.toString());
                totalFilter--;
                if(query!=null && query.length()>0) {
                    handleIntent(getIntent());
                }else if(!filtersConstraint.isEmpty()) {
                    loadSelection();
                    resetLoaderFilter();
                }else{
                    selectionPlaces = null;
                    selectionArgs = null;
                    if (getSupportLoaderManager().getLoader(PLACE) == null) {
                        getSupportLoaderManager().initLoader(PLACE, null, NavigationDrawer.this);
                    } else {
                        getSupportLoaderManager().restartLoader(PLACE, null, NavigationDrawer.this);
                    }
                    if (getSupportLoaderManager().getLoader(TUTORIAL) == null) {
                        getSupportLoaderManager().initLoader(TUTORIAL, null, NavigationDrawer.this);
                    } else {
                        getSupportLoaderManager().restartLoader(TUTORIAL, null, NavigationDrawer.this);
                    }

                }

                //((PlacesFragment)(((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()))).setFilters(filtersConstraint);
                return false;
            }
        });
        deepLink = PlusShare.getDeepLinkId(getIntent());
        navDrawer.getMenu().findItem(R.id.home).setChecked(true);
        if(Utility.isNetworkAvailable(this)) {
            new NewsAsync().execute("https://ajax.googleapis.com/ajax/services/search/news?v=2.0&q=reciclaje&rsz=8");
        }

        //handleIntent(getIntent());
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!savedInstance) {
            loadFilters();
            loadSelection();
            resetLoaderFilter();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!savedInstance){
            saveFilters();
            savedInstance = true;
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        deepLinkIntent(PlusShare.getDeepLinkId(intent));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
        //searchView.setIconifiedByDefault(false);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
            Intent intentSettings = new Intent(NavigationDrawer.this,Settings.class);
            startActivity(intentSettings);
        }
        if(id == R.id.help){
            Intent intentHelp = new Intent(NavigationDrawer.this, Help.class);
            startActivity(intentHelp);
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
        drawerToggle.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles

    }

    public void setUpToolbar(){
        toolbar.setTitle(R.string.my_home);
        setSupportActionBar(toolbar);

    }

    public void setUpViewPager(){
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.places)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.map)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.events)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tutorial)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.news)));


        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        placesFragment = new PlacesFragment();
        placesFragment.setOnPlaceListener(new PlacesFragment.onPlaceListener() {
            @Override
            public void onPlace(View view, long position) {


                Intent intent = new Intent(NavigationDrawer.this, Detail.class);
                intent.putExtra("id", position);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        mapFragment = new MapFragment();
        mapFragment.setOnMapListener(new MapFragment.onMapListener() {
            @Override
            public void onMap(Place place) {
                Intent intent = new Intent(NavigationDrawer.this, Detail.class);
                intent.putExtra("id", place.getId());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        eventsFragment = new EventsFragment();
        eventsFragment.setOnEventListener(new EventsFragment.OnEventListener() {
            @Override
            public void onAction(long id) {
                //We should open an activity to the user can sign up in the event
                Intent intent = new Intent(NavigationDrawer.this, EventActivityGoing.class);
                intent.putExtra("user", user);
                intent.putExtra("event", id);
                startActivity(intent);
            }
        });

        tutorialsFragment = new TutorialFragment();
        tutorialsFragment.setOnTutorialListener(new TutorialFragment.onTutorialListener() {
            @Override
            public void onTutorial(View view, long position, final String link) {
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    }
                });

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }

            }
        });
        newsFragment = new NewsFragment();
        newsFragment.setOnNewsListener(new NewsFragment.OnNewsListener() {
            @Override
            public void onNews(final String link) {
                mInterstitialAdNews.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

                    }
                });
                if(mInterstitialAdNews.isLoaded()){
                    mInterstitialAdNews.show();
                }else{

                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(link)));
                }
            }
        });
        viewPagerAdapter.addFragment(placesFragment, getResources().getString(R.string.places));
        viewPagerAdapter.addFragment(mapFragment, getResources().getString(R.string.map));
        viewPagerAdapter.addFragment(eventsFragment, getResources().getString(R.string.events));
        viewPagerAdapter.addFragment(tutorialsFragment, getResources().getString(R.string.tutorial));
        viewPagerAdapter.addFragment(newsFragment,getResources().getString(R.string.news));
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void showCategoriesDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FilterCategoriesDialogFragment categoriesDialog = new FilterCategoriesDialogFragment();
        categoriesDialog.show(fm, "fragment_categories");
    }

    public void showSubCategoriesDialog(long idCategory){
        FragmentManager fm = getSupportFragmentManager();
        FilterSubCategoriesDialogFragment subcategoriesDialog = new FilterSubCategoriesDialogFragment();
        subcategoriesDialog.setIdCategory(idCategory);
        subcategoriesDialog.show(fm, "fragment_subcategories");
    }

    public void addItemToMenu(long idCategory, long idSubcategory, String subcategory){
        Menu menu = navFilterDrawer.getMenu();
        filtersConstraint.add(subcategory);
        menu.add(0, filtersConstraint.size(), Menu.NONE, subcategory).setIcon(android.R.drawable.ic_delete);
        if(query!=null && query.length()>0) {
            handleIntent(getIntent());
        }else if(!filtersConstraint.isEmpty()) {
            loadSelection();
            resetLoaderFilter();
        }else {
            selectionPlaces = null;
            selectionArgs = null;
            if (getSupportLoaderManager().getLoader(PLACE) == null) {
                getSupportLoaderManager().initLoader(PLACE, null, NavigationDrawer.this);
            } else {
                getSupportLoaderManager().restartLoader(PLACE, null, NavigationDrawer.this);
            }
            if (getSupportLoaderManager().getLoader(TUTORIAL) == null) {
                getSupportLoaderManager().initLoader(TUTORIAL, null, NavigationDrawer.this);
            } else {
                getSupportLoaderManager().restartLoader(TUTORIAL, null, NavigationDrawer.this);
            }

        }
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
            filtersConstraint = new ArrayList<>();
            while((text=input.readLine())!=null){
                menu.add(0,totalFilter,Menu.NONE,text).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                totalFilter++;

            }
            input.close();
            for(int i =0; i<menu.size(); i++){
                filtersConstraint.add(menu.getItem(i).toString());
            }


            deleteFile(FILE);
        }catch (Exception e){}

    }
    public Toolbar getToolbar(){
        return toolbar;
    }
    @Override
    public void onBackPressed() {

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!savedInstance) {
            saveFilters();
            savedInstance = true;
        }
        outState.putString("query", query);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadFilters();
        savedInstance = false;
        query = savedInstanceState.getString("query");
        if(query!=null && query.length()>0) {
            handleIntent(getIntent());
        }else {
            loadSelection();
            resetLoaderFilter();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecappContract.PlaceEntry.COLUMN_RATING + " DESC ";
        String sortOrderEvent = RecappContract.EventEntry.COLUMN_DATE + " ASC ";
        switch(id){
            case USER:
                return new CursorLoader(
                        this,
                        RecappContract.UserEntry.buildUserEmail(emailUser),
                        null,
                        null,
                        null,
                        null
                );

            case PLACE:
                return new CursorLoader(
                        this,
                        RecappContract.PlaceEntry.CONTENT_URI,
                        null,
                        selectionPlaces,
                        selectionArgs,
                        sortOrder
                );
            case PLACE_FILTER:
                return new CursorLoader(
                        this,
                        RecappContract.SubCategoryByPlaceEntry.buildSubCategoryByPlacePlaceSubCategory(),
                        new String[]{RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry._ID,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_NAME,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_LOG,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_LAT,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_ADDRESS,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_DESCRIPTION,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_RATING,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE,
                                RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry.COLUMN_WEB},
                        selectionPlaces,
                        selectionArgs,
                        sortOrder
                );
            case EVENT:
                //This show the events which the user isn't going to
                return new CursorLoader(
                        this,
                        RecappContract.EventEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        sortOrderEvent
                );
            case EVENT_GOING:
                return new CursorLoader(
                        this,
                        RecappContract.EventByUserEntry.buildEventByUserUser(emailUser),
                        new String[]{RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry._ID,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_ADDRESS,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_DATE,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_DESCRIPTION,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_NAME,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_CREATOR,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_LAT,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_LOG,
                                RecappContract.EventEntry.TABLE_NAME+"."+RecappContract.EventEntry.COLUMN_IMAGE},
                        null,
                        null,
                        sortOrderEvent
                );
            case TUTORIAL:
                return new CursorLoader(
                        this,
                        RecappContract.TutorialEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            case TUTORIAL_FILTER:
                String[] selectionArgs = new String[filtersConstraint.size()];
                filtersConstraint.toArray(selectionArgs);
                String selectionTutorialFilter = buildSelectionPlaceFilters(selectionArgs);
                return new CursorLoader(
                        this,
                        RecappContract.SubCategoryByTutorialEntry.buildSubCategoryByTutorialSubCategoryTutorial(),
                        new String[]{RecappContract.TutorialEntry.TABLE_NAME+"."+RecappContract.TutorialEntry._ID,
                                RecappContract.TutorialEntry.TABLE_NAME+"."+RecappContract.TutorialEntry.COLUMN_NAME,
                                RecappContract.TutorialEntry.COLUMN_DESCRIPTION,
                                RecappContract.TutorialEntry.COLUMN_LINK_VIDEO},
                        selectionTutorialFilter,
                        selectionArgs,
                        RecappContract.TutorialEntry.TABLE_NAME +"."+RecappContract.TutorialEntry.COLUMN_NAME+" ASC "
                );

        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Place> places;
        List<Event> events;
        List<Event> eventsGoing;
        List<Tutorial> tutorials;
        switch (loader.getId()){
            case USER:
                if(data.moveToFirst()){
                    userCursor = data;
                    user = new User();
                    user.setEmail(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_EMAIL)));
                    user.setId(data.getLong(data.getColumnIndexOrThrow(RecappContract.UserEntry._ID)));
                    user.setProfileImage(data.getBlob(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE)));
                    user.setName(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME)));
                    user.setLastName(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_LASTNAME)));
                    if(user!=null) {
                        mapFragment.setUser(user);
                    }
                    if(!user.getLastName().equals("") && !user.getName().equals("")){
                        name.setText(user.getName()+" "+user.getLastName());
                    }
                    if(!user.getEmail().equals("")){
                        email.setText(user.getEmail());
                    }
                    if (user.getProfileImage()!=null){
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 3;
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getProfileImage(), 0,
                                user.getProfileImage().length,options));
                    }

                    deepLinkIntent(deepLink);
                }
                break;

            case PLACE:
                if(filtersConstraint.isEmpty()) {
                    places = Place.allPlaces(data);
                    placesFragment.setData(places, data);
                    mapFragment.setDate(places,data);
                }
                break;
            case PLACE_FILTER:
                if(!filtersConstraint.isEmpty()) {
                    places = Place.allPlaces(data);
                    placesFragment.setData(places, data);
                    mapFragment.setDate(places,data);
                }
                break;
            case EVENT:
                events = Event.allEvents(data);
                eventsFragment.setDataEvents(events,data);
                break;
            case EVENT_GOING:
                eventsGoing = Event.allEvents(data);
                eventsFragment.setDataEventsGoing(eventsGoing, data);
                break;
            case TUTORIAL:
                if(filtersConstraint.isEmpty()) {
                    //Log.e("algo","algo");
                    tutorials = Tutorial.allTutorials(data);
                    tutorialsFragment.setDataTutorials(tutorials, data);
                    if(Utility.isNetworkAvailable(this)) {
                        addPreviewsToTutorial(tutorials);
                    }
                }
                break;
            case TUTORIAL_FILTER:
                if(!filtersConstraint.isEmpty()){
                    tutorials = Tutorial.allTutorials(data);
                    tutorialsFragment.setDataTutorials(tutorials, data);
                    if(Utility.isNetworkAvailable(this)) {
                        addPreviewsToTutorial(tutorials);
                    }
                }

                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        userCursor.close();
        placesFragment.closeData();
        mapFragment.closeData();
        eventsFragment.closeData();
        tutorialsFragment.closeData();
    }

    public String buildSelectionPlaceFilters(String[] filters){
        String where = null;
        if(filters!=null && filters.length>0){
            where = RecappContract.SubCategoryEntry.TABLE_NAME+"."+ RecappContract.SubCategoryEntry.COLUMN_NAME + " IN ( ? ";
            for (int i =1; i<filters.length; i++){
                where+=",? ";
            }
            where += " )";
        }
        return where;
    }

    public void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            searchView.setQuery(query,false);
            Snackbar.make(root.findViewById(R.id.coordination_navigation_drawer),"Place name : " +query,Snackbar.LENGTH_LONG).show();
            searchView.clearFocus();
            suggestions.saveRecentQuery(query, null);
            //searchView.invalidate();
            if(query.toUpperCase().equals("ALL PLACES") || query.toUpperCase().equals("TODOS LOS LUGARES")){
                if(!filtersConstraint.isEmpty()){
                    loadSelection();
                    resetLoaderFilter();
                }else{
                    selectionArgs=null;
                    selectionPlaces = null;
                    if (getSupportLoaderManager().getLoader(PLACE) == null) {
                        getSupportLoaderManager().initLoader(PLACE, null, this);
                    } else {
                        getSupportLoaderManager().restartLoader(PLACE, null, this);
                    }

                }
            }else {
                this.query = query;
                if (filtersConstraint.size() > 0) {

                    selectionArgs = new String[filtersConstraint.size()];
                    String[] selectionArgsTemp;
                    filtersConstraint.toArray(selectionArgs);
                    selectionPlaces = buildSelectionPlaceFilters(selectionArgs);
                    selectionArgsTemp = selectionArgs;
                    selectionArgs = new String[filtersConstraint.size() + 1];
                    for (int i = 0; i < selectionArgsTemp.length; i++) {
                        selectionArgs[i] = selectionArgsTemp[i];
                    }
                    selectionArgs[filtersConstraint.size()] = "%" + query + "%";
                    selectionPlaces += "  AND " + RecappContract.PlaceEntry.TABLE_NAME + "." + RecappContract.PlaceEntry.COLUMN_NAME + " LIKE ?";

                    resetLoaderFilter();
                } else {
                    selectionPlaces = RecappContract.PlaceEntry.TABLE_NAME + "." + RecappContract.PlaceEntry.COLUMN_NAME + " LIKE ?";
                    selectionArgs = new String[1];
                    selectionArgs[0] = "%" + query + "%";
                    if (getSupportLoaderManager().getLoader(PLACE) == null) {
                        getSupportLoaderManager().initLoader(PLACE, null, this);
                    } else {
                        getSupportLoaderManager().restartLoader(PLACE, null, this);
                    }
                }
            }

        }
    }
    public void loadSelection(){
        if (filtersConstraint.size() != 0) {
            selectionArgs = null;
            selectionPlaces = null;
            if (filtersConstraint.size() > 0) {
                selectionArgs = new String[filtersConstraint.size()];
                filtersConstraint.toArray(selectionArgs);
                selectionPlaces = buildSelectionPlaceFilters(selectionArgs);
            }

        }
    }
    public void deepLinkIntent(String deepLink){
        if(deepLink!=null && !deepLink.equals("")){
            String [] segments = deepLink.split("/");
            Intent intent = new Intent(NavigationDrawer.this, Detail.class);
            intent.putExtra("id", Long.parseLong(segments[segments.length - 1]));
            intent.putExtra("user", user);
            startActivity(intent);

        }
    }
    public void resetLoaderFilter(){
        if (getSupportLoaderManager().getLoader(PLACE_FILTER) == null) {
            getSupportLoaderManager().initLoader(PLACE_FILTER, null, this);
        } else {
            getSupportLoaderManager().restartLoader(PLACE_FILTER, null, this);
        }
        if (getSupportLoaderManager().getLoader(TUTORIAL_FILTER) == null) {
            getSupportLoaderManager().initLoader(TUTORIAL_FILTER, null, this);
        } else {
            getSupportLoaderManager().restartLoader(TUTORIAL_FILTER, null, this);
        }
    }
    private void addPreviewsToTutorial(List<Tutorial> tutorials){
        for (int i = 0; i < tutorials.size(); i++){
            Tutorial tutorial = tutorials.get(i);
            getPreviewFromYouTube(tutorial);

        }
    }

    private Bitmap getPreviewFromYouTube(Tutorial tutorial){
        Bitmap bitmap = null;

        Uri.Builder uri = new Uri.Builder();
        uri.scheme("https");
        uri.authority("www.youtube.com/oembed");
        uri.appendQueryParameter("url", tutorial.getLink());
        uri.appendQueryParameter("format", "json");

        try {
            URL url = new URL(URLDecoder.decode(uri.build().toString(), "UTF-8"));
            new URLPreviewYouTube().execute(new Object[]{tutorial, url.toString()});
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    private class URLPreviewYouTube extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... objects) {
            Tutorial tutorial = (Tutorial) objects[0];
            String data = (String) objects[1];
            StringBuilder description = null;
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String thumbnailURL = null;
            try {
                URL url = new URL(data);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                description = new StringBuilder();
                String line="";
                while ((line=br.readLine())!=null)
                    description.append(line);
                Log.d("Getting description", description.toString());
                JSONObject object = new JSONObject(description.toString());
                thumbnailURL = object.getString("thumbnail_url");


            }catch (Exception e){
                Log.d("Excepcion ", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }
            return new Object[]{tutorial, thumbnailURL};
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            Tutorial tutorial = (Tutorial) objects[0];
            String thumbnailURL = (String) objects[1];
            tutorial.setPreviewURL(thumbnailURL);
            if(Utility.isNetworkAvailable(getApplicationContext())) {
                new PreviewYouTube().execute(tutorial);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private class PreviewYouTube extends AsyncTask<Tutorial, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Tutorial... tutorials) {
            Tutorial tutorial = tutorials[0];
            String data = tutorial.getPreviewURL();
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            Bitmap image = null;
            try {
                URL url = new URL(data);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                image = BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                Log.d("Excepcion ", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }
            return new Object[]{tutorial, image};
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            super.onPostExecute(objects);
            Tutorial tutorial = (Tutorial) objects[0];
            Bitmap bitmap = (Bitmap) objects[1];
            tutorial.setPreview(bitmap);
            tutorialsFragment.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private class NewsAsync extends AsyncTask<String,Void,Void>{
        private boolean swipe;

        public NewsAsync() {
        }

        public NewsAsync(boolean swipe) {
            this.swipe = swipe;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String urlAddress = strings[0];
            StringBuilder newsJson;
            HttpURLConnection urlConnection = null;
            HttpURLConnection urlConnectionImage =  null;
            news = new ArrayList<>();
            try {
                URL url = new URL(urlAddress);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = "";
                newsJson = new StringBuilder();

                while ((line=br.readLine())!=null){
                    newsJson.append(line);
                }
                JSONObject object = new JSONObject(newsJson.toString());
                JSONObject response = object.getJSONObject("responseData");
                JSONArray results = response.getJSONArray("results");

                for(int i=0; i<results.length(); i++){
                    News mNews = new News();
                    mNews.setDescription(android.text.Html.fromHtml(results.getJSONObject(i).getString("content")).toString());
                    mNews.setTitle(android.text.Html.fromHtml(results.getJSONObject(i).getString("titleNoFormatting")).toString());
                    mNews.setEditor(results.getJSONObject(i).getString("publisher"));
                    mNews.setDate(results.getJSONObject(i).getString("publishedDate"));
                    mNews.setUrl(results.getJSONObject(i).getString("unescapedUrl"));
                    if(results.getJSONObject(i).has("image")){
                        JSONObject images = results.getJSONObject(i).getJSONObject("image");

                        URL urlImage = new URL(images.getString("url"));
                        urlConnectionImage = (HttpURLConnection) urlImage.openConnection();
                        urlConnectionImage.connect();
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = BitmapFactory.decodeStream(urlConnectionImage.getInputStream(),null,options);
                        mNews.setImage(bitmap);
                    }
                    news.add(mNews);
                }

            }catch (Exception e){
                Log.e("NewsFragment", e.toString());
            }finally {
                urlConnection.disconnect();
                if(urlConnectionImage!=null){
                    urlConnectionImage.disconnect();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newsFragment.setNews(news,swipe);
            if(swipe){
                newsFragment.swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public void callNewsRefresh(String url){
        if(Utility.isNetworkAvailable(this)) {
            new NewsAsync(true).execute(url);
        }

    }


}
