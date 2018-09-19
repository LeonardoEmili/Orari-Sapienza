package com.sterbsociety.orarisapienza.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class implements Parcelable, which is a way of Android to serialize an Object.
 * Doc about it here: http://www.vogella.com/tutorials/AndroidParcelable/article.html
 * This is a model for many RecyclerViews and other views, it should keep a reference
 * to its father in getMainBuilding() for better retrieving data.
 */
public class Classroom implements Parcelable {

    private String name, code;
    private int sits;   //numberOfSeats
    private double appeal;
    private String buildingCode;
    private int buildingIndex;

    public Classroom(String name, String code, int sits, Building building, int buildingIndex) {
        this(name, code, sits, building);
        this.buildingIndex = buildingIndex;
    }

    public Classroom(String name, String code, int sits, Building building) {
        this(name, code, sits);
        this.buildingCode = building.getCode();
    }

    public Classroom(String name, String code, int sits) {
        this.name = name;
        this.code = code;
        this.sits = sits;
    }

    public Classroom() {
        // Required empty public constructor
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSits() {
        return sits;
    }

    public void setSits(int sits) {
        this.sits = sits;
    }

    public double getAppeal() {
        return appeal;
    }

    public void setAppeal(double appeal) {
        this.appeal = appeal;
    }

    public int getBuildingIndex() {
        return buildingIndex;
    }

    public void setBuildingIndex(int buildingIndex) {
        this.buildingIndex = buildingIndex;
    }

    public String getFullCode() {
        return buildingCode + "-" + code;
    }

    public void printInfo() {
        System.out.print("   | ");
        System.out.println(this.name);
        System.out.print("       | ");
        System.out.println(this.sits);
        System.out.print("       | ");
        System.out.println(this.code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(code);
        out.writeInt(sits);
        out.writeDouble(appeal);
        out.writeString(buildingCode);
        out.writeInt(buildingIndex);
    }

    /**
     * This method is used to regenerate the object.
     * All Parcelables must have a CREATOR that implements these two methods
     */
    public static final Parcelable.Creator<Classroom> CREATOR = new Parcelable.Creator<Classroom>() {
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    private Classroom(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
        this.sits = in.readInt();
        this.appeal = in.readDouble();
        this.buildingCode = in.readString();
        this.buildingIndex = in.readInt();
    }
}