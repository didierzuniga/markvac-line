package com.markvac.line.login.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.markvac.line.login.interactor.SigninInteractor;
import com.markvac.line.login.interactor.SigninInteractorImpl;
import com.markvac.line.login.view.Signin;
import com.markvac.line.login.view.SigninView;

/**
 * Created by unicorn on 8/28/2018.
 */

public class SigninPresenterImpl implements SigninPresenter {

    private SigninView view;
    private SigninInteractor interactor;

    public SigninPresenterImpl(SigninView view) {
        this.view = view;
        interactor = new SigninInteractorImpl(this);
    }

    @Override
    public void signin(String username, String password, Signin signin, FirebaseAuth firebaseAuth) {
        interactor.signin(username, password, signin, firebaseAuth);
    }

    @Override
    public void signinError(String error) {
        view.signinError(error);
    }

    @Override
    public void signinSuccess(String username, String email, String uid, String company, String position) {
        view.signinSuccess(username, email, uid, company, position);
    }

    @Override
    public void dniNotExist() {
        view.dniNotExist();
    }
}
