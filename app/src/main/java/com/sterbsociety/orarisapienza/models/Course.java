package com.sterbsociety.orarisapienza.models;

public class Course {

    private String name, id, courseKey;

    public Course(String name, String id, String courseKey) {
        this.name = name;
        this.id = id;
        this.courseKey = courseKey;
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

    public String getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
    }
}
