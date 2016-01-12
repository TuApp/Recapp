package com.unal.tuapp.recapp.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.ScreenSlidePagerAdapter;
import com.unal.tuapp.recapp.backend.model.categoryApi.model.Category;
import com.unal.tuapp.recapp.backend.model.commentApi.model.Comment;
import com.unal.tuapp.recapp.backend.model.eventApi.model.Event;
import com.unal.tuapp.recapp.backend.model.eventByUserApi.model.EventByUser;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.backend.model.placeImageApi.model.PlaceImage;
import com.unal.tuapp.recapp.backend.model.reminderApi.model.Reminder;
import com.unal.tuapp.recapp.backend.model.statisticsApi.model.Statistics;
import com.unal.tuapp.recapp.backend.model.subCategoryApi.model.SubCategory;
import com.unal.tuapp.recapp.backend.model.subCategoryByPlaceApi.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.subCategoryByTutorialApi.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.tutorialApi.model.Tutorial;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.UserByPlace;
import com.unal.tuapp.recapp.fragments.ImageFragment;
import com.unal.tuapp.recapp.fragments.RecappFragment;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CategoryEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CommentEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.EventByUserEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.EventEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.GcmRegistrationEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceImageEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.ReminderEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.StatisticsEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByPlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryByTutorialEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.SubCategoryEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.TutorialEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserByPlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

import java.util.Random;


public class Recapp extends AppCompatActivity {
    private View root;
    public static ViewPager carrousel;
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;
    public static final String AUTHORITY = "com.unal.tuapp.recapp.app";
    public static final String ACCOUNT_TYPE = "example.com";
    public static final String ACCOUNT = "dummyaccount";

    public static Account mAccount;

    public static ProgressDialog init;
    public static int initValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = getLayoutInflater().inflate(R.layout.activity_recapp, null);
        carrousel = (ViewPager) root.findViewById(R.id.view_pager_recapp);
        setContentView(root);
        setUpViewPager();
        addData();
        mAccount = createSyncAccount(this);

    }

    public static Account createSyncAccount(Context context){
        Account newAccount = new Account(ACCOUNT,ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if(accountManager.addAccountExplicitly(newAccount,null,null)){
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            Random random = new Random();
            ContentResolver.addPeriodicSync(newAccount,AUTHORITY,Bundle.EMPTY,26*60*60+60*random.nextInt(6));
            return newAccount;

        }
        return accountManager.getAccountsByType(ACCOUNT_TYPE)[0];
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpViewPager(){
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        ImageFragment main1 = ImageFragment.newInstance(R.drawable.main1,getString(R.string.first_general));
        ImageFragment main2 = ImageFragment.newInstance(R.drawable.main2,getString(R.string.first_points));
        ImageFragment main3 = ImageFragment.newInstance(R.drawable.main3,getString(R.string.first_events));
        screenSlidePagerAdapter.addFrament(main1);
        screenSlidePagerAdapter.addFrament(main2);
        screenSlidePagerAdapter.addFrament(main3);
        screenSlidePagerAdapter.addFrament(new RecappFragment());
        carrousel.setOffscreenPageLimit(4);
        carrousel.setAdapter(screenSlidePagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (carrousel.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            carrousel.setCurrentItem(carrousel.getCurrentItem() - 1);
        }
    }


    private void addData(){
        SharedPreferences pref = this.getPreferences(0);
        boolean data = pref.getBoolean("data", false);
        new GcmRegistrationEndPoint(this).execute();
        if(Utility.isNetworkAvailable(this)) {
            if (!data) {
            /*ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading the information");
            progressDialog.show();*/
                //We should call the async task
                init = new ProgressDialog(this);
                init.setMessage("We are downloading the initial information");
                init.setCancelable(false);
                init.show();


                Place place = new com.unal.tuapp.recapp.backend.model.placeApi.model.Place();
                Pair<Context, Pair<Place, String>> pair = new Pair<>(this.getApplicationContext(), new Pair<>(place, "getPlaces"));
                new PlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);

                User user = new User();
                Pair<Pair<Context, String>, Pair<User, String>> pairUser = new Pair<>(new Pair<>(getApplicationContext(), ""),
                        new Pair<>(user, "getUsers"));
                new UserEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUser);


                Category category = new Category();
                Pair<Context, Pair<Category, String>> pairCategory = new Pair<>(this.getApplicationContext(), new Pair<>(category, "getCategories"));
                new CategoryEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairCategory);

                SubCategory subCategory = new SubCategory();
                Pair<Context, Pair<SubCategory, String>> pairSubCategory = new Pair<>(this.getApplicationContext(), new Pair<>(subCategory, "getSubCategories"));
                new SubCategoryEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairSubCategory);

                Tutorial tutorial = new Tutorial();
                Pair<Context, Pair<Tutorial, String>> pairTutorial = new Pair<>(this.getApplicationContext(), new Pair<>(tutorial, "getTutorials"));
                new TutorialEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairTutorial);


                SubCategoryByPlace subCategoryByPlace = new SubCategoryByPlace();
                Pair<Context, Pair<SubCategoryByPlace, String>> pairSubCategoryByPlace = new Pair<>(this.getApplicationContext(), new Pair<>(subCategoryByPlace, "getSubCategoryByPlace"));
                new SubCategoryByPlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairSubCategoryByPlace);

                SubCategoryByTutorial subCategoryByTutorial = new SubCategoryByTutorial();
                Pair<Context, Pair<SubCategoryByTutorial, String>> pairSubCategoryByTutorial = new Pair<>(this.getApplicationContext(), new Pair<>(subCategoryByTutorial, "getSubCategoryByTutorial"));
                new SubCategoryByTutorialEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairSubCategoryByTutorial);

                Comment comment = new Comment();
                Pair<Pair<Context, Pair<Long, Long>>, Pair<Comment, String>> pairComment = new Pair<>(new Pair<>(getApplicationContext(), new Pair<>(-1L, -1L)),
                        new Pair<>(comment, "getComments"));
                new CommentEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairComment);

                Reminder reminder = new Reminder();
                Pair<Context, Pair<Reminder, String>> pairReminder = new Pair<>(getApplicationContext(), new Pair<>(reminder, "getAllReminders"));
                new ReminderEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairReminder);

                PlaceImage placeImage = new PlaceImage();
                Pair<Pair<Context, Long>, Pair<PlaceImage, String>> pairPlaceImage = new Pair<>(new Pair<>(getApplicationContext(), -1L), new Pair<>(placeImage, "getAllImagesPlace"));
                new PlaceImageEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairPlaceImage);

                UserByPlace userByPlace = new UserByPlace();
                Pair<Context, Pair<UserByPlace, String>> pairUserByPlace = new Pair<>(getApplicationContext(), new Pair<>(userByPlace, "getUserByPlace"));
                new UserByPlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUserByPlace);

                Event event = new Event();
                Pair<Context, Pair<Event, String>> pairEvent = new Pair<>(getApplicationContext(), new Pair<>(event, "getEvents"));
                new EventEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairEvent);

                EventByUser eventByUser = new EventByUser();
                Pair<Context, Pair<EventByUser, String>> pairEventByUser = new Pair<>(getApplicationContext(), new Pair<>(eventByUser,
                        "getEventByUser"));
                new EventByUserEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairEventByUser);

                Statistics statistics = new Statistics();
                Pair<Context,Pair<Statistics,String>> pairStatistics = new Pair<>(getApplicationContext(),
                        new Pair<>(statistics,"getStatistics"));
                new StatisticsEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairStatistics);

                //progressDialog.dismiss();


            }
            SharedPreferences.Editor edt = pref.edit();
            edt.putBoolean("data", true);
            edt.commit();
        }
    }




}
