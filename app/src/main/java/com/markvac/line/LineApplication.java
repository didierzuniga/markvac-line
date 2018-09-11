package com.markvac.line;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by unicorn on 8/28/2018.
 */

public class LineApplication extends Application {

    public FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ConnectivityManager connectivityManager;
    private boolean connected = false;
    public String dni, uid, email, company, position;
    public SharedPreferences shaPref;
    public SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        shaPref = getSharedPreferences("sharedMarkvacLine", MODE_PRIVATE);
        editor = shaPref.edit();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            firebaseUser = firebaseAuth.getCurrentUser();
            uid = firebaseUser.getUid();
            email = firebaseUser.getEmail();
            dni = shaPref.getString("dni", null);
            company = shaPref.getString("company", null);

        } else {
            firebaseUser = null;
        }
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
        }
        return connected;
    }
}
