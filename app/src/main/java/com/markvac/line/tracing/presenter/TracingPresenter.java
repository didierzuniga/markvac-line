package com.markvac.line.tracing.presenter;

/**
 * Created by unicorn on 8/22/2018.
 */

public interface TracingPresenter {
    void saveCoordinates(String coordinates, String username);
    void successfulStore();
}
