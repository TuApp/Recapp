package com.unal.tuapp.recapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserEndPoint;

/**
 * Created by andresgutierrez on 11/23/15.
 */
public class MyPointsFragment extends Fragment {
    private  View root;
    private static ImageView userImage;
    private static TextView userName;
    private static TextView userPoints;
    private static ImageView refresh;
    private static User user;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_my_points,container,false);
        userImage = (ImageView) root.findViewById(R.id.user_image);
        userName = (TextView) root.findViewById(R.id.user_name);
        userPoints = (TextView) root.findViewById(R.id.user_points);
        refresh = (ImageView) root.findViewById(R.id.user_refresh);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            user = extras.getParcelable("user");
            if(user!=null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap image = BitmapFactory.decodeByteArray(user.getProfileImage(),0,user.getProfileImage().length
                        ,options);
                userImage.setImageBitmap(image);
                userName.setText(user.getName()+ " " + user.getLastName());
            }
        }
        if(Utility.isNetworkAvailable(getContext())){
            com.unal.tuapp.recapp.backend.model.userApi.model.User userBackend = new com.unal.tuapp.recapp.backend.model.userApi.model.User();
            userBackend.setId(user.getId());
            Pair<Pair<Context,String>,Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User,String>> pair
                    = new Pair<>(new Pair<>(getContext(),""),new Pair<>(userBackend,"getUserPoint"));
            new UserEndPoint(getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,pair);
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(getContext())) {
                    com.unal.tuapp.recapp.backend.model.userApi.model.User userBackend = new com.unal.tuapp.recapp.backend.model.userApi.model.User();
                    userBackend.setId(user.getId());
                    Pair<Pair<Context, String>, Pair<com.unal.tuapp.recapp.backend.model.userApi.model.User, String>> pair
                            = new Pair<>(new Pair<>(getContext(), ""), new Pair<>(userBackend, "getUserPoint"));
                    new UserEndPoint(getActivity()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);
                }else{
                    new AlertDialog.Builder(getContext())
                            .setCancelable(false)
                            .setTitle(R.string.internet)
                            .setMessage(R.string.need_internet)
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
        userPoints.setText(getResources().getString(R.string.points) + ": "+ user);
    }
}
