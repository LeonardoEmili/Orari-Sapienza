package com.sterbsociety.orarisapienza.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.sterbsociety.orarisapienza.utils.iVec2;

import java.util.ArrayList;

public class Building implements Parcelable {

    public String name, code, address;
    public iVec2 pos;
    public ArrayList<Classroom> aule;

    public Building(String name, String code) {
        this();
        this.name = name;
        this.code = code;
    }

    public Building() {
        // Required empty public constructor
        this.aule = new ArrayList<>();
        this.pos = new iVec2();
    }

    public double getLat() {
        return pos.getX();
    }

    public double getLong() {
        return pos.getY();
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
        out.writeParcelable(pos, flags);
        out.writeList(aule);
    }

    /**
     * This method is used to regenerate the object.
     * All Parcelables must have a CREATOR that implements these two methods
     */
    public static final Parcelable.Creator<Building> CREATOR = new Parcelable.Creator<Building>() {
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

    private Building(Parcel in) {
        this();
        name = in.readString();
        code = in.readString();
        pos = in.readParcelable(iVec2.class.getClassLoader());
        in.readList(aule, Classroom.class.getClassLoader());
    }
}