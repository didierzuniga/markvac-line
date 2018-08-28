package com.markvac.line.apis;

import com.markvac.line.models.Time;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by unicorn on 8/26/2018.
 */

public interface RetrofitEncryptService {
    @GET("process-data/{pass}")
    Call<Time> loadTime(@Path("pass") String code);
}
