package com.markvac.line;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by unicorn on 8/28/2018.
 */

public class LineApplication extends Application {

    public FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    public String username, uid, email;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            firebaseUser = firebaseAuth.getCurrentUser();
            uid = firebaseUser.getUid();
            email = firebaseUser.getEmail();
        } else {
            firebaseUser = null;
        }
    }
}
