package com.markvac.line.tracing.presenter;

import com.markvac.line.tracing.view.Tracing;

/**
 * Created by unicorn on 8/22/2018.
 */

public interface TracingPresenter {
//    void saveCoordinates(String coordinates, String dni, String company);
    void saveCoordinates(String company, String typeTracking, String dni, String date, String time, String coordinates,
                         String typeSubstance, String amountSubstance, Tracing tracing);
    void successfulStore();
    void successfulUpload();
    void uploadData(String company, String dni, Tracing tracing);
}
