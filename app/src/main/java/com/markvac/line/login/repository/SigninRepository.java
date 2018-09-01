package com.markvac.line.login.repository;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unicorn on 8/28/2018.
 */

public interface SigninRepository {
    void signin(String dni, String password, Activity activity, FirebaseAuth firebaseAuth);
}
