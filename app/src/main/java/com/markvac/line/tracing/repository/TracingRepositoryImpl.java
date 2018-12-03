package com.markvac.line.tracing.repository;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markvac.line.LineApplication;
import com.markvac.line.apis.RetrofitDatetimeAdapter;
import com.markvac.line.apis.RetrofitDatetimeService;
import com.markvac.line.models.Time;
import com.markvac.line.models.TrackingSupervision;
import com.markvac.line.tracing.interactor.TracingInteractor;
import com.markvac.line.tracing.presenter.TracingPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by unicorn on 8/12/2018.
 */

public class TracingRepositoryImpl implements TracingRepository {

    private TracingPresenter presenter;
    private TracingInteractor interactor;
    private JSONArray uploadToDB;
    private String dni, companyName;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceCompanies = database.getReference("companies");

    public TracingRepositoryImpl(TracingPresenter presenter, TracingInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void uploadData(JSONArray upload, String company, String dnii) {
        dni = dnii;
        companyName = company;
        uploadToDB = upload;
        try {
            for (int i = 0; i < uploadToDB.length(); i++){
                String data = (uploadToDB.get(i)).toString(); // {"typeTrack":"tr_irriga","coordinates":"{\","typeSubstance":"A1","amountSubstance":"15"}
                JSONObject jsonObjectForKey = new JSONObject(data); // Lo mismo del anterior pero en Object
                String typeTrack = jsonObjectForKey.getString("typeTrack");
                String date = jsonObjectForKey.getString("date");
                String time = jsonObjectForKey.getString("time");
                String coords = jsonObjectForKey.getString("coordinates");
                int distance = jsonObjectForKey.getInt("distance");
                int duration = jsonObjectForKey.getInt("duration"); // Minutes
                String typeSubstanc = jsonObjectForKey.getString("typeSubstance");
                String amountSubstanc = jsonObjectForKey.getString("amountSubstance");
                String path = companyName + "/" + typeTrack + "/" + dni + "/" + date + "/" + time;
                TrackingSupervision traced = new TrackingSupervision(distance, duration, coords, typeSubstanc, amountSubstanc);
                referenceCompanies.child(path).setValue(traced);
            }
            presenter.successfulUpload();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //DATETIME ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//        Retrofit retrofit = new RetrofitDatetimeAdapter().getAdapter();
//        RetrofitDatetimeService service = retrofit.create(RetrofitDatetimeService.class);
//        Call<Time> call;
//        // Hardcoded Country "CO"
//        call = service.loadTime("CO");
//        call.enqueue(new Callback<Time>() {
//            @Override
//            public void onResponse(Call<Time> call, Response<Time> response) {
//                dateNow = response.body().getDate();
//                timeNow = response.body().getTime();
//            }
//
//            @Override
//            public void onFailure(Call<Time> call, Throwable t) {
//                //Error handler
//            }
//        });
        //DATETIME ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

//        timer = new Timer();
//        timer.schedule(new sendData(), 1000, 1000);
    }

//    public class sendData extends TimerTask {
//        @Override
//        public void run() {
//            if (timeNow != null || dateNow != null){
//
//                try {
//                    for (int i = 0; i < uploadToDB.length(); i++){
//                        String typeSubstanc = null;
//                        String amountSubstanc = null;
//                        String data = (uploadToDB.get(i)).toString(); // {"typeTrack":"tr_irriga","coordinates":"{\","typeSubstance":"A1","amountSubstance":"15"}
//                        JSONObject jsonObjectForKey = new JSONObject(data); // Lo mismo del anterior pero en Object
//                        String typeTrack = jsonObjectForKey.getString("typeTrack");
//                        String coords = jsonObjectForKey.getString("coordinates");
//                        int distance = jsonObjectForKey.getInt("distance");
//                        typeSubstanc = jsonObjectForKey.getString("typeSubstance");
//                        amountSubstanc = jsonObjectForKey.getString("amountSubstance");
//
//
//                        String path = companyName + "/" + typeTrack + "/" + dni + "/" + dateNow + "/" + timeNow;
//                        TrackingSupervision traced = new TrackingSupervision(distance, 1, coords, typeSubstanc, amountSubstanc);
//                        referenceCompanies.child(path).setValue(traced);
//
//                    }
//
//                    timer.cancel();
//                    presenter.successfulUpload();
//                    dateNow = null;
//                    timeNow = null;
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
