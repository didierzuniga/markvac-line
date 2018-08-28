package com.markvac.line.login.interactor;

import com.markvac.line.login.presenter.SigninPresenter;
import com.markvac.line.login.repository.SigninRepository;
import com.markvac.line.login.repository.SigninRepositoryImpl;

/**
 * Created by unicorn on 8/28/2018.
 */

public class SigninInteractorImpl implements SigninInteractor {

    private SigninPresenter presenter;
    private SigninRepository repository;

    public SigninInteractorImpl(SigninPresenter presenter) {
        this.presenter = presenter;
        repository = new SigninRepositoryImpl(presenter, this);
    }
}
