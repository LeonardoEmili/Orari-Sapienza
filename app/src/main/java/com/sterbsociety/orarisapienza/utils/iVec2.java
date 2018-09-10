package com.sterbsociety.orarisapienza.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * In MapsActivity I used x as Latitude and y and Longitude.
 */
public class iVec2 implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(x);
        out.writeDouble(y);
    }

    /**
     * This method is used to regenerate the object.
     * All Parcelables must have a CREATOR that implements these two methods
     */
    public static final Parcelable.Creator<iVec2> CREATOR = new Parcelable.Creator<iVec2>() {
        public iVec2 createFromParcel(Parcel in) {
            return new iVec2(in);
        }

        public iVec2[] newArray(int size) {
            return new iVec2[size];
        }
    };

    private iVec2(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }
}