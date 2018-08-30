package com.markvac.line.tracing.repository;

/**
 * Created by unicorn on 8/12/2018.
 */

public interface TracingRepository {
    void saveCoordinates(String coordinates, int duration, int distance, String userId);
}
