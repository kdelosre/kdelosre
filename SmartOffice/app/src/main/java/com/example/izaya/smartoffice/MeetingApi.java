package com.example.izaya.smartoffice;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import static android.os.FileObserver.DELETE;

/**
 * Created by syamamura on 9/27/2016.
 *
 * Specify calling signatures of Meeting API calls
 * (needed for Retrofit to work)
 */
public interface MeetingApi {
    public static final String MEETINGS_PATH = "/meetings";
    public static final String MEETING_PATH = "/meetings/{id}";

    @GET(MEETINGS_PATH)
    Call<List<Meeting>> getMeetings();

    @GET(MEETING_PATH)
    Call<Meeting> getMeeting(@Path("id") long id);

    @POST(MEETINGS_PATH)
    Call<Meeting> addMeeting(@Body Meeting meeting);

    @PUT(MEETING_PATH)
    Call<Meeting> updateMeeting(@Path("id") long id, @Body Meeting meeting);

    @DELETE(MEETING_PATH)
    Call<DeleteResult> deleteMeeting(@Path("id") long id);
}
