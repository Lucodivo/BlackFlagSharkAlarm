package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, Alarm.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
