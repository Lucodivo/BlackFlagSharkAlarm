package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// this is the activity that plays the video when the alarm goes off
// It is NOT the activity that actually sets the alarm
public class Alarm extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener{

    // YouTubePlayerFragment that will hold the player
    private YouTubePlayerFragment playerFragment;
    // player to play the alarm
    public YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // these options allow the alarm to wake up the phone and play the youtube video
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // access the YouTubePlayerFragment of the activity
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.alarm_player_fragment);
        // attempt to initialize the YouTubePlayer
        playerFragment.initialize(DeveloperKey.ANDROID_KEY, this);
    }

    // function called if the YouTubePlayerFragment is sucessfully initialized
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        // only load a video if the player was not initalizd through a restoration
        if(!wasRestored){
            // set the activity's YouTubePlayer to the player retrieved from playerFragment.initalize()
            this.player = youTubePlayer;
            // retrieve the preferences of the app for the user
            SharedPreferences prefs = getSharedPreferences(Overview.ALARM_PREFS, MODE_PRIVATE);
            // if the preferences does not find a preset song, it will play Heart Gongs by Software Blonde
            String songID = prefs.getString("AlarmID1", "I0M2gIhgvW0");
            this.player.loadVideo(songID);
            // debug log that the youtube player sucessfully initialized
            Log.d("Alarm.java: ", "YouTube Player successfully initialized");
        }
        // TODO: MIGHT BE ABLE TO RESUME PLAY IN AN ELSE STATEMENT INSTEAD OF onConfigurationChanged
    }

    // function called if the YouTubePlayerFragment is unsucessfully initialized
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        // debug log that the youtube player failed to initialize
        Log.d("Alarm.java: ", "YouTube Player failed to initialize");
    }

    // currently used for the effects of an orientation change
    // MAY BE ABLE TO REMOVE AND JUST USE onInitalizationSuccess
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        // resume the video that would have been paused otherwise
        this.player.play();
    }
}
