package com.sterbsociety.orarisapienza.model;

import com.sterbsociety.orarisapienza.utils.iVec2;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Building {

    private String name;
    private String code;
    private iVec2 pos;
    private String location;
    private ArrayList<Classroom> classrooms;
    private String[] other;

    public Building(String name, String code, iVec2 pos, String location) {
        this.classrooms = new ArrayList<>();
        this.name=name;
        this.code=code;
        this.pos=pos;
        this.location=location;
    }

    public Building(String name, String code) {
        this.name=name;
        this.code=code;
    }

    public void fillOther(String[] array) {
        this.other=array;
    }

    public void printAule() {
        for(Classroom classroom:this.classrooms) {
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
}
