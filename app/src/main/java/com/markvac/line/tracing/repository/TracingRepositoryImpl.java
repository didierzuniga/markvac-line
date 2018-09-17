package com.markvac.line.tracing.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markvac.line.apis.RetrofitDatetimeAdapter;
import com.markvac.line.apis.RetrofitDatetimeService;
import com.markvac.line.models.Time;
import com.markvac.line.models.TrackingSupervision;
import com.markvac.line.tracing.interactor.TracingInteractor;
import com.markvac.line.tracing.presenter.TracingPresenter;

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

    private Timer timer;
    private String coords, dateNow, timeNow, dni, companyName;
    private int durat, distan;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceCompanies = database.getReference("companies");
    DatabaseReference referenceUsers = database.getReference("users");

    public TracingRepositoryImpl(TracingPresenter presenter, TracingInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void saveCoordinates(String coordinates, int duration, int distance, String userId, String company) {
        coords = coordinates;
        durat = duration;
        distan = distance;
        dni = userId;
        companyName = company;
        //DATETIME
        Retrofit retrofit = new RetrofitDatetimeAdapter().getAdapter();
        RetrofitDatetimeService service = retrofit.create(RetrofitDatetimeService.class);
        Call<Time> call;
        // Hardcoded Country "CO"
        call = service.loadTime("CO");
        call.enqueue(new Callback<Time>() {
            @Override
            public void onResponse(Call<Time> call, Response<Time> response) {
                dateNow = response.body().getDate();
                timeNow = response.body().getTime();
            }

            @Override
            public void onFailure(Call<Time> call, Throwable t) {
                //Error handler
            }
        });
        //DATETIME

        timer = new Timer();
        timer.schedule(new sendData(), 1000, 1000);
    }

    public class sendData extends TimerTask {
        @Override
        public void run() {
            if (timeNow != null || dateNow != null){
                String path = companyName + "/tracing/" + dni + "/" + dateNow + "/" + timeNow;
                TrackingSupervision traced = new TrackingSupervision(distan, durat, coords);
                referenceCompanies.child(path).setValue(traced);
                timer.cancel();
                presenter.successfulStore();
                dateNow = null;
                timeNow = null;
            }
        }
    }
}
