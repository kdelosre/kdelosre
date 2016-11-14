package com.sharplabs.myclientapp;

/**
 * Created by syamamura on 9/27/2016.
 *
 * Data object to contain Meeting content, currently just id and name.
 * (needed for Retrofit to work)
 */
public class Meeting {
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meeting(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.name;
    }
}
