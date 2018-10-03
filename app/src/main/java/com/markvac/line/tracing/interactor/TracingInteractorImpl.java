package com.markvac.line.tracing.interactor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by unicorn on 8/22/2018.
 */

public class TracingInteractorImpl implements TracingInteractor, DirectionFinderListener {

    private JSONObject jsonCurrentCoordinates, jsonObjectForKey;
    private JSONArray jsonArrayFromShared, jsonArrayToUpload;
    private String userId, companyName;
    private int counterMainArray, counterChildCoords, counterInTimer, distance, duration, counterI = 0, counterJ, j;
    private Thread thre1, thre2;
    private LineApplication app;
    private TracingPresenter presenter;
    private TracingRepository repository;

    public TracingInteractorImpl(TracingPresenter presenter) {
        this.presenter = presenter;
        repository = new TracingRepositoryImpl(presenter, this);
    }

    @Override
    public void saveCoordinates(String company, String typeTracking, String dni, String dateInit, String timeInit,
                                String dateFinal, String timeFinal, String coordinates,
                                String typeSubstance, String amountSubstance, Activity activity){

        app = (LineApplication) activity.getApplicationContext();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date dateTimeInitial = null;
        Date dateTimeFinal = null;
        try {
            dateTimeInitial = dateFormat.parse(dateInit + " " + timeInit);
            dateTimeFinal = dateFormat.parse(dateFinal + " " + timeFinal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = dateTimeFinal.getTime() - dateTimeInitial.getTime();
        long minutesInMilli = 1000 * 60;
        long elapsedMinutes = difference / minutesInMilli;
        int elapsedMinutesInteger = (int) elapsedMinutes;
        JSONObject dataJson = new JSONObject();
        JSONArray registerJson = new JSONArray();


        try {
            if (app.shaPref.getString("registerReadyToDB", null) != null) {
                JSONArray jsonArrayFromShared = new JSONArray(app.shaPref.getString("registerReadyToDB", null));
                dataJson.put("typeTrack", typeTracking);
                dataJson.put("date", dateInit);
                dataJson.put("time", timeInit);
                dataJson.put("coordinates", coordinates);
                dataJson.put("duration", elapsedMinutesInteger);
                dataJson.put("typeSubstance", typeSubstance);
                dataJson.put("amountSubstance", amountSubstance);

                jsonArrayFromShared.put(dataJson);

                String registerFromJsonArrayFromShared = jsonArrayFromShared.toString();
                app.editor.putString("registerReadyToDB", registerFromJsonArrayFromShared);
                app.editor.commit();


            } else {
                dataJson.put("typeTrack", typeTracking);
                dataJson.put("date", dateInit);
                dataJson.put("time", timeInit);
                dataJson.put("coordinates", coordinates);
                dataJson.put("duration", elapsedMinutesInteger);
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
    }

    @Override
    public void uploadData(String company, String dni, Activity activity) {
        app = (LineApplication) activity.getApplicationContext();
        userId = dni;
        companyName = company;
        counterMainArray = 0;
        jsonArrayToUpload = new JSONArray();
        try {
            jsonArrayFromShared = new JSONArray(app.shaPref.getString("registerReadyToDB", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeForOne();
    }

    public void executeForOne(){
        for (int i = counterI; i < (jsonArrayFromShared.length()); i++) {
            counterI += 1;
            counterChildCoords = 0;
            counterInTimer = 0;
            distance = 0;
            duration = 0;
            try {
                String data = (jsonArrayFromShared.get(i)).toString(); // {"typeTrack":"tr_irriga","coordinates":"{\","typeSubstance":"A1","amountSubstance":"15"}
                jsonObjectForKey = new JSONObject(data); // Lo mismo del anterior pero en Object
                String coordJsonInString = jsonObjectForKey.getString("coordinates");
                jsonCurrentCoordinates = new JSONObject(coordJsonInString); // Coordenadas de cada Key
            } catch (JSONException e) {
                e.printStackTrace();
            }
            counterJ = 0;
            executeForTwo();
            break;
        }
    }

    public void executeForTwo(){
        for (j = counterJ; j < jsonCurrentCoordinates.length() - 1; j++) {
            counterJ += 1;
            String latLonToSplit_1 = null;
            String latLonToSplit_2 = null;
            try {
                latLonToSplit_1 = jsonCurrentCoordinates.getString(String.valueOf(j));
                latLonToSplit_2 = jsonCurrentCoordinates.getString(String.valueOf(j + 1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                new DirectionFinder(TracingInteractorImpl.this, latLonToSplit_1, latLonToSplit_2).execute();
                break;
            } catch (UnsupportedEncodingException e) {
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
            counterChildCoords += 1;
            distance += route.distance.value;
            duration += route.duration.value;
        }

        if (counterChildCoords == (jsonCurrentCoordinates.length() - 1)){
            counterMainArray += 1;
            try {
                jsonObjectForKey.put("distance", distance);
                jsonArrayToUpload.put(jsonObjectForKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (counterMainArray == jsonArrayFromShared.length()) {
                repository.uploadData(jsonArrayToUpload, companyName, userId);
            } else {
                executeForOne();
            }
        } else {
            executeForTwo();
        }
    }
}
