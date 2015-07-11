package com.unal.tuapp.recapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by andresgutierrez on 7/10/15.
 */
public class SignOut extends AppCompatActivity {
    private GooglePlus mGooglePlus;
    private TextView name;
    private TextView email;
    private Button signout;
    private Button disconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e("algo",""+GooglePlus.instance);
        mGooglePlus = GooglePlus.getInstance(this,null,null);

        setContentView(R.layout.signout);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        signout = (Button) findViewById(R.id.signOut);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGooglePlus.mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                    mGooglePlus.mGoogleApiClient.disconnect();
                    Intent intent = new Intent(SignOut.this, Recapp.class);
                    startActivity(intent);
                }
            }
        });
        disconnect = (Button) findViewById(R.id.disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plus.AccountApi.clearDefaultAccount(mGooglePlus.mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGooglePlus.mGoogleApiClient);
                Intent intent = new Intent(SignOut.this, Recapp.class);
                startActivity(intent);

            }
        });
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGooglePlus.mGoogleApiClient);
        Account currentAccount =  Plus.AccountApi;
        name.setText(currentPerson.getDisplayName());
        email.setText(currentAccount.getAccountName(mGooglePlus.mGoogleApiClient));


    }

}
