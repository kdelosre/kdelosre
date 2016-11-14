package com.example.izaya.smartoffice;

/**
 * Created by syamamura on 10/3/2016.
 *
 * Data object to contain result of getQueryResponse() call
 * (needed for Retrofit to work)
 */
public class QueryResponse {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
