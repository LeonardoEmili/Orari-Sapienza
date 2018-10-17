package com.sterbsociety.orarisapienza.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class POJO {

    public HashMap<String, HashMap<String, List<Integer>>> timeTables = new HashMap<>();
    public HashMap<String, List<Integer>> matrix = new HashMap<>();
    public SapienzaMap smap = new SapienzaMap();
    public List<String> alist = new ArrayList<>();
    // String key is formed by courseName_courseCode
    public HashMap<String, HashMap<String, String>> specialCourses = new HashMap<>();

    public POJO() {
        // Required empty public constructor
    }

    public POJO(HashMap<String, HashMap<String, List<Integer>>> timeTables, HashMap<String, List<Integer>> matrix, SapienzaMap smap, ArrayList<String> alist) {
        this.alist = alist;
        this.matrix = matrix;
        this.smap = smap;
        this.timeTables = timeTables;
    }

    public POJO(HashMap<String, HashMap<String, List<Integer>>> timeTables, HashMap<String, List<Integer>> matrix, SapienzaMap smap, ArrayList<String> alist, HashMap<String, HashMap<String, String>> specialCourses) {
        this.alist = alist;
        this.matrix = matrix;
        this.smap = smap;
        this.timeTables = timeTables;
        this.specialCourses = specialCourses;
    }

    private HashMap<String, HashMap<String, String>> getSpecialCourses() {
        if (specialCourses == null) {
            specialCourses = new HashMap<>();
        }
        return specialCourses;
    }

    public void setAlist(List<String> alist) {
        this.alist = alist;
    }

    public void setSpecialCourses(HashMap<String, HashMap<String, String>> specialCourses) {
        this.specialCourses = specialCourses;
    }

    public void setTimeTables(HashMap<String, HashMap<String, List<Integer>>> timeTables) {
        this.timeTables = timeTables;
    }

    public void setMatrix(HashMap<String, List<Integer>> matrix) {
        this.matrix = matrix;
    }

    public void setSmap(SapienzaMap smap) {
        this.smap = smap;
    }

    public SapienzaMap getSmap() {
        return smap;
    }
}