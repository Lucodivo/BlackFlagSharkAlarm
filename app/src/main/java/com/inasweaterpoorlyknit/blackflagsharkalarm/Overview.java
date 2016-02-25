package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

// this is the "main" java file of the program. It will in, in the future, contain an overview
// of all the alarms and give the user the ability to add multiple alarms
public class Overview extends AppCompatActivity
    implements TimePickerDialog.OnTimeSetListener {
    // the pending intent will allow us to call the BroadcastReceiver(AlarmReceiver in this instance)
    // at a point in time where our program may not be currently running
    private PendingIntent pendingIntent;
    // AlarmManager is used to call Alarm.java(to set off the YouTube alarm) at a specified time
    private AlarmManager alarmManager;

    // alarm button asks for a specific time and sets the alarm
    private Button alarmButton;
    // test button should test the alarm
    private Button testButton;
    // pick button allows the user to pick a song to wake up to
    private Button pickButton;

    // public string holding the name of the app's user preferences
    public static final String ALARM_PREFS = "AlarmPrefs";
    // the video id and title of the song the user picked.
    private String returnedVideoID;
    //private String returnedVideoTitle;

    // this used in OnActivityResult, it helps indicate whether that result was from the SearchActivity.java
    private static final int SEARCH_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // access the pick song button and retrieve a song through SearchActivity.java
        pickButton = (Button)findViewById(R.id.pick_song_button);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a search intent
                Intent searchIntent = new Intent(v.getContext(), SearchActivity.class);
                // add a string called "song" holding the string value "name"
                // currently for verification purposes, may be useless
                searchIntent.putExtra("song", "name");
                // call the activity for results(activity will return a video id and video name)
                startActivityForResult(searchIntent, SEARCH_CODE);
            }
        });

        // access the set alarm button and call the setAlarm() function when the button is clicked
        alarmButton = (Button)findViewById(R.id.alarm_activity_button);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        // access the test alarm button and call the testAlarm() function when the button is clicked
        testButton = (Button)findViewById(R.id.test_activity_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAlarm();
            }
        });
    }

    // set the alarm
    public void setAlarm(){
        // create a new TimePickerFragment that will help us retrieve the time from the user
        DialogFragment newFragment = new TimePickerFragment();
        // display this fragment to the user
        newFragment.show(getFragmentManager(), "timePicker");
        // information from the TimePickerFragment will be handle in this file, as it extends TimePickerDialog.OnTimeSetListener
        // it is handled in the OnTimeSet function
    }

    // allows the user to test the current alarm(maybe adjust volume )
    public void testAlarm(){
        // create intent to start test alarm.java
        Intent intent = new Intent(this, Alarm.class);
        // start the alarm activity
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
                    // TODO: Can probably just remove the returnedVideoID string and add directly to SharedPreferences
                    returnedVideoID = data.getStringExtra("Song ID");
                        //returnedVideoTitle = data.getStringExtra("Song Title");
                    // get shared preference for our app and add the song the user chose and commit the changes
                    SharedPreferences.Editor editor = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE).edit();
                    editor.putString("AlarmID1", returnedVideoID);
                    editor.commit();
                    // tell the user the video was saved
                    Toast.makeText(this, "Video saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // this function is called after the user picks a time for the alarm
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // initialize the alarm manager
        alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

        // create an alarm intent that will be called by the pendingItent
        Intent alarmIntent = new Intent(view.getContext(), AlarmReceiver.class);
        // pending intent that will call AlarmReceiver.java at a specified time
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        // get calendar instance and set it to current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // update the hour and day accoring to the user's choice
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        // we always want it to set off at 0 seconds on the minute
        calendar.set(Calendar.SECOND, 0);

        // if the time set was less than the current time, add a day, since the user wants the alarm
        // to go off the next day and not some random hour and minute in the past
        if(calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
            calendar.add(Calendar.DATE, 1);
        }
        
        // we use set exact to ensure the alarm goes off at the exact time the user wants
        // it takes the pending intent and will wake the phone up when it goes off
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*AlarmManager.INTERVAL_DAY,*/ pendingIntent);

        // inform the user the alarm is set
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}
