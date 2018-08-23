package com.markvac.line.apis;

import com.markvac.line.models.Time;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by unicorn on 8/22/2018.
 */

public interface RetrofitDatetimeService {
    @GET("datetime/{code}")
    Call<Time> loadTime(@Path("code") String code);
}
