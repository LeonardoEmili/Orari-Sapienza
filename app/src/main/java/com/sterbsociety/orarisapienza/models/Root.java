package com.sterbsociety.orarisapienza.models;

public class Root {

    public POJO finalDB;
    public String version = "1.0.0";

    public void setSmap(SapienzaMap smap) {
        finalDB.smap = smap;
    }

    public SapienzaMap getSmap() {
        return finalDB.smap;
    }
}