package com.sterbsociety.orarisapienza.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is just an useful wrapper which contains all the info
 * required to form a Study Plan.
 */
public class StudyPlanPresenter implements Parcelable {

    private String startDate, endDate;
    private double latitude, longitude;
    private Building building;

    /**
     *  Since the user is allowed to fast create the plan by skipping settings,
     *  we can check these values and if they're both -1 then we have to
     *  create a study plan inside the campus.
     */
    public StudyPlanPresenter() {
        latitude = -1;
        longitude = -1;
    }

    public StudyPlanPresenter(String startDate, String endDate, double latitude, double longitude, Building building) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.building = building;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setGpsLocation(Location gpsLocation) {
        if (gpsLocation != null) {
            this.latitude = gpsLocation.getLatitude();
            this.longitude = gpsLocation.getLongitude();
        }
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public double getLatitude() {
        if (building != null)
            return building.getLat();
        return latitude;
    }

    public double getLongitude() {
        if (building != null) {
            return building.getLong();
        }
        return longitude;
    }

    public void setDates(Date firstDate, Date secondDate, SimpleDateFormat simpleDateFormat) {
        startDate = simpleDateFormat.format(firstDate);
        endDate = simpleDateFormat.format(secondDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(startDate);
        out.writeString(endDate);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeParcelable(building, flags);
    }

    /**
     * This method is used to regenerate the object.
     * All Parcelables must have a CREATOR that implements these two methods
     */
    public static final Parcelable.Creator<StudyPlanPresenter> CREATOR = new Parcelable.Creator<StudyPlanPresenter>() {
        public StudyPlanPresenter createFromParcel(Parcel in) {
            return new StudyPlanPresenter(in);
        }

        public StudyPlanPresenter[] newArray(int size) {
            return new StudyPlanPresenter[size];
        }
    };

    private StudyPlanPresenter(Parcel in) {
        startDate = in.readString();
        endDate = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        building = in.readParcelable(Building.class.getClassLoader());
    }
}
