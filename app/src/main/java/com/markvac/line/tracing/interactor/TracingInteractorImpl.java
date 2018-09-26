package com.markvac.line.tracing.interactor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
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

//    private JSONObject coordinatesJson;
    private JSONObject jsonCurrentCoordinates, jsonObjectForKey;
    private JSONArray jsonArrayFromShared, jsonArrayToUpload;
    private String userId, companyName;
    private int counterMainArray, counterChildCoords, distance, duration;
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

    @Override
    public void uploadData() {
        counterMainArray = 0;
        jsonArrayToUpload = new JSONArray();
        try {
            jsonArrayFromShared = new JSONArray(app.shaPref.getString("registerReadyToDB", null));

            for (int i = 0; i < (jsonArrayFromShared.length()); i++) {
                counterChildCoords = 0;
                distance = 0;
                duration = 0;
                String data = (jsonArrayFromShared.get(i)).toString(); // {"typeTrack":"tr_irriga","coordinates":"{\","typeSubstance":"A1","amountSubstance":"15"}
                jsonObjectForKey = new JSONObject(data); // Lo mismo del anterior pero en Object
                String coordJsonInString = jsonObjectForKey.getString("coordinates");
                JSONObject jsonCurrentCoordinates = new JSONObject(coordJsonInString); // Coordenadas de cada Key


                for (int j = 0; i < jsonCurrentCoordinates.length() - 1; i++){
                    String latLonToSplit_1 = jsonCurrentCoordinates.getString(String.valueOf(j));
                    String latLonToSplit_2 = jsonCurrentCoordinates.getString(String.valueOf(j + 1));
                    try {
                        new DirectionFinder(this, latLonToSplit_1, latLonToSplit_2).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
//        try {
//            JSONObject jsonFromShared = new JSONObject(coordinates);
//            coordinatesJson = jsonFromShared;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 0; i < (coordinatesJson.length() - 1); i++) {
//            try {
//                String latLonToSplit_1 = coordinatesJson.getString(String.valueOf(i));
//                String latLonToSplit_2 = coordinatesJson.getString(String.valueOf(i + 1));
//                try {
//                    new DirectionFinder(this, latLonToSplit_1, latLonToSplit_2).execute();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            counterChildCoords += 1;
            distance += route.distance.value;
            duration += route.duration.value;
        }

        if (counterChildCoords == (jsonCurrentCoordinates.length() - 1)){
            // Aqui terminan las coordenadas de cada Key del Array principal

            counterMainArray += 1;

            try {
                jsonObjectForKey.put("distance", distance);
                jsonArrayToUpload.put(jsonObjectForKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (counterMainArray == jsonArrayFromShared.length()) {
                // guardar datos
                // repository.saveCoordinates(coords, duration, distance, userId, companyName);
            }
        }


    }
}
