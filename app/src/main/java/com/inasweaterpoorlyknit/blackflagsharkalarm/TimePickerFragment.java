package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

// time picker fragment to more easily get date information form the user
public class TimePickerFragment extends DialogFragment {
    // used to record the activity the dialog is being called from
    private Activity mActivity;
    // used to record which object is responding to the results of the users chosen time
    private TimePickerDialog.OnTimeSetListener mListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        
        // set the activity as the one that called TimePickerFragment
        mActivity = activity;

        try{
            // set the listener as the activity that is listening for the chose time(and implements TimePickerDialog.OnTimeSetListener)
            mListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // create a calendar with the instance of the current time
        final Calendar c = Calendar.getInstance();
        // set the hour and minute to the time chosen by the user
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // TimePickerDialog(context, OnTimeSetListener, hour user chose, minute user chose, whether the activity is a 24-hour view)
        return new TimePickerDialog(mActivity, mListener, hour, minute, DateFormat.is24HourFormat(mActivity));
    }
}
