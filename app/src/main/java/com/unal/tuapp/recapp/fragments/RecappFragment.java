package com.unal.tuapp.recapp.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.activities.NavigationDrawer;
import com.unal.tuapp.recapp.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecappFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private GooglePlus mGooglePlus;
    private SignInButton mSignInButton;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recapp,container,false);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mGooglePlus=GooglePlus.getInstance(getActivity(), this, this);
        mSignInProgress = mGooglePlus.STATE_DEFAULT;
        mSignInButton = (SignInButton) root.findViewById(R.id.googlePlus);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignInProgress = mGooglePlus.STATE_SIGN_IN;
                if (!(mGooglePlus.mGoogleApiClient.isConnecting())) {
                    mGooglePlus.mGoogleApiClient.connect();
                }
                //resolveSignInError();
            }
        });
        return root;
    }

    @Override
    public void onStart(){
        super.onStart();
        /*I need to set the instance of singleton in null and after that I need to create it again
        *I don't know but if I don't do that the program doesn't work correctly
        */
        GooglePlus.instance = null;
        mGooglePlus = GooglePlus.getInstance(getActivity(),this,this);
        if(!(mGooglePlus.mGoogleApiClient.isConnected())) {
            mGooglePlus.mGoogleApiClient.connect();
        }


    }


    private void resolveSignInError(){
        if(mSignInIntent!=null){
            try{
                mSignInProgress = mGooglePlus.STATE_IN_PROGRESS;
                getActivity().startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        mGooglePlus.RC_SIGN_IN,null,0,0,0);
            } catch (IntentSender.SendIntentException e) {
                mSignInProgress = mGooglePlus.STATE_SIGN_IN;
                mGooglePlus.mGoogleApiClient.connect();

            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInProgress = mGooglePlus.STATE_DEFAULT;
        //Toast.makeText(getActivity(),"User connected",Toast.LENGTH_SHORT).show();
        String email = Plus.AccountApi.getAccountName(mGooglePlus.mGoogleApiClient);
        Intent intent = new Intent(getActivity(),NavigationDrawer.class);
        intent.putExtra("email",email);
        startActivity(intent);

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGooglePlus.mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(mSignInProgress != mGooglePlus.STATE_IN_PROGRESS){
            mSignInIntent = connectionResult.getResolution();
            if(mSignInProgress == mGooglePlus.STATE_SIGN_IN){
                resolveSignInError();
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode,int responseCode,Intent intent){
        if(mGooglePlus.RC_SIGN_IN==requestCode){
            if(responseCode== getActivity().RESULT_OK){
                mSignInProgress = mGooglePlus.STATE_SIGN_IN;
            }else{
                mSignInProgress = mGooglePlus.STATE_DEFAULT;
            }
            if(!(mGooglePlus.mGoogleApiClient.isConnecting())){
                mGooglePlus.mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}
