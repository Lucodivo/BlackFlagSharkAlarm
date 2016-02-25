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

public class Overview extends AppCompatActivity
    implements TimePickerDialog.OnTimeSetListener {
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    // alarm button asks for a specific time and sets the alarm
    private Button alarmButton;
    // test button should test the alarm
    private Button testButton;
    // pick button allows the user to pick a song to wake up to
    private Button pickButton;


    public static final String ALARM_PREFS = "AlarmPrefs";
    // the video id and title of the song the user picked.
    private String returnedVideoID;
    //private String returnedVideoTitle;

    private static final int SEARCH_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // access the pick song button and retrieve a song through the SearchActivity class
        pickButton = (Button)findViewById(R.id.pick_song_button);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(v.getContext(), SearchActivity.class);
                searchIntent.putExtra("song", "name");
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

    public void setAlarm(){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void testAlarm(){
        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("Song ID", this.returnedVideoID);
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
                    SharedPreferences.Editor editor = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE).edit();
                    editor.putString("AlarmID1", returnedVideoID);
                    editor.commit();
                    //returnedVideoTitle = data.getStringExtra("Song Title");
                    Toast.makeText(this, "Video saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(view.getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("Song ID", this.returnedVideoID);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*AlarmManager.INTERVAL_DAY,*/ pendingIntent);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}
