package com.markvac.line.apis;

import com.markvac.line.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by unicorn on 8/22/2018.
 */

public class RetrofitDatetimeAdapter {
    Retrofit retrofit;
    public RetrofitDatetimeAdapter(){
    }

    public Retrofit getAdapter(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_DATE_TIME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
