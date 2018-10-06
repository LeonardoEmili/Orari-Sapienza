package com.sterbsociety.orarisapienza.models;

import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudyPlanBuilder {

    private ArrayList<String[]> program = new ArrayList<>();
    private ArrayList<Building> buildings;
    private Building startBuilding;
    private HashMap<String, List<Integer>> dataMatrix;
    private ArrayList<Building> checked, nearby;
    public int radius = 500;

    public StudyPlanBuilder(ArrayList<Building> buildings, HashMap<String, List<Integer>> dataMatrix, Building startBuilding) {
        this.buildings = buildings;
        this.dataMatrix = dataMatrix;
        this.checked = new ArrayList<>();
        this.nearby = new ArrayList<>();
        this.startBuilding = startBuilding;
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

    public void createProgramInt(int start, int end) {
        fillNearby(startBuilding);
        findNextRoom(start, end, startBuilding);
    }

    public ArrayList<String[]> getProgram() {
        return program;
    }

    private void findNextRoom(int start, int end, Building building) {
        if (start >= end) {
            return;
        }
        if (building == null) {
            if (this.program.get(program.size() - 1)[3].equals("")) {
                this.program.get(program.size() - 1)[2] = AppUtils.getHourByIndex(start + 1);
                findNextRoom(start + 1, end, startBuilding);
                return;
            } else {
                this.program.add(new String[]{AppUtils.getDayByIndex(start), AppUtils.getHourByIndex(start), AppUtils.getHourByIndex(start + 1), ""});
                findNextRoom(start + 1, end, startBuilding);
                return;
            }
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
            startBuilding = building;
            findNextRoom(max, end, building);
        } else {
            findNextRoom(start, end, changeBuilding(building));
        }

    }

    private Building changeBuilding(Building b) {
        checked.add(b);
        int MAX_RAD = 2000;
        if (nearby.size() == 0 && radius < MAX_RAD) {
            radius += 100;
            fillNearby(b);
        } else if (radius >= MAX_RAD) {
            return null;
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