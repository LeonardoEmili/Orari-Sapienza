package com.sterbsociety.orarisapienza.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Classroom implements Parcelable {

    private String name, code;
    private int numberOfSeats;
    private double appeal;

    public Classroom(String name, String id, int numberOfSeats) {
        this.name = name;
        this.code = id;
        this.numberOfSeats = numberOfSeats;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public double getAppeal() {
        return appeal;
    }

    public void setAppeal(double appeal) {
        this.appeal = appeal;
    }

    public String getMainBuilding() {
        // todo add reference to his father
        // should return getFather().getName();
        return "foo";
    }

    public String getMainBuildingAddress() {
        // todo add reference to his father
        // should return getFather().getAddress;
        return "bar";
    }

    public String getCurrentClass() {
        // todo add reference to db ? maybe ?
        return "Fondamenti di programmazione";
    }

    // Returns available/occupied
    public String getStatus() {
        return "Occupata";
    }

    public String getCurrentClassTime() {
        return "10:30 - 13:00";
    }

    public void printInfo() {
        System.out.print("   | ");
        System.out.println(this.name);
        System.out.print("       | ");
        System.out.println(this.numberOfSeats);
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
        out.writeString(this.name);
        out.writeString(this.code);
        out.writeInt(this.numberOfSeats);
        out.writeDouble(this.appeal);
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
        this.numberOfSeats = in.readInt();
        this.appeal = in.readDouble();
    }
}