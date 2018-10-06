package com.sterbsociety.orarisapienza.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class POJO {

    public HashMap<String, HashMap<String, Integer>> timeTables;
    public HashMap<String, List<Integer>> matrix;
    public SapienzaMap smap;
    public List<String> alist;
    // String key is formed by courseName_courseCode
    public HashMap<String, HashMap<String, String>> specialCourses;

    public POJO() {
        // Required empty public constructor
    }

    public POJO(HashMap<String, HashMap<String, Integer>> timeTables, HashMap<String, List<Integer>> matrix, SapienzaMap smap, ArrayList<String> alist) {
        this.alist = alist;
        this.matrix = matrix;
        this.smap = smap;
        this.timeTables = timeTables;
    }

    public POJO(HashMap<String, HashMap<String, Integer>> timeTables, HashMap<String, List<Integer>> matrix, SapienzaMap smap, ArrayList<String> alist, HashMap<String, HashMap<String, String>> specialCourses) {
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
}