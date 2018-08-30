package com.markvac.line.login.presenter;

import com.google.firebase.auth.FirebaseUser;
import com.markvac.line.login.interactor.SplashInteractor;
import com.markvac.line.login.interactor.SplashInteractorImpl;
import com.markvac.line.login.view.Splash;
import com.markvac.line.login.view.SplashView;

/**
 * Created by unicorn on 8/29/2018.
 */

public class SplashPresenterImpl implements SplashPresenter {

    private SplashView view;
    private SplashInteractor interactor;

    public SplashPresenterImpl(SplashView view) {
        this.view = view;
        interactor = new SplashInteractorImpl(this);
    }

    @Override
    public void verifyNetworkAndInternet(Splash splash, boolean isOnline, FirebaseUser firebaseUser, String uid) {
        interactor.verifyNetworkAndInternet(splash, isOnline, firebaseUser, uid);
    }

    @Override
    public void alertNoGps() {
        view.alertNoGps();
    }

    @Override
    public void goSignin() {
        view.goSignin();
    }

    @Override
    public void goTracing() {
        view.goTracing();
    }
}
