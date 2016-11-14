package com.sharplabs.myclientapp;

/**
 * Created by syamamura on 9/28/2016.
 *
 * Data object to contain result of deleteMeeting() call
 * * (needed for Retrofit to work)
 */
public class DeleteResult {
    private String ok;
    private String n;

    String getOk() {
        return ok;
    }

    String getN() {
        return n;
    }
}
