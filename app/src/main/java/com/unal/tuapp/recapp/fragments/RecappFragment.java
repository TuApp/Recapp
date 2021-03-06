package com.unal.tuapp.recapp.fragments;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.unal.tuapp.recapp.activities.Company;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.others.GooglePlus;
import com.unal.tuapp.recapp.activities.NavigationDrawer;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.others.Utility;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecappFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private GooglePlus mGooglePlus;
    private SignInButton mSignInButton;
    private Button mLoginCompany;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recapp,container,false);
        /*Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
        GooglePlus.instance = null;
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
        mLoginCompany = (Button) root.findViewById(R.id.companyLogin);
        mLoginCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.login_dialog);
                //dialog.setTitle(getActivity().getString(R.string.login));
                final TextInputLayout email = (TextInputLayout) dialog.findViewById(R.id.company_email);
                final TextInputLayout password = (TextInputLayout) dialog.findViewById(R.id.company_password);
                email.setErrorEnabled(true);
                password.setErrorEnabled(true);
                Button login = (Button) dialog.findViewById(R.id.login);
                email.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b && !isValidEmail(email.getEditText().getText().toString())) {
                            email.setError("The email is not valid");
                        }else{
                            email.setError(null);
                        }
                    }
                });
                password.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b && password.getEditText().getText().toString().equals("")) {
                            password.setError("The password can't be empty ");
                        }else{
                            password.setError(null);
                        }
                    }
                });
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String emailText = email.getEditText().getText().toString();
                        String passwordText = password.getEditText().getText().toString();
                        if (!passwordText.equals("")) {
                            password.setError(null);
                            if (isValidEmail(emailText)) {
                                email.setError(null);
                                Cursor data = getActivity().getContentResolver().query(
                                        RecappContract.PlaceEntry.buildPlaceEmailUri(emailText.trim()),
                                        new String[]{RecappContract.PlaceEntry._ID, RecappContract.PlaceEntry.COLUMN_PASSWORD},
                                        null,
                                        null,
                                        null
                                );
                                if (data.moveToFirst()) {
                                    if (passwordText.equals(data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_PASSWORD)))) {
                                        password.setError(null);
                                        dialog.dismiss();
                                        Intent intent = new Intent(getActivity(), Company.class);
                                        intent.putExtra("email", emailText);
                                        intent.putExtra("id",data.getLong(data.getColumnIndexOrThrow(RecappContract.PlaceEntry._ID)));
                                        startActivity(intent);
                                    } else {
                                        password.setError("The passwords don't match");
                                    }
                                } else {
                                    email.setError("The email is not associated to a company");
                                }
                            }
                        } else {
                            password.setError("The password can't be empty");
                        }
                    }
                });

                dialog.show();


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
        if(mGooglePlus.mGoogleApiClient.isConnected()) {
            if(Utility.isNetworkAvailable(getContext())) {
                Account account = Plus.AccountApi;

                Intent intent = new Intent(getActivity(), NavigationDrawer.class);
                intent.putExtra("email",account.getAccountName(mGooglePlus.mGoogleApiClient));
                startActivity(intent);
            }else{
                mGooglePlus.mGoogleApiClient.disconnect();
            }
        }

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
    public boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



}
