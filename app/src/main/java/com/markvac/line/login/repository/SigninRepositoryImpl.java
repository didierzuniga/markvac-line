package com.markvac.line.login.repository;

import com.markvac.line.login.interactor.SigninInteractor;
import com.markvac.line.login.presenter.SigninPresenter;

/**
 * Created by unicorn on 8/28/2018.
 */

public class SigninRepositoryImpl implements SigninRepository {

    private SigninPresenter presenter;
    private SigninInteractor interactor;

    public SigninRepositoryImpl(SigninPresenter presenter, SigninInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }
}
