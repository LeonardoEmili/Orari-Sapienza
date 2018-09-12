package com.sterbsociety.orarisapienza.models;

// todo ref to Facoltá?
public class Course {

    private String name;
    private String id;

    public Course(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return id + " - " + name;
    }
}
