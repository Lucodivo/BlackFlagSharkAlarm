package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Overview extends AppCompatActivity{
    private Button alarmButton;
    private Button testButton;
    private Button pickButton;

    private String returnedVideoID;
    private String returnedVideoTitle;

    private static final int SEARCH_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        alarmButton = (Button)findViewById(R.id.alarm_activity_button);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(v);
            }
        });
        testButton = (Button)findViewById(R.id.test_activity_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAlarm(v);
            }
        });
        pickButton = (Button)findViewById(R.id.pick_song_button);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(v.getContext(), SearchActivity.class);
                searchIntent.putExtra("song", "name");
                startActivityForResult(searchIntent, SEARCH_CODE);
            }
        });
    }

    public void setAlarm(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void testAlarm(View v){
        Intent intent = new Intent(this, Alarm.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            // Check which request we're responding to
            if (requestCode == SEARCH_CODE) {
                if(data.getExtras().containsKey("Song ID")){
                    returnedVideoID = data.getStringExtra("Song ID");
                    returnedVideoTitle = data.getStringExtra("Song Title");
                }
            }
        }
    }
}
