package com.unal.tuapp.recapp.activities;



import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.fragments.ContestFragment;
import com.unal.tuapp.recapp.fragments.MyPointsFragment;
import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.others.OnSendDataToActivity;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.LoadProfileImage;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.fragments.CommentsFragment;
import com.unal.tuapp.recapp.fragments.MyEventsFragment;
import com.unal.tuapp.recapp.fragments.PlacesFavoriteFragment;
import com.unal.tuapp.recapp.fragments.RemindersFragment;


public class UserDetail extends AppCompatActivity implements CommentsFragment.OnCommentListener,OnSendDataToActivity {
    private  Toolbar toolbar;
    private  NavigationView navigationView;
    private  DrawerLayout drawerLayout;
    private  GooglePlus googlePlus;
    private  static View root;
    private  User user;
    private  Fragment fragmentPlace;
    private  Fragment fragmentComment;
    private  Fragment fragmentReminder;
    private  Fragment fragmentEvents;
    private  Fragment fragmentContest;
    private  static Fragment fragmentPoints;
    private  String newType;
    private  AdView mAdView;
    private  String TAG = UserDetail.class.getSimpleName();
    private  TextView name;
    private  TextView email;
    private  de.hdodenhof.circleimageview.CircleImageView imageView;
    private  PendingIntent pendingIntent;
    private  String point;
    private  String emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_user_detail,null);
        setContentView(root);
        mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        Bundle extras = getIntent().getExtras();
        name = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.user_email);
        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile);

        if(extras!=null){
            user = extras.getParcelable("user");
            newType = extras.getString("type");
        }
        if(savedInstanceState!=null){
            newType = savedInstanceState.getString("type");
            point = savedInstanceState.getString("points");
        }

        googlePlus = GooglePlus.getInstance(this, null, null);
        if (googlePlus.mGoogleApiClient.isConnected()) {
            //Account account = Plus.AccountApi;
            if(user!=null) {
                name.setText(user.getName() + " " + user.getLastName());

                email.setText(user.getEmail());


                if (user.getProfileImage() != null) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getProfileImage(), 0,
                            user.getProfileImage().length,options));
                }
                else if (Utility.isNetworkAvailable(this)) {
                    Person currentPerson = Plus.PeopleApi.getCurrentPerson(googlePlus.mGoogleApiClient);
                    String personPhotoUrl = currentPerson.getImage().getUrl();
                    //We try to request a image with major size, the new image will be of 600*600 pixels
                    //The user doesn't have a image so we try to download one and put it to the user
                    personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + googlePlus.PROFILE_PIC_SIZE;
                    new LoadProfileImage(root, imageView).execute(personPhotoUrl, user.getEmail());
                }
            }else if(Utility.isNetworkAvailable(this)){
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googlePlus.mGoogleApiClient);
                Account account = Plus.AccountApi;
                String personPhotoUrl = currentPerson.getImage().getUrl();
                //We try to request a image with major size, the new image will be of 600*600 pixels
                personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + googlePlus.PROFILE_PIC_SIZE;


                name.setText(currentPerson.getDisplayName());

                emailUser = account.getAccountName(googlePlus.mGoogleApiClient);
                email.setText(emailUser);

                try{
                    Utility.addUser(this,emailUser,
                            currentPerson.getName().getGivenName(), currentPerson.getName().getFamilyName());
                }catch (Exception e){}
                new LoadProfileImage(root, imageView).execute(personPhotoUrl, account.getAccountName(googlePlus.mGoogleApiClient));
            }


        }
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) root.findViewById(R.id.nav_drawer);
        drawerLayout = (DrawerLayout) root.findViewById(R.id.user_detail);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentPlace = new PlacesFavoriteFragment();
        ((PlacesFavoriteFragment) fragmentPlace).setOnPlaceListener(new PlacesFavoriteFragment.onPlaceListener() {
                @Override
                public void onPlace(View view, long position) {
                    Intent intent = new Intent(UserDetail.this, Detail.class);
                    intent.putExtra("id", position);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

        });
        fragmentComment = new CommentsFragment();
        fragmentReminder = new RemindersFragment();
        fragmentEvents = new MyEventsFragment();
        fragmentPoints = new MyPointsFragment();
        fragmentContest = new ContestFragment();
        ((MyEventsFragment) fragmentEvents).setOnEventListener(new MyEventsFragment.OnEventListener() {
            @Override
            public void onAction(long id) {
                Intent intent = new Intent(UserDetail.this, EventUpdateActivity.class);
                intent.putExtra("event", id);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Intent intentHome = new Intent(UserDetail.this, NavigationDrawer.class);
                        intentHome.putExtra("email", user.getEmail());
                        startActivity(intentHome);
                    case R.id.favorites:
                        getSupportActionBar().setTitle(R.string.my_favorites);
                        newType = "favorite";
                        if (fragmentPlace.isAdded()) {
                            fragmentTransaction.show(fragmentPlace);
                        }

                        fragmentTransaction.hide(fragmentComment);
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.setCommentPositon(-1);
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.notifyDataSetChanged();
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.hide(fragmentEvents);
                        fragmentTransaction.hide(fragmentPoints);
                        fragmentTransaction.hide(fragmentContest);
                        fragmentTransaction.commit();
                        break;
                    case R.id.appointments:
                        getSupportActionBar().setTitle(R.string.my_appointments);
                        newType = "reminder";
                        if (fragmentReminder.isAdded()) {
                            fragmentTransaction.show(fragmentReminder);
                        }
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.setCommentPositon(-1);
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.notifyDataSetChanged();
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.hide(fragmentEvents);
                        fragmentTransaction.hide(fragmentPoints);
                        fragmentTransaction.hide(fragmentContest);
                        fragmentTransaction.commit();
                        break;
                    case R.id.comments:
                        getSupportActionBar().setTitle(R.string.my_comments);
                        newType = "comment";
                        if (fragmentComment.isAdded()) {
                            fragmentTransaction.show(fragmentComment);
                        }
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.setCommentPositon(-1);
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.notifyDataSetChanged();
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.hide(fragmentEvents);
                        fragmentTransaction.hide(fragmentPoints);
                        fragmentTransaction.hide(fragmentContest);
                        fragmentTransaction.commit();

                        break;
                    case R.id.events:
                        getSupportActionBar().setTitle(R.string.my_events);
                        newType = "event";
                        if (fragmentEvents.isAdded()) {
                            fragmentTransaction.show(fragmentEvents);
                        }
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.setCommentPositon(-1);
                        ((CommentsFragment) fragmentComment).recycleCommentsAdapter.notifyDataSetChanged();
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.hide(fragmentPoints);
                        fragmentTransaction.hide(fragmentContest);
                        fragmentTransaction.commit();
                        break;
                    case R.id.sign_out:
                        if (GooglePlus.mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(GooglePlus.mGoogleApiClient);
                            GooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(UserDetail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;
                        }
                        break;
                    case R.id.disconnect:
                        if (GooglePlus.mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(GooglePlus.mGoogleApiClient);
                            Plus.AccountApi.revokeAccessAndDisconnect(GooglePlus.mGoogleApiClient);
                            GooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(UserDetail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;
                        }
                        break;
                    case R.id.points:
                        getSupportActionBar().setTitle(R.string.points);
                        newType = "points";
                        if (fragmentPoints.isAdded()) {
                            fragmentTransaction.show(fragmentPoints);
                        }
                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.hide(fragmentEvents);
                        fragmentTransaction.hide(fragmentContest);
                        fragmentTransaction.commit();

                        break;
                    case R.id.contest:
                        getSupportActionBar().setTitle(R.string.contest);
                        newType = "contest";
                        if (fragmentContest.isAdded()) {
                            fragmentTransaction.show(fragmentContest);
                        }
                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.hide(fragmentEvents);
                        fragmentTransaction.hide(fragmentPoints);
                        fragmentTransaction.commit();
                        break;

                }
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return false;
            }
        });
        switch (newType) {
            case "favorite":
                getSupportActionBar().setTitle(R.string.my_favorites);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentContest);
                fragmentTransaction.hide(fragmentPoints);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.hide(fragmentEvents);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.favorites).setChecked(true);
                break;
            case "comment":
                getSupportActionBar().setTitle(R.string.my_comments);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentContest);
                fragmentTransaction.hide(fragmentPoints);
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.hide(fragmentEvents);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.comments).setChecked(true);
                break;
            case "reminder":
                //We should delete all reminders which endDate < now
                getSupportActionBar().setTitle(R.string.my_appointments);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentContest);
                fragmentTransaction.hide(fragmentPoints);
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentEvents);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.appointments).setChecked(true);
                break;
            case "event":
                getSupportActionBar().setTitle(R.string.my_events);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentContest);
                fragmentTransaction.hide(fragmentPoints);
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.events).setChecked(true);
                break;
            case "points":
                getSupportActionBar().setTitle(R.string.points);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentContest);
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.hide(fragmentEvents);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.points).setChecked(true);
                break;
            case "contest":
                getSupportActionBar().setTitle(R.string.contest);
                fragmentTransaction.replace(R.id.user_detail_container, fragmentPoints, "points");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentEvents, "event");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentContest, "contest");
                fragmentTransaction.hide(fragmentPoints);
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.hide(fragmentEvents);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.contest).setChecked(true);
                break;

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
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
        if(android.R.id.home==id){
            Intent intentHome = new Intent(UserDetail.this, NavigationDrawer.class);
            intentHome.putExtra("email",user.getEmail());
            startActivity(intentHome);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("type", newType);
        savedInstanceState.putString("points",point);
    }
    @Override
    public void onBackPressed() {

    }



    @Override
    public void onCommentDelete(boolean comment) {
        if(comment){
           PlacesFavoriteFragment placesFavoriteFragment = (PlacesFavoriteFragment)getSupportFragmentManager()
                   .findFragmentByTag("favorite");
            if(placesFavoriteFragment!=null){
                placesFavoriteFragment.resetLoader();
            }

        }
    }

    @Override
    public void sendData(Long user) {
        ((MyPointsFragment) fragmentPoints).showPoint(user);
    }
}
