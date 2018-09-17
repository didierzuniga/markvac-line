package com.markvac.line.tracing.interactor;

import android.app.Activity;

/**
 * Created by unicorn on 8/22/2018.
 */

public interface TracingInteractor {
//    void saveCoordinates(String coordinates, String dni, String company);
    void saveCoordinates(String company, String typeTracking, String dni, String coordinates,
                         String typeSubstance, String amountSubstance, Activity activity);
}
