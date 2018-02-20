package com.sidewindercookie.glance;

/**
 * Created by hydrabolt on 2/20/18.
 */

public class Note {
    String name, details;

    public Note(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
}
