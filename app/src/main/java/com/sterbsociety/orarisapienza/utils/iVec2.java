package com.sterbsociety.orarisapienza.utils;

/**
 * In MapsActivity I used x as Latitude and y and Longitude.
 */
public class iVec2 {

    private double x;
    private double y;

    public iVec2(double x,double y) {
        this.x=x;
        this.y=y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}