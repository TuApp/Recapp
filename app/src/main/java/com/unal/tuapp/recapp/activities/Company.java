package com.unal.tuapp.recapp.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.PlaceImage;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.fragments.CompanyCommentsFragment;
import com.unal.tuapp.recapp.fragments.CompanyEventsFragment;
import com.unal.tuapp.recapp.fragments.CompanyImagesFragment;
import com.unal.tuapp.recapp.fragments.CompanyInformationFragment;
import com.unal.tuapp.recapp.fragments.CompanyMainFragment;
import com.unal.tuapp.recapp.fragments.CompanyPointsFragment;
import com.unal.tuapp.recapp.others.OnSendDataToActivity;
import com.unal.tuapp.recapp.others.OnSendDataToActivityPlace;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceImageEndPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by andresgutierrez on 11/3/15.
 */
public class Company extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,OnSendDataToActivity,OnSendDataToActivityPlace{
    private FloatingActionButton companyEvent;
    private FloatingActionButton companyImages;
    private FloatingActionButton companyImagesCamera;
    private FloatingActionButton companyImagesGallery;
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
    private Fragment companyImagesFragment;
    private Fragment companyPointsFragment;
    private String menu;
    private boolean addImages;
    private String imagePath;
    private Bitmap image;
    private PendingIntent pendingIntent;
    private final String TAG = Company.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_company, null);
        setContentView(root);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        menu = "home";
        if(savedInstanceState!=null){
            menu = savedInstanceState.getString("type");
            addImages = savedInstanceState.getBoolean("addImage");
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
        companyImages = (FloatingActionButton) root.findViewById(R.id.company_add_image);
        companyImagesCamera = (FloatingActionButton) root.findViewById(R.id.company_add_image_camera);
        companyImagesGallery = (FloatingActionButton) root.findViewById(R.id.company_add_image_gallery);

        companyImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImages =  addImages ?false:true;
                if(addImages){
                    companyImages.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    companyImagesCamera.show();
                    companyImagesGallery.show();
                }else{
                    companyImages.setImageResource(R.drawable.ic_add_white_24dp);
                    companyImagesGallery.hide();
                    companyImagesCamera.hide();
                }
            }
        });
        companyImagesGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePicture = new Intent(Intent.ACTION_GET_CONTENT);
                choosePicture.setType("image/*");
                startActivityForResult(choosePicture,2);
            }
        });
        companyImagesCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File photo = null;
                try {
                    photo = createImageFile();
                }catch (Exception e){}
                if(photo!=null) {
                    imagePath = photo.getAbsolutePath();
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getPackageManager()) != null) {
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photo));
                        startActivityForResult(takePicture, 1);
                    }
                }
            }
        });



        companyImage = (de.hdodenhof.circleimageview.CircleImageView) root.findViewById(R.id.favorite_image);
        companyName = (TextView) root.findViewById(R.id.company_name);
        companyHome = new CompanyMainFragment();
        companyInformation = new CompanyInformationFragment();
        companyComments = new CompanyCommentsFragment();
        companyEventFragment =  new CompanyEventsFragment();
        companyImagesFragment = new CompanyImagesFragment();
        companyPointsFragment = new CompanyPointsFragment();
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
                            ft.hide(companyImagesFragment);
                            ft.hide(companyPointsFragment);
                            ft.commit();
                            companyEvent.hide();
                            companyImages.hide();
                            if(addImages){
                                companyImagesCamera.hide();
                                companyImagesGallery.hide();
                            }
                            menu = "home";
                            break;
                        case R.id.information:
                            if (companyInformation.isAdded()) {
                                ft.show(companyInformation);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyComments);
                            ft.hide(companyEventFragment);
                            ft.hide(companyImagesFragment);
                            ft.hide(companyPointsFragment);

                            ft.commit();
                            companyEvent.hide();
                            companyImages.hide();

                            if(addImages){
                                companyImagesCamera.hide();
                                companyImagesGallery.hide();
                            }
                            menu = "information";
                            break;
                        case R.id.comments:
                            if(companyComments.isAdded()){
                                ft.show(companyComments);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyEventFragment);
                            ft.hide(companyImagesFragment);
                            ft.hide(companyPointsFragment);

                            ft.commit();
                            companyEvent.hide();
                            companyImages.hide();
                            if(addImages){
                                companyImagesCamera.hide();
                                companyImagesGallery.hide();
                            }
                            menu = "comments";
                            break;
                        case R.id.events:
                            if(companyEventFragment.isAdded()){
                                ft.show(companyEventFragment);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyComments);
                            ft.hide(companyImagesFragment);
                            ft.hide(companyPointsFragment);

                            ft.commit();
                            companyEvent.show();
                            companyImages.hide();
                            if(addImages){
                                companyImagesCamera.hide();
                                companyImagesGallery.hide();
                            }
                            menu = "events";
                            break;
                        case R.id.images:
                            if(companyImagesFragment.isAdded()){
                                ft.show(companyImagesFragment);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyComments);
                            ft.hide(companyEventFragment);
                            ft.hide(companyPointsFragment);

                            ft.commit();
                            companyImages.show();
                            companyImages.setImageResource(R.drawable.ic_add_white_24dp);
                            companyImagesCamera.hide();
                            companyImagesGallery.hide();
                            companyEvent.hide();
                            addImages = false;
                            menu = "images";
                            break;
                        case R.id.points:
                            if(companyPointsFragment.isAdded()){
                                ft.show(companyPointsFragment);
                            }
                            ft.hide(companyHome);
                            ft.hide(companyInformation);
                            ft.hide(companyComments);
                            ft.hide(companyImagesFragment);
                            ft.hide(companyEventFragment);

                            ft.commit();
                            companyEvent.hide();
                            companyImages.hide();
                            if(addImages){
                                companyImagesCamera.hide();
                                companyImagesGallery.hide();
                            }
                            menu = "points";

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
                fragmentTransaction.add(R.id.company_container, companyComments, "comments");
                fragmentTransaction.add(R.id.company_container, companyEventFragment,"events");
                fragmentTransaction.add(R.id.company_container, companyImagesFragment,"images");
                fragmentTransaction.add(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.hide(companyPointsFragment);
                fragmentTransaction.hide(companyImagesFragment);
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
                fragmentTransaction.add(R.id.company_container, companyImagesFragment,"images");
                fragmentTransaction.add(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.hide(companyPointsFragment);
                fragmentTransaction.hide(companyImagesFragment);
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
                fragmentTransaction.add(R.id.company_container, companyImagesFragment,"images");
                fragmentTransaction.add(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.hide(companyPointsFragment);
                fragmentTransaction.hide(companyImagesFragment);
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
                fragmentTransaction.add(R.id.company_container, companyImagesFragment,"images");
                fragmentTransaction.add(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.hide(companyPointsFragment);
                fragmentTransaction.hide(companyImagesFragment);
                fragmentTransaction.hide(companyComments);
                fragmentTransaction.hide(companyInformation);
                fragmentTransaction.hide(companyHome);
                fragmentTransaction.commit();
                companyEvent.show();
                navDrawer.getMenu().findItem(R.id.events).setChecked(true);
                break;
            case "images":
                fragmentTransaction.replace(R.id.company_container, companyImagesFragment, "images");
                fragmentTransaction.add(R.id.company_container, companyHome, "home");
                fragmentTransaction.add(R.id.company_container, companyInformation, "information");
                fragmentTransaction.add(R.id.company_container, companyComments, "comments");
                fragmentTransaction.add(R.id.company_container, companyEventFragment, "events");
                fragmentTransaction.add(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.hide(companyPointsFragment);
                fragmentTransaction.hide(companyHome);
                fragmentTransaction.hide(companyEventFragment);
                fragmentTransaction.hide(companyInformation);
                fragmentTransaction.hide(companyComments);
                fragmentTransaction.commit();
                companyImages.show();
                if(addImages){
                    companyImagesCamera.show();
                    companyImagesGallery.show();
                }
                companyEvent.hide();
                navDrawer.getMenu().findItem(R.id.images).setChecked(true);
                break;
            case "points":
                fragmentTransaction.replace(R.id.company_container, companyPointsFragment, "points");
                fragmentTransaction.add(R.id.company_container, companyInformation, "information");
                fragmentTransaction.add(R.id.company_container, companyHome, "home");
                fragmentTransaction.add(R.id.company_container, companyComments,"comments");
                fragmentTransaction.add(R.id.company_container, companyImagesFragment,"images");
                fragmentTransaction.add(R.id.company_container, companyEventFragment, "event");
                fragmentTransaction.hide(companyImagesFragment);
                fragmentTransaction.hide(companyComments);
                fragmentTransaction.hide(companyInformation);
                fragmentTransaction.hide(companyHome);
                fragmentTransaction.hide(companyEventFragment);
                fragmentTransaction.commit();
                companyEvent.hide();
                navDrawer.getMenu().findItem(R.id.points).setChecked(true);

                break;
        }

        if(getSupportLoaderManager().getLoader(PLACE)==null){
            getSupportLoaderManager().initLoader(PLACE,null,this);
        }else{
            getSupportLoaderManager().restartLoader(PLACE,null,this);
        }


    }



    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(getIntent().getExtras()!=null){
            intent.putExtra("email",getIntent().getExtras().getString("email"));
            intent.putExtra("id", getIntent().getExtras().getLong("id"));
            setIntent(intent);
        }
        resolveIntent(intent);
    }
    public void  connectMifare(final MifareClassic mifare) {
        Log.e("algo",""+placeId);
        Utility.readMifare(mifare, this,placeId);


    }

    private void resolveIntent(Intent intent) {
        Log.i(TAG, "resolving intent");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            Log.i(TAG, "found a tag");
            MifareClassic mifare = MifareClassic.get(tag);

            connectMifare(mifare);

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
        outState.putBoolean("addImage",addImages);
    }
    @Override
    public void onBackPressed() {}

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFilename = "JPEG_"+timeStamp+"_";
        File storeDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        File image = File.createTempFile(
                imageFilename,
                ".jpg",
                storeDir
        );
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,root.getWidth()/2,root.getHeight(),true);
            image = bitmapScaled;

        }
        if(requestCode==2 && resultCode==Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), data.getData());
                Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap,root.getWidth()/2,root.getHeight(),true);
                image = bitmapScaled;
            }catch (Exception e){

            }

        }
        PlaceImage placeImage = new PlaceImage();
        placeImage.setId(System.currentTimeMillis());
        placeImage.setPlaceId(placeId);
        placeImage.setWorth(5);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        placeImage.setImage(Utility.encodeImage(stream.toByteArray()));
        ContentValues values = new ContentValues();
        values.put(RecappContract.PlaceImageEntry.COLUMN_IMAGE,stream.toByteArray());
        values.put(RecappContract.PlaceImageEntry.COLUMN_PLACE_KEY,placeImage.getPlaceId());
        values.put(RecappContract.PlaceImageEntry._ID,placeImage.getId());
        values.put(RecappContract.PlaceImageEntry.COLUMN_WORTH,placeImage.getWorth());
        getContentResolver().insert(
                RecappContract.PlaceImageEntry.CONTENT_URI,
                values
        );
        Pair<Pair<Context,Long>,Pair<PlaceImage,String>> pair = new Pair<>(new Pair<>(getApplicationContext(),-1L),
                new Pair<>(placeImage,"addImage"));
        new PlaceImageEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);
        companyImagesGallery.hide();
        companyImagesCamera.hide();
        companyImages.setImageResource(R.drawable.ic_add_white_24dp);

    }

    @Override
    public void sendData(User user) {
        ((CompanyPointsFragment) companyPointsFragment).showPoint(user);
    }

    @Override
    public void onSendDataPlace(com.unal.tuapp.recapp.backend.model.placeApi.model.Place place) {
        ((CompanyPointsFragment) companyPointsFragment).showCompanyPoint(place);
    }
}
