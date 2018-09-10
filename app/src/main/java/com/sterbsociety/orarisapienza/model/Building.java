package com.sterbsociety.orarisapienza.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sterbsociety.orarisapienza.utils.iVec2;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Building implements Parcelable {

    private String name;
    private String code;
    private iVec2 pos;
    private String location;
    private ArrayList<Classroom> classrooms;
    private String[] other;

    public Building(String name, String code, iVec2 pos, String location) {
        this.classrooms = new ArrayList<>();
        this.name = name;
        this.code = code;
        this.pos = pos;
        this.location = location;
    }

    public Building(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void fillOther(String[] array) {
        this.other = array;
    }

    public void printAule() {
        for (Classroom classroom : this.classrooms) {
            classroom.printInfo();
        }
    }

    public double getLat() {
        return pos.getX();
    }

    public double getLong() {
        return pos.getY();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String[] getOther() {
        return other;
    }

    public String getLocation() {
        return location;
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
        out.writeString(location);
        out.writeList(classrooms);
        out.writeStringArray(other);
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
        name = in.readString();
        code = in.readString();
        pos = in.readParcelable(iVec2.class.getClassLoader());
        location = in.readString();
        in.readList(classrooms, Classroom.class.getClassLoader());
        other = in.createStringArray();
    }
}
