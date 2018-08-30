package com.markvac.line.tracing.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markvac.line.apis.RetrofitDatetimeAdapter;
import com.markvac.line.apis.RetrofitDatetimeService;
import com.markvac.line.models.Time;
import com.markvac.line.models.User;
import com.markvac.line.tracing.interactor.TracingInteractor;
import com.markvac.line.tracing.presenter.TracingPresenter;

import java.util.ArrayList;
import java.util.List;
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
    private String coords, dateNow, timeNow, username;
    private int durat, distan;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceCompanies = database.getReference("companies");
    DatabaseReference referenceUsers = database.getReference("users");

    public TracingRepositoryImpl(TracingPresenter presenter, TracingInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void saveCoordinates(String coordinates, int duration, int distance, String userId) {
        coords = coordinates;
        durat = duration;
        distan = distance;
        username = userId;
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
            if (timeNow != null){
                referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if ((snapshot.getKey()).equals(username)){
                                User user = snapshot.getValue(User.class);
                                String company = user.getCompany();
                                String path = company + "/tracing/" + username;
                                referenceCompanies.child(path).child(dateNow).child(timeNow).child("points").setValue(coords);
                                referenceCompanies.child(path).child(dateNow).child(timeNow).child("duration").setValue(durat);
                                referenceCompanies.child(path).child(dateNow).child(timeNow).child("distance").setValue(distan);

                                timer.cancel();
                                presenter.successfulStore();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
