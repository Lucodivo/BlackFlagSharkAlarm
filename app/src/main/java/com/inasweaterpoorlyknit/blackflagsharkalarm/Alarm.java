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

public class Alarm extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener{

    private YouTubePlayerFragment playerFragment;
    public YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.alarm_player_fragment);
        playerFragment.initialize(DeveloperKey.ANDROID_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            this.player = youTubePlayer;
            SharedPreferences prefs = getSharedPreferences(Overview.ALARM_PREFS, MODE_PRIVATE);
            String songID = prefs.getString("AlarmID1", "I0M2gIhgvW0");
            this.player.loadVideo(songID);
            Log.d("Alarm.java: ", "YouTube Player successfully initialized");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("Alarm.java: ", "YouTube Player failed to initialize");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        this.player.play();
    }
}
