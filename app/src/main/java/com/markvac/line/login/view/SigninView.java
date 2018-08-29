package com.markvac.line.login.view;

/**
 * Created by unicorn on 8/28/2018.
 */

public interface SigninView {
    void showProgressBar();
    void hideProgressBar();
    void enableInputs();
    void disableInputs();
    void signinError(String error);
    void signinSuccess(String username, String email, String uid);
    void dniNotExist();
}
