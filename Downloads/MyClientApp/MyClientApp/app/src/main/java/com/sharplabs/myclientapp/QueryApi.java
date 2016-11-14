package com.sharplabs.myclientapp;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by syamamura on 10/3/2016.
 *
 * Specify calling signatures of Query API call
 * (needed for Retrofit to work)
 */
public interface QueryApi {
    public static final String QUERY_PATH = "/queries/{query}";

    @GET(QUERY_PATH)
    Call<List<QueryResponse>> getQueryResponse(@Path("query") String query);
}
