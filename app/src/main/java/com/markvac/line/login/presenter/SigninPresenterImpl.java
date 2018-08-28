package com.markvac.line.login.presenter;

import com.markvac.line.login.interactor.SigninInteractor;
import com.markvac.line.login.interactor.SigninInteractorImpl;
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
}
