package com.markvac.line.models;

/**
 * Created by unicorn on 9/1/2018.
 */

public class TrackingSupervision {
    public int distance;
    public int duration;
    public String points;
    public String typesubstance;
    public String amountSubstance;

    public TrackingSupervision() {
    }

    public TrackingSupervision(int distance, int duration, String points, String typesubstance, String amountSubstance) {
        this.distance = distance;
        this.duration = duration;
        this.points = points;
        this.typesubstance = typesubstance;
        this.amountSubstance = amountSubstance;
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

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTypesubstance() {
        return typesubstance;
    }

    public void setTypesubstance(String typesubstance) {
        this.typesubstance = typesubstance;
    }

    public String getAmountSubstance() {
        return amountSubstance;
    }

    public void setAmountSubstance(String amountSubstance) {
        this.amountSubstance = amountSubstance;
    }
}
