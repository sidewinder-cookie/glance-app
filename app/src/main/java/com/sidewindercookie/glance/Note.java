package com.sidewindercookie.glance;

/**
 * Created by hydrabolt on 2/20/18.
 */

public class Note {
    String name, details;
    InformalLocation informalLocation;

    public Note(String name, String details, InformalLocation location) {
        this.name = name;
        this.details = details;
        this.informalLocation = location;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
    public InformalLocation getInformalLocation() { return informalLocation; }
}
