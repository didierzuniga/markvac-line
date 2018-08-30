package com.markvac.line.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by unicorn on 8/29/2018.
 */

public interface SplashInteractor {
    void verifyNetworkAndInternet(Activity activity, boolean isOnline, FirebaseUser firebaseUser, String uid);
}
