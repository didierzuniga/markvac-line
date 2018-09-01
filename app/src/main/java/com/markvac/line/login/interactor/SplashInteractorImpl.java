package com.markvac.line.login.interactor;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.markvac.line.login.presenter.SplashPresenter;
import com.markvac.line.login.repository.SplashRepository;
import com.markvac.line.login.repository.SplashRepositoryImpl;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by unicorn on 8/29/2018.
 */

public class SplashInteractorImpl implements SplashInteractor {

    private LocationManager locationManager;
    private SplashPresenter presenter;
    private SplashRepository repository;

    public SplashInteractorImpl(SplashPresenter presenter) {
        this.presenter = presenter;
//        repository = new SplashRepositoryImpl(presenter);
    }

    @Override
    public void verifyNetworkAndInternet(Activity activity, boolean isOnline, FirebaseUser firebaseUser, String email) {

        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!network_enabled) {
            presenter.alertNoGps();
        } else {
            if (firebaseUser != null) {
                presenter.goTracing();
            } else {
                presenter.goSignin();
            }
        }
    }
}
