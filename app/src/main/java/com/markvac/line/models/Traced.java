package com.markvac.line.models;

/**
 * Created by unicorn on 9/1/2018.
 */

public class Traced {
    public int distance;
    public int duration;
    public double gallons;
    public String points;

    public Traced() {
    }

    public Traced(int distance, int duration, String points) {
        this.distance = distance;
        this.duration = duration;
        this.points = points;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getGallons() {
        return gallons;
    }

    public void setGallons(double gallons) {
        this.gallons = gallons;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
