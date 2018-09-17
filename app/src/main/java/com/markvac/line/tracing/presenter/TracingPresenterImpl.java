package com.markvac.line.tracing.presenter;

import android.util.Log;

import com.markvac.line.tracing.interactor.TracingInteractor;
import com.markvac.line.tracing.interactor.TracingInteractorImpl;
import com.markvac.line.tracing.view.Tracing;
import com.markvac.line.tracing.view.TracingView;

/**
 * Created by unicorn on 8/22/2018.
 */

public class TracingPresenterImpl implements TracingPresenter {

    private TracingView view;
    private TracingInteractor interactor;
    public TracingPresenterImpl(TracingView view) {
        this.view = view;
        interactor = new TracingInteractorImpl(this);
    }


    @Override
    public void saveCoordinates(String company, String typeTracking, String dni, String coordinates,
                                String typeSubstance, String amountSubstance, Tracing tracing) {
        interactor.saveCoordinates(company, typeTracking, dni, coordinates, typeSubstance, amountSubstance, tracing);
    }

    @Override
    public void successfulStore() {
        view.successfulStore();
    }
}
