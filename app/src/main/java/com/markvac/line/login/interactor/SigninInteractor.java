package com.markvac.line.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 8/28/2018.
 */

public interface SigninInteractor {
    void signin(String username, String password, Activity activity, FirebaseAuth firebaseAuth);
}
