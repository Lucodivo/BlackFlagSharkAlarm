package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(view.getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(view.getContext(), 0, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*AlarmManager.INTERVAL_DAY,*/ pendingIntent);

        Toast.makeText(view.getContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}
