package com.markvac.line.tracing.repository;

import android.app.Activity;

import org.json.JSONArray;

/**
 * Created by unicorn on 8/12/2018.
 */

public interface TracingRepository {
    void uploadData(JSONArray upload, String company, String dni);
}
