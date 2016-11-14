package com.example.izaya.smartoffice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by syamamura on 9/28/2016.
 *
 * Provide UI for displaying a meeting's content
 */
public class MeetingContent extends AppCompatActivity {

    private EditText mMeetingId = null;
    private EditText mMeetingName = null;
    private Button mDoneButton = null;
    private long mId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_content);

        mMeetingId = (EditText) findViewById(R.id.meeting_id);
        mMeetingName = (EditText) findViewById(R.id.meeting_name);
        mDoneButton = (Button) findViewById(R.id.done_button);

        // if updating meeting content, fill in texts here
        Intent intent = this.getIntent();
        int requestCode = intent.getIntExtra("RequestCode", 0);
        if(Defs.UPDATE_MEETING == requestCode) {

            mId = intent.getLongExtra("MeetingId", -1);
            String name = intent.getStringExtra("MeetingName");
            mMeetingId.setText("" + mId);
            mMeetingName.setText(name);
        }

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieve id and name from UI
                String id = mMeetingId.getText().toString();
                long meetingId = Long.parseLong(id);
                String meetingName = mMeetingName.getText().toString();

                // send id and name back to MeetingList activity
                Intent intent = new Intent();
                intent.putExtra("Id", mId); // current meeting id
                intent.putExtra("MeetingId", meetingId); // newly assigned meeting id
                intent.putExtra("MeetingName", meetingName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
