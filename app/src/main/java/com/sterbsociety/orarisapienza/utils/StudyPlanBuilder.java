package com.sterbsociety.orarisapienza.utils;

import com.sterbsociety.orarisapienza.models.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudyPlanBuilder {
    private ArrayList<String[]> program = new ArrayList<>();
    private ArrayList<Building> buildings;
    private HashMap<String, List<Integer>> dataMatrix;
    private List<String> alist;
    private ArrayList<Building> checked = new ArrayList<>();
    private ArrayList<Building> nearby = new ArrayList<>();
    int radius = 500;

    public StudyPlanBuilder(ArrayList<Building> buildings, HashMap<String, List<Integer>> dataMatrix, List<String> alist) {
        this.buildings = buildings;
        this.dataMatrix = dataMatrix;
        this.alist = alist;
    }

    public void add(String classroom, String time) {
        this.program.add(new String[]{classroom, time});
    }

    public void remove(int i) {
        this.program.remove(i);
    }

    public String[] get(int i) {
        return this.program.get(i);
    }

    public void createProgramInt(int start, int end, Building building) {
        fillNearby(building);
        findNextRoom(start, end, building);
    }

    public ArrayList<String[]> getProgram() {
        return program;
    }

    private void findNextRoom(int start, int end, Building building) {
        if (start >= end) {
            return;
        }
        int max = start, i = start;
        String room = "";
        for (String s : dataMatrix.keySet()) {
            if (s.startsWith(building.getCode())) {
                while (i < dataMatrix.get(s).size() && dataMatrix.get(s).get(i) == 0) {
                    i++;
                }
                if (i > max) {
                    max = i;
                    room = s;
                }
                i = start;
            }
        }
        if (max > start) {
            this.program.add(new String[]{AppUtils.getDayByIndex(start), AppUtils.getHourByIndex(start), AppUtils.getHourByIndex((Math.min(end, max))), room});
            checked.clear();
            fillNearby(building);
            findNextRoom(max, end, building);
        } else {
            findNextRoom(start, end, changeBuilding(building));
        }

    }

    private Building changeBuilding(Building b) {
        checked.add(b);
        if (nearby.size() == 0) {
            radius += 100;
            fillNearby(b);
        }
        return nearby.remove(0);
    }

    private void fillNearby(Building building) {
        double d;
        nearby.clear();
        for (Building b : buildings) {
            if (!this.checked.contains(b)) {
                d = AppUtils.haversine(b.pos.getX(), b.pos.getY(), building.pos.getX(), building.pos.getY());
                if (0 < d && d <= radius) {
                    nearby.add(b);
                }
            }
        }
    }
}
