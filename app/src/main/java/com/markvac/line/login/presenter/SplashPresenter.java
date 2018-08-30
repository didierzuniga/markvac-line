package com.markvac.line.login.presenter;

import com.google.firebase.auth.FirebaseUser;
import com.markvac.line.login.view.Splash;

/**
 * Created by unicorn on 8/29/2018.
 */

public interface SplashPresenter {
    void verifyNetworkAndInternet(Splash splash, boolean isOnline, FirebaseUser firebaseUser, String uid);
    void alertNoGps();
    void goSignin();
    void goTracing();
}
