package com.sterbsociety.orarisapienza.models;

import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

import static com.sterbsociety.orarisapienza.utils.AppUtils.getDayByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getHourByIndex;

public class StudyPlanBuilder {

    @Nullable
    private Building startBuilding;

    private ArrayList<String[]> program;
    private ArrayList<Building> buildings;
    private StudyPlanPresenter spp;
    private HashMap<String, List<Integer>> dataMatrix;
    private ArrayList<Building> checked, nearby;
    private int st;
    private int en;
    public int radius = 500;

    public StudyPlanBuilder(ArrayList<Building> buildings, HashMap<String, List<Integer>> dataMatrix, StudyPlanPresenter spp) {
        this.buildings = buildings;
        this.dataMatrix = dataMatrix;
        this.checked = new ArrayList<>();
        this.nearby = new ArrayList<>();
        this.program = new ArrayList<>();
        this.spp = spp;
        this.st = AppUtils.timeToInt(spp.getHours()[0], AppUtils.dayToInt(spp.getDays()[0]));
        this.en = AppUtils.timeToInt(spp.getHours()[1], AppUtils.dayToInt(spp.getDays()[1]));
        this.startBuilding = spp.getBuilding();

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

    public void createProgramInt() {
        if (st > en) {
            fillNearby(startBuilding);
            findNextRoom(st, 341, startBuilding);
            fillNearby(startBuilding);
            findNextRoom(0, en, startBuilding);
        } else {
            fillNearby(startBuilding);
            findNextRoom(st, en, startBuilding);
        }
    }

    public ArrayList<String[]> getProgram() {
        return program;
    }

    private void findNextRoom(int start, int end, Building building) {
        if (start >= end) {
            return;
        }
        if (building == null) {
            if (this.program.size() > 0 && this.program.get(program.size() - 1)[3].equals("")) {
                this.program.get(program.size() - 1)[2] = getHourByIndex(start + 1);
                findNextRoom(start + 1, end, startBuilding);
                return;
            } else {
                this.program.add(new String[]{getDayByIndex(start), getHourByIndex(start),
                        getHourByIndex(start + 1), "", getDayByIndex(start + 1)});
                findNextRoom(start + 1, end, startBuilding);
                return;
            }
        }
        int max = start, i = start;
        String room = "";
        for (String s : dataMatrix.keySet()) {
            if (s.startsWith(building.code)) {
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
            this.program.add(new String[]{getDayByIndex(start), getHourByIndex(start + 1),
                    getHourByIndex((Math.min(end, max)) + 1), room, getDayByIndex((Math.min(end, max)) + 1)});
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
        if (radius < MAX_RAD) {
            radius += 100;
            fillNearby(b);
        } else {
            return null;
        }
        if (nearby.size() == 0) {
            return changeBuilding(b);
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

    public String[] getMoment(int i) {
        String stDate = spp.getStartDate();
        String enDate = spp.getEndDate();
        String[] progInd = program.get(i);
        if (stDate.substring(0, 3).toLowerCase().equals(progInd[3])) {
            return new String[]{stDate.substring(0, stDate.length() - 5) + progInd[1], stDate.substring(0, stDate.length() - 5) + progInd[2]};
        } else if (stDate.substring(0, 3).toLowerCase().equals(progInd[0])) {
            return new String[]{stDate.substring(0, stDate.length() - 5) + progInd[1], enDate.substring(0, stDate.length() - 5) + progInd[2]};
        } else {
            return new String[]{enDate.substring(0, stDate.length() - 5) + progInd[1], enDate.substring(0, stDate.length() - 5) + progInd[2]};
        }
    }

    public String getClassroomMoment(int i) {
        return program.get(i)[3];
    }
}