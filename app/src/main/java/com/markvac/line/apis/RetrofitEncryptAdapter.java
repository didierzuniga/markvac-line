package com.markvac.line.apis;

import com.markvac.line.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by unicorn on 8/26/2018.
 */

public class RetrofitEncryptAdapter {
    Retrofit retrofit;
    public RetrofitEncryptAdapter(){
    }

    public Retrofit getAdapter(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_PYTHON)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
