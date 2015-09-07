package com.unal.tuapp.recapp;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unal.tuapp.recapp.data.User;

public class UserDetail extends AppCompatActivity implements CommentsFragment.OnCommentListener {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private GooglePlus googlePlus;
    private View root;
    private User user;
    private Fragment fragmentPlace;
    private Fragment fragmentComment;
    private Fragment fragmentReminder;
    private String newType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_user_detail,null);
        setContentView(root);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
            newType = extras.getString("type");
        }
        if(savedInstanceState!=null){
            newType = savedInstanceState.getString("newType");
        }
        googlePlus = GooglePlus.getInstance(this,null,null);
        if(googlePlus.mGoogleApiClient.isConnected()){
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
            }else{
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googlePlus.mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();
                //We try to request a image with major size, the new image will be of 600*600 pixels
                //The user doesn't have a image so we try to download one and put it to the user
                personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length()-2) + googlePlus.PROFILE_PIC_SIZE;
                new LoadProfileImage(root,imageView).execute(personPhotoUrl,user.getEmail());
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.home:
                        Intent intentHome = new Intent(UserDetail.this,NavigationDrawer.class);
                        startActivity(intentHome);
                    case R.id.favorites:
                        getSupportActionBar().setTitle("My Favorites");
                        newType = "favorite";
                        if(fragmentPlace.isAdded()){
                            fragmentTransaction.show(fragmentPlace);
                        }

                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.commit();
                        break;
                    case R.id.appointments:
                        getSupportActionBar().setTitle("My Reminders");
                        newType = "reminder";
                        if(fragmentReminder.isAdded()){
                            fragmentTransaction.show(fragmentReminder);
                        }
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentComment);
                        fragmentTransaction.commit();
                        break;
                    case R.id.comments:
                        getSupportActionBar().setTitle("My Comments");
                        newType = "comment";
                        if(fragmentComment.isAdded()){
                            fragmentTransaction.show(fragmentComment);
                        }
                        fragmentTransaction.hide(fragmentPlace);
                        fragmentTransaction.hide(fragmentReminder);
                        fragmentTransaction.commit();

                        break;
                    case R.id.sign_out:
                        if(GooglePlus.mGoogleApiClient.isConnected()){
                            Plus.AccountApi.clearDefaultAccount(GooglePlus.mGoogleApiClient);
                            GooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(UserDetail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                    case R.id.disconnect:
                        if(GooglePlus.mGoogleApiClient.isConnected()){
                            Plus.AccountApi.clearDefaultAccount(GooglePlus.mGoogleApiClient);
                            Plus.AccountApi.revokeAccessAndDisconnect(GooglePlus.mGoogleApiClient);
                            GooglePlus.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(UserDetail.this, Recapp.class);
                            startActivity(intent);
                            //animation = false;

                        }
                        break;
                }
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return false;

            }
        });

        switch (newType){
            case "favorite":
                getSupportActionBar().setTitle("My Favorites");
                fragmentTransaction.replace(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.favorites).setChecked(true);
                break;
            case "comment":
                getSupportActionBar().setTitle("My Comments");
                fragmentTransaction.replace(R.id.user_detail_container, fragmentComment, "comment");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container,fragmentReminder,"reminder");
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentReminder);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.comments).setChecked(true);
                break;
            case "reminder":
                //We should delete all reminders which endDate < now
                getSupportActionBar().setTitle("My reminders");
                fragmentTransaction.replace(R.id.user_detail_container, fragmentReminder, "reminder");
                fragmentTransaction.add(R.id.user_detail_container, fragmentPlace, "favorite");
                fragmentTransaction.add(R.id.user_detail_container,fragmentComment,"comment");
                fragmentTransaction.hide(fragmentPlace);
                fragmentTransaction.hide(fragmentComment);
                fragmentTransaction.commit();
                navigationView.getMenu().findItem(R.id.appointments).setChecked(true);
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
            startActivity(intentHome);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("newType", newType);
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
}
