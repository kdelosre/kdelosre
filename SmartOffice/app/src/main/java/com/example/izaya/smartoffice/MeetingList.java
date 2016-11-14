package com.example.izaya.smartoffice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Main activity to display query and meeting list related information
 */
public class MeetingList extends AppCompatActivity {

    private ArrayList<Meeting> mMeetings = new ArrayList<Meeting>();
    private Meeting mMeeting = null;
    private DeleteResult mResult = null;

    private ArrayAdapter mAdapter = null;
    private MeetingApi mMeetingService = null;
    private QueryApi mQueryService = null;
    private ListView mList = null;
    private int mSelected = -1;

    private EditText mQuery = null;
    private Button mSend = null;
    private TextView mResponse = null;
    private static String mServerUrl = null;

    private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver(this);
    private IntentFilter mFilter = new IntentFilter(Intent.ACTION_VIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // read in values from preferences (for now, only server url is read)
        updateValuesFromPreferences();

        // set up query related UI and processing
//        mQuery = (EditText) findViewById(R.id.query);
//        mSend = (Button) findViewById(R.id.send);
//        mResponse = (TextView) findViewById(R.id.response);

        // when send button is clicked, send query to server and receive
        // response
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mQuery.getText().toString();
                showQueryResponse(query);
            }
        });

        // set up meeting list related UI and processing

        // adapter holds data to be displayed in the list
        mAdapter = new ArrayAdapter<Meeting>(this, R.layout.simple_row, mMeetings);

        mList = (ListView) findViewById(R.id.meeting_list);
        mList.setAdapter(mAdapter);

        // when new list item is clicked, update current selection (single selection mode
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelected = position;
            }
        });

        // set up references to meeting and query services
        setupServices();

        // populate meeting list from server
        getMeetings();

        // subscribe to 'news' topic to get notification from Firebase
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    /**
     * Update references to services when server url is changed
     */
    private void setupServices() {
        // ret up retrofit related functionality
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServerUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMeetingService = retrofit.create(MeetingApi.class);
        mQueryService = retrofit.create(QueryApi.class);
    }

    /**
     * Read in various values from preferences stored in MyPrefernceActivity.
     * Currently only "server_url" is read from preferences.
     */
    private void updateValuesFromPreferences() {
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            mServerUrl = sp.getString(Prefs.SERVER_URL, Prefs.SERVER_URL_DEFAULT);
        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "URL not set.  Set from 'Preferences' menu...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        this.registerReceiver(mReceiver, mFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.preferences:
                // Go to MyPreferencesActivity to set up preferences, then come back
                Intent prefIntent = new Intent(this, MyPreferenceActivity.class);
                startActivityForResult(prefIntent, Defs.UPDATE_PREFERENCES);
                return true;
            case R.id.add_meeting:
                // when 'Add Meeting' is selected from menu, start
                // sub-activity to fill in id and name first
                Intent addIntent = new Intent(this, MeetingContent.class);
                addIntent.putExtra("RequestCode", Defs.ADD_MEETING);
                startActivityForResult(addIntent, Defs.ADD_MEETING);
                return true;
            case R.id.update_meeting:
                // when 'Update Meeting' is selected from menu, start
                // sub-activity to update id and name
                if(mSelected >= 0) {
                    Meeting selected = mMeetings.get(mSelected);
                    long id = selected.getId();
                    getMeetingAndStartActivity(id);
                }
                 return true;
            case R.id.delete_meeting:
                // when 'Delete Meeting' is selected from menu, immediately
                // delete currently selected meeting
                if(mSelected >= 0) {
                    Meeting selected = mMeetings.get(mSelected);
                    long id = selected.getId();
                    deleteMeeting(id);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

    /**
     * Retrieve meeting id and name then open sub-activity (MeetingContent) for
     * typing in values.  Once it is done, onActivityResult() is called for sending
     * request to server
     *
     * @param id Meeting id
     */
    private void getMeetingAndStartActivity(long id) {
        Call<Meeting> call = mMeetingService.getMeeting(id);
        call.enqueue(new Callback<Meeting>() {

            @Override
            public void onResponse(Response<Meeting> response, Retrofit retrofit) {
                Meeting meeting = response.body();
                mMeeting = meeting;
                long id = mMeeting.getId();
                String name = mMeeting.getName();

                Intent updateIntent = new Intent(MeetingList.this, MeetingContent.class);
                updateIntent.putExtra("RequestCode", Defs.UPDATE_MEETING);
                updateIntent.putExtra("Id", id);
                updateIntent.putExtra("MeetingId", id);
                updateIntent.putExtra("MeetingName", name);
                startActivityForResult(updateIntent, Defs.UPDATE_MEETING);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    /**
     * Called when sub-activity, 'MeetingContent' activity is closed
     *
     * @param requestCode 'ADD_MEETING' for adding meeting, 'UPDATE_MEETING" for updating meeting
     * @param resultCode ignored
     * @param data 'MeetingId' and 'MeetingName' are only data bassed back currently
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Defs.ADD_MEETING) {
            long id = data.getLongExtra("MeetingId", 0);
            String name = data.getStringExtra("MeetingName");
            Meeting meeting = new Meeting(id, name);
            addMeeting(meeting);
        } else if(requestCode == Defs.UPDATE_MEETING) {
            long id = data.getLongExtra("Id", 0);
            long newId = data.getLongExtra("MeetingId", 0);
            String newName = data.getStringExtra("MeetingName");
            Meeting meeting = new Meeting(newId, newName);
            updateMeeting(id, meeting);
        } else if(requestCode == Defs.UPDATE_PREFERENCES) {
            updateValuesFromPreferences();
            setupServices();
            getMeetings();
        }
    }

    /**
     * Retrieve all meetings currently present
     */
    private void getMeetings() {
        Call<List<Meeting>> call = mMeetingService.getMeetings();
        call.enqueue(new Callback<List<Meeting>>() {

            @Override
            public void onResponse(Response<List<Meeting>> response, Retrofit retrofit) {
                List<Meeting> meetings = response.body();
                mMeetings.clear();
                for(Meeting meeting : meetings) {
                    mMeetings.add(meeting);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    /**
     * Retrieve meeting specified its id
     *
     * @param id  Id of the meeting to be retrieved
     */
    private void getMeeting(long id) {
        Call<Meeting> call = mMeetingService.getMeeting(id);
        call.enqueue(new Callback<Meeting>() {

            @Override
            public void onResponse(Response<Meeting> response, Retrofit retrofit) {
                Meeting meeting = response.body();
                mMeeting = meeting;
             }

            @Override
            public void onFailure(Throwable t) {

            }
        });
     }

    /**
     * Add new meeting
     *
     * @param meeting New 'Meeting' object to be created
     */
    private void addMeeting(Meeting meeting) {
        Call<Meeting> call = mMeetingService.addMeeting(meeting);
        call.enqueue(new Callback<Meeting>() {

            @Override
            public void onResponse(Response<Meeting> response, Retrofit retrofit) {
                Meeting meeting = response.body();
                mMeeting = meeting;
                getMeetings();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    /**
     * Update existing meeting specified by id to the content of new 'Meeting' object
     *
     * @param id Id of the meeting to be updated
     * @param meeting New 'Meeting" object to be replaced by old one
     */
    private void updateMeeting(long id, Meeting meeting) {
        Call<Meeting> call = mMeetingService.updateMeeting(id, meeting);
        call.enqueue(new Callback<Meeting>() {

            @Override
            public void onResponse(Response<Meeting> response, Retrofit retrofit) {
                Meeting meeting = response.body();
                mMeeting = meeting;
                getMeetings();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    /**
     * Delete a meeting specified by its id
     *
     * @param id Id of the meeting to be deleted
     */
    private void deleteMeeting(long id) {
        Call<DeleteResult> call = mMeetingService.deleteMeeting(id);
        call.enqueue(new Callback<DeleteResult>() {

            @Override
            public void onResponse(Response<DeleteResult> response, Retrofit retrofit) {
                DeleteResult result = response.body();
                mResult = result;
                getMeetings();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    /**
     * Custom BroadcastReceiver object for updating meeting status changes
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {

        private MeetingList meetingList = null;

        public MyBroadcastReceiver(MeetingList meetingList) {
            this.meetingList = meetingList;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            meetingList.getMeetings();
        }
    }

    /**
     * Called to send query to the server to get text response
     *
     * @param query Query text to be sent to server for receiving request
     */
    private void showQueryResponse(String query) {
        Call<List<QueryResponse>> call = mQueryService.getQueryResponse(query);
        call.enqueue(new Callback<List<QueryResponse>>(){

            @Override
            public void onResponse(Response<List<QueryResponse>> response, Retrofit retrofit) {

                List<QueryResponse> responseList = response.body();
                if(responseList.size() > 0) {
                    String text = responseList.get(0).getText();
                    mResponse.setText(text);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // do nothing
            }
        });
   }
}
