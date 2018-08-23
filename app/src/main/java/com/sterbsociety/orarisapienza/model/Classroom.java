package com.sterbsociety.orarisapienza.model;

public class Classroom {

    private String name, code;
    private int numberOfSeats;
    private double appeal;

    public Classroom(String name, String id, int numberOfSeats) {
        this.name = name;
        this.code = id;
        this.numberOfSeats = numberOfSeats;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public double getAppeal() {
        return appeal;
    }

    public void setAppeal(double appeal) {
        this.appeal = appeal;
    }

    public String getMainBuilding() {
        // todo add reference to his father
        // should return getFather().getName();
        return "foo";
    }

    public void printInfo() {
        System.out.print("   | ");
        System.out.println(this.name);
        System.out.print("       | ");
        System.out.println(this.numberOfSeats);
        System.out.print("       | ");
        System.out.println(this.code);
    }
}