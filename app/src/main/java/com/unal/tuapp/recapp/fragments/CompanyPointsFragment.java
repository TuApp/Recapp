package com.unal.tuapp.recapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Company;
import com.unal.tuapp.recapp.backend.model.placeApi.model.Place;
import com.unal.tuapp.recapp.backend.model.userApi.model.User;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

/**
 * Created by andresgutierrez on 11/24/15.
 */
public class CompanyPointsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static View root;
    private static LinearLayout searchUser;
    private static LinearLayout companyPoints;
    private static NestedScrollView userData;
    private static TextInputLayout email;
    private static Button search;
    private static ImageView userImage;
    private static TextView userName;
    private static TextView userPoints;
    private static TextView maxPoint;
    private static SeekBar points;
    private static Button addPoints;
    private static String emailUser;
    private static final int USER =  0324;
    private static com.unal.tuapp.recapp.data.User user;
    private static long placeId;
    private static long pointsAdded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_points_company,container,false);
        searchUser = (LinearLayout) root.findViewById(R.id.search_user);
        companyPoints = (LinearLayout) root.findViewById(R.id.company_points);
        userData = (NestedScrollView) root.findViewById(R.id.user_data);
        email  = (TextInputLayout) root.findViewById(R.id.user_email);
        search = (Button) root.findViewById(R.id.user_search_points);
        userImage = (ImageView) root.findViewById(R.id.user_image);
        userName = (TextView) root.findViewById(R.id.user_name);
        userPoints = (TextView) root.findViewById(R.id.user_points);
        maxPoint = (TextView) root.findViewById(R.id.max_points);
        points = (SeekBar) root.findViewById(R.id.slider_points);
        addPoints = (Button) root.findViewById(R.id.points);

        if(getActivity().getIntent().getExtras()!=null){
            placeId = getActivity().getIntent().getExtras().getLong("id");
        }

        addPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(getContext()) && user!=null){
                    email.getEditText().setText("");
                    searchUser.setVisibility(View.VISIBLE);
                    companyPoints.setVisibility(View.GONE);
                    userData.setVisibility(View.GONE);
                    Place place = new Place();
                    place.setId(placeId);
                    place.setPoints(pointsAdded);
                    Pair<Context,Pair<Place,String>> pair = new Pair<>(getContext(),new Pair<>(
                            place,"removePoints"
                    ));
                    new PlaceEndPoint((Company)getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);
                    com.unal.tuapp.recapp.backend.model.userApi.model.User userBackend = new com.unal.tuapp.recapp.backend.model.userApi.model.User();
                    userBackend.setId(user.getId());
                    userBackend.setPoints(pointsAdded);
                    Pair<Pair<Context,String>,Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User,String>> pairUser
                            = new Pair<>(new Pair<>(getContext(),""),new Pair<>(userBackend,"addPointsUser"));
                    new UserEndPoint(getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUser);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.internet)
                            .setMessage(R.string.need_internet)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        points.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pointsAdded = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(),""+pointsAdded,Toast.LENGTH_SHORT).show();

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(getContext())) {
                    emailUser = email.getEditText().getText().toString();
                    if (getLoaderManager().getLoader(USER) == null) {
                        getLoaderManager().initLoader(USER, null, CompanyPointsFragment.this);
                    } else {
                        getLoaderManager().restartLoader(USER, null, CompanyPointsFragment.this);
                    }
                }else{
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.internet)
                            .setMessage(R.string.need_internet)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }

            }
        });


        return root;
    }

    public void showPoint(Long user){
        userPoints.setText(getResources().getString(R.string.points) + " " +user);
    }

    public void showCompanyPoint(Place place){
        companyPoints.setVisibility(View.VISIBLE);
        maxPoint.setText(place.getPoints() + "");
        points.setMax((Integer.parseInt("" + place.getPoints())));
        points.setVerticalScrollbarPosition(points.getMax() / 2);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == USER) {
            return new CursorLoader(
                    getContext(),
                    RecappContract.UserEntry.buildUserEmail(emailUser),
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() ==  USER){
            if(data.moveToFirst()){//We find an user
                searchUser.setVisibility(View.GONE);
                userData.setVisibility(View.VISIBLE);
                user = new com.unal.tuapp.recapp.data.User();
                user.setName(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_NAME)));
                user.setLastName(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_LASTNAME)));
                user.setId(data.getLong(data.getColumnIndexOrThrow(RecappContract.UserEntry._ID)));
                user.setProfileImage(data.getBlob(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_USER_IMAGE)));
                user.setEmail(data.getString(data.getColumnIndexOrThrow(RecappContract.UserEntry.COLUMN_EMAIL)));
                initData();
                if(Utility.isNetworkAvailable(getContext())){
                    Place place = new Place();
                    place.setId(placeId);
                    Pair<Context,Pair<Place,String>> pair = new Pair<>(getContext(),new Pair<>(
                            place,"getPlace"
                    ));
                    new PlaceEndPoint((Company)getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pair);
                    com.unal.tuapp.recapp.backend.model.userApi.model.User userBackend = new com.unal.tuapp.recapp.backend.model.userApi.model.User();
                    userBackend.setId(user.getId());
                    Pair<Pair<Context,String>,Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User,String>> pairUser
                            = new Pair<>(new Pair<>(getContext(),""),new Pair<>(userBackend,"getUserPoint"));
                    new UserEndPoint(getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pairUser);
                }
            }else{
                new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setTitle(R.string.email)
                        .setMessage(R.string.wrong_email)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public void initData(){
        userName.setText(user.getName() + " " + user.getLastName());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap image = BitmapFactory.decodeByteArray(user.getProfileImage(),0,user.getProfileImage().length,options);
        userImage.setImageBitmap(image);
    }
}

