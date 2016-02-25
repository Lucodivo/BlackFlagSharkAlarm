package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// a BroadcastRecever class that activates Alarm.java when called
public class AlarmReceiver extends BroadcastReceiver {
    // function called when 
    @Override
    public void onReceive(Context context, Intent intent) {
        // intent that calls alarm class
        Intent alarmIntent = new Intent(context, Alarm.class);
        // ensure the inent opens up a task for the activity being called(won't open another task if one is alreayd open for activity)
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // start the activity
        context.startActivity(alarmIntent);
    }
}
