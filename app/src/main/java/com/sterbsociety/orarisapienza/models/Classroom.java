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
    private int numberOfSeats;
    private double appeal;
    private Building building;

    public Classroom(String name, String id, int numberOfSeats, Building building) {
        this(name, id, numberOfSeats);
        this.building = building;
    }

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
        // todo add reference to his father, to be replaced with getBuilding
        return "foo";
    }

    public String getMainBuildingAddress() {
        // todo add reference to his father, to be replaced with building.getAddress
        return "Viale dell'universitá, 42";
    }

    public String getCurrentClass() {
        // todo add reference to db ? maybe ?
        return "Fondamenti di programmazione";
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
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
        out.writeString(name);
        out.writeString(code);
        out.writeInt(numberOfSeats);
        out.writeDouble(appeal);
        out.writeParcelable(building, flags);
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
        this.building = in.readParcelable(Building.class.getClassLoader());
    }
}