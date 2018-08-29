package com.markvac.line.login.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.markvac.line.login.view.Signin;

/**
 * Created by unicorn on 8/28/2018.
 */

public interface SigninPresenter {
    void signin(String username, String password, Signin signin, FirebaseAuth firebaseAuth);
    void signinError(String error);
    void signinSuccess(String username, String email, String uid);
    void dniNotExist();
}
