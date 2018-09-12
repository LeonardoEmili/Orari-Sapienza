package com.sterbsociety.orarisapienza.models;

import java.util.ArrayList;

public class SapienzaMap {

    private ArrayList<Building> buildings;

    public SapienzaMap() {
        buildings=new ArrayList<>();
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }
}
