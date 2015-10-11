package com.unal.tuapp.recapp;


import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.data.Category;
import com.unal.tuapp.recapp.data.MySuggestionProvider;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.SubCategory;
import com.unal.tuapp.recapp.data.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private Button addCategory;
    private final String TAG = NavigationDrawer.class.getSimpleName();
    private final String FILE = "filters.txt";
    private static final int USER = 10;
    private static final int CATEGORY = 199;
    private static final int SUB_CATEGORY = 299;
    private static final int PLACE = 345;
    private static final int PLACE_FILTER = 467;
    private Cursor userCursor;
    private MapFragment mapFragment;
    private  PlacesFragment placesFragment;
    private MultiAutoCompleteTextView filterCategory;
    private LimitArrayAdapterCategory adapter;
    private LimitArrayAdapterSubCategory adapterSubCategory;
    private AutoCompleteTextView filterSubcategory;
    private String [] subCategory;
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        setContentView(root);
        eventCreate = (FloatingActionButton) root.findViewById(R.id.event_create);
        query = "";
        totalFilter = 0;
        mGooglePlus = GooglePlus.getInstance(this, null, null);
        filtersConstraint = new ArrayList<>();
        savedInstance = false;
        if(savedInstanceState!=null){
            savedInstance = true;
        }
        email = (TextView) findViewById(R.id.user_email);
        name = (TextView) findViewById(R.id.user_name);

        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);
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

                Utility.addUser(this,emailUser,
                        currentPerson.getName().getGivenName(), currentPerson.getName().getFamilyName());
                new LoadProfileImage(root, imageView).execute(personPhotoUrl, account.getAccountName(mGooglePlus.mGoogleApiClient));
            }else {
                emailUser = getIntent().getExtras().getString("email");
                Utility.addUser(this,emailUser,"","");
                email.setText(emailUser);

            }

            if(getSupportLoaderManager().getLoader(USER)==null) {
                getSupportLoaderManager().initLoader(USER, null, this);
            }else{
                getSupportLoaderManager().restartLoader(USER,null,this);
            }
            if(getSupportLoaderManager().getLoader(CATEGORY)==null){
                getSupportLoaderManager().initLoader(CATEGORY,null,this);
            }else{
                getSupportLoaderManager().restartLoader(CATEGORY,null,this);
            }
            if(getSupportLoaderManager().getLoader(PLACE)==null){
                getSupportLoaderManager().initLoader(PLACE,null,this);
            }else{
                getSupportLoaderManager().restartLoader(PLACE,null,this);
            }
            resetLoaderFilter();



        }
        filterCategory = (MultiAutoCompleteTextView) findViewById(R.id.filter_text);
        List<Category> categories  = new ArrayList<>();
        filterCategory.setTokenizer(new Rfc822Tokenizer());
        adapter = new LimitArrayAdapterCategory(this,
              categories);

        filterCategory.setThreshold(1);
        filterCategory.setAdapter(adapter);

        List<SubCategory> subCategories = new ArrayList<>();
        filterSubcategory = (AutoCompleteTextView) findViewById(R.id.filter_subcategory);
        adapterSubCategory = new LimitArrayAdapterSubCategory(this,subCategories);
        filterSubcategory.setThreshold(1);
        filterSubcategory.setAdapter(adapterSubCategory);


        addCategory = (Button) root.findViewById(R.id.add_category);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] text = filterCategory.getText().toString().split(", ");
                if (!Arrays.deepEquals(text, subCategory)) {
                    subCategory = text;
                    if (getSupportLoaderManager().getLoader(SUB_CATEGORY) == null) {
                        getSupportLoaderManager().initLoader(SUB_CATEGORY, null, NavigationDrawer.this);
                    } else {
                        getSupportLoaderManager().restartLoader(SUB_CATEGORY, null, NavigationDrawer.this);
                    }
                }
            }
        });
        add = (Button) root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText editText = (EditText) root.findViewById(R.id.filter_text);
                if(!filterSubcategory.getText().toString().trim().equals("")){
                    Menu menu = navFilterDrawer.getMenu();
                    String menuFilter = filterSubcategory.getText().toString();
                    boolean isAdded = false;
                    for(int i=0; i<menu.size(); i++){
                        if(menuFilter.toLowerCase().equals(menu.getItem(i).toString().toLowerCase())){
                            isAdded = true;
                            break;
                        }
                    }
                    if(!isAdded) {
                        filtersConstraint.add(menuFilter);
                        menu.add(0, totalFilter, Menu.NONE, menuFilter).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                        totalFilter++;
                        selectionArgs=null;
                        selectionPlaces=null;
                        if(query!=null && query.length()>0) {
                            handleIntent(getIntent());
                        }else {
                            loadSelection();
                            resetLoaderFilter();
                        }
                        //((PlacesFragment)(((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()))).setFilters(filtersConstraint);

                    }
                    filterSubcategory.setText("");


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
                Intent intent = new Intent(NavigationDrawer.this,EventActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==2){
                    eventCreate.setVisibility(View.VISIBLE);
                }else{
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
                filtersConstraint.remove(menuItem.toString());
                totalFilter--;
                if(query!=null && query.length()>0) {
                    handleIntent(getIntent());
                }else if(filtersConstraint.size()>0) {
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

                }

                //((PlacesFragment)(((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()))).setFilters(filtersConstraint);
                return false;
            }
        });
        deepLink = PlusShare.getDeepLinkId(getIntent());


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
        drawerToggle.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles

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
                intent.putExtra("id",place.getId());
                intent.putExtra("user",user);
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
            case CATEGORY:
                return new CursorLoader(
                        this,
                        RecappContract.CategoryEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            case SUB_CATEGORY:
                String selection = buildSelection(subCategory);
                return new CursorLoader(
                        this,
                        RecappContract.SubCategoryEntry.buildSubCategoryCategoryUri(),
                        new String[]{RecappContract.SubCategoryEntry.TABLE_NAME+"."+ RecappContract.SubCategoryEntry._ID,
                                RecappContract.SubCategoryEntry.TABLE_NAME+"."+ RecappContract.SubCategoryEntry.COLUMN_NAME,
                                RecappContract.CategoryEntry.TABLE_NAME+"."+ RecappContract.CategoryEntry.COLUMN_IMAGE},
                        selection,
                        subCategory,
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

        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Place> places;
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
                    if (user.getProfileImage()!=null){
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getProfileImage(), 0,
                                user.getProfileImage().length));
                    }
                    deepLinkIntent(deepLink);
                }
                break;
            case CATEGORY:
                List<Category> categories = Category.allCategories(data);
                adapter.setData(categories);
                break;
            case SUB_CATEGORY:
                List<SubCategory> subCategories = SubCategory.allSubCategories(data);
                adapterSubCategory.setData(subCategories);

                if(subCategories.size()>0){
                    add.setVisibility(View.VISIBLE);
                }else{
                    add.setVisibility(View.GONE);
                }
                break;
            case PLACE:
                if(filtersConstraint.size()==0) {
                    places = Place.allPlaces(data);
                    placesFragment.setData(places, data);
                    mapFragment.setDate(places,data);
                }
                break;
            case PLACE_FILTER:
                if(filtersConstraint.size()>0) {
                    places = Place.allPlaces(data);
                    placesFragment.setData(places, data);
                    mapFragment.setDate(places,data);
                }
                break;


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        userCursor.close();
        placesFragment.closeData();
        mapFragment.closeData();
    }
    public String buildSelection(String [] category){
        String where = null;
        if(category!=null && category.length>0){
            where = RecappContract.CategoryEntry.TABLE_NAME+"."+ RecappContract.CategoryEntry.COLUMN_NAME + " IN ( ? ";
            for (int i =1; i<category.length; i++){
                where+=",? ";
            }
            where += " )";
        }
        return where;
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
                if(filtersConstraint.size()>0){
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
    }
}
