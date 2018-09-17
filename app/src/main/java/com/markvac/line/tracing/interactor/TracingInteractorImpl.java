package com.markvac.line.tracing.interactor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.markvac.line.LineApplication;
import com.markvac.line.direction_modules.DirectionFinder;
import com.markvac.line.direction_modules.DirectionFinderListener;
import com.markvac.line.direction_modules.Route;
import com.markvac.line.tracing.presenter.TracingPresenter;
import com.markvac.line.tracing.repository.TracingRepository;
import com.markvac.line.tracing.repository.TracingRepositoryImpl;
import com.markvac.line.tracing.view.Tracing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by unicorn on 8/22/2018.
 */

public class TracingInteractorImpl implements TracingInteractor, DirectionFinderListener {

    private JSONObject coordinatesJson;
    private String coords, userId, companyName;
    private int counter, distance = 0, duration = 0;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private LineApplication app;
    private TracingPresenter presenter;
    private TracingRepository repository;

    public TracingInteractorImpl(TracingPresenter presenter) {
        this.presenter = presenter;
        repository = new TracingRepositoryImpl(presenter, this);
    }

    @Override
    public void saveCoordinates(String company, String typeTracking, String dni, String coordinates,
                                String typeSubstance, String amountSubstance, Activity activity) {

        app = (LineApplication) activity.getApplicationContext();
        counter = 0;
//        coords = coordinates;
        userId = dni;
        companyName = company;
//        typeTrack = typeTracking;
//        typeSubstan = typeSubstance;
//        amountSubstan = amountSubstance;

        // Almacenar datos en Preferences

        //Company
        //Type tracking
        //Dni
        //Distance
        //Duration
        //Coordinates
        //Type substance (if exist)
        //Amount substance (if exist)

        JSONObject dataJson = new JSONObject();
        JSONArray registerJson = new JSONArray();

        try {
            if (app.shaPref.getString("registerReadyToDB", null) != null) {
                JSONArray jsonArrayFromShared = new JSONArray(app.shaPref.getString("registerReadyToDB", null));
                dataJson.put("typeTrack", typeTracking);
                dataJson.put("coordinates", coordinates);
                dataJson.put("typeSubstance", typeSubstance);
                dataJson.put("amountSubstance", amountSubstance);

                jsonArrayFromShared.put(dataJson);

                String registerFromJsonArrayFromShared = jsonArrayFromShared.toString();
                app.editor.putString("registerReadyToDB", registerFromJsonArrayFromShared);
                app.editor.commit();


            } else {
                dataJson.put("typeTrack", typeTracking);
                dataJson.put("coordinates", coordinates);
                dataJson.put("typeSubstance", typeSubstance);
                dataJson.put("amountSubstance", amountSubstance);

                registerJson.put(dataJson);

                String registerFromJsonArray = registerJson.toString();
                app.editor.putString("registerReadyToDB", registerFromJsonArray);
                app.editor.commit();
            }

            presenter.successfulStore();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        calculateDistanceAndTime(coordinates);
    }

//    @Override
//    public void saveCoordinates(String coordinates, String dni, String company) {
//        counter = 0;
//        coords = coordinates;
//        userId = dni;
//        companyName = company;
//        calculateDistanceAndTime(coordinates);
//    }

    public void calculateDistanceAndTime(String coordinates) {
        try {
            JSONObject jsonFromShared = new JSONObject(coordinates);
            coordinatesJson = jsonFromShared;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < (coordinatesJson.length() - 1); i++) {
            try {
                String latLonToSplit_1 = coordinatesJson.getString(String.valueOf(i));
                String latLonToSplit_2 = coordinatesJson.getString(String.valueOf(i + 1));
                try {
                    new DirectionFinder(this, latLonToSplit_1, latLonToSplit_2).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            counter += 1;
            distance += route.distance.value;
            duration += route.duration.value;
        }

        if (counter == (coordinatesJson.length() - 1)) {
//            repository.saveCoordinates(coords, duration, distance, userId, companyName);

        }
    }
}
