package com.inasweaterpoorlyknit.blackflagsharkalarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.api.services.youtube.model.SearchResult;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SearchActivity extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener{

    private YouTubePlayerFragment playerFragment; // youtube player fragment to play searched videos
    private static final int RECOVERY_DIALOG_REQUEST = 1; // used for youtube's onInitializedFailure

    public YouTubePlayer player; // the player initialized by playerFragment; object that controls the player

    private Object lock = new Object(); // a lock object used for synchronization with task
    public List<SearchResult> searchResults; // list to hold the search results from youtube's search api
    public ArrayList<String> resultTitles = new ArrayList<>();  // list to hold titles from the search

    // used to update listView
    private int playingVideoIndex;  // index of the video being played

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // set playing video to -1, to check if a search ever occurred
        playingVideoIndex = -1;


        // accessing our player fragment through the contentView
        // setting up the youtube player through the playerFragment
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.search_player_fragment);

        // Accessing all of our components
        final Button searchButton = (Button) findViewById(R.id.search_button); // button to get YouTube search results
        final EditText songEditText = (EditText) findViewById(R.id.song_text); // editText for getting song from user
        final EditText artistEditText = (EditText) findViewById(R.id.artist_text); // editText for getting artist from user
        final ListView searchListView = (ListView) findViewById(R.id.search_list_view); // listView to show search results
        final FloatingActionButton returnButton = (FloatingActionButton) findViewById(R.id.return_button); // upload button to send song to host

        // input method manager to control when the keyboard is active
        final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        playerFragment.initialize(DeveloperKey.ANDROID_KEY, this);

        // if the user says they are done editing, search for results
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if(actionID == EditorInfo.IME_ACTION_DONE){
                    searchButton.performClick();
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        // the YouTube search functionality
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // tast for thread, so we can access networks outside of the main thread
                Runnable searchTask = new Runnable() {
                    @Override
                    public void run() {
                        // concatenate the artist and song names from the user
                        // NOTE: Maybe not have it be two things. Unnecessary but might look better?
                        String query = artistEditText.getText().toString() + " " + songEditText.getText().toString();

                        // this synchronized lock ensures we don't display the search results until they are found
                        synchronized (lock) {
                            //calling our search function to access YouTube's api and return the search results
                            searchResults = Search.Search(query, DeveloperKey.BROWSER_KEY);
                            lock.notify();
                        }

                        // debug info to ensure our search was successful
                        if(searchResults.get(0).getId().getVideoId() != null){
                            Log.d("newSongID: ", "new song id is " + searchResults.get(0).getId().getVideoId());
                        } else {
                            Log.d("newSongID: ", "couldn't get new song id");
                        }
                    }
                };

                // creating a thread to search for the videos
                Thread threadObj = new Thread(searchTask);
                threadObj.start();

                // ensuring that we do not access the searchResults until the search has finished
                synchronized(lock) {
                    try {
                        // have the object wait until it is notified
                        lock.wait();

                        // play the first video that is returned in the searchResults
                        player.loadVideo(searchResults.get(0).getId().getVideoId());
                        playingVideoIndex = 0;  // set our play video index to the first video
                        if(searchResults != null) { // if there are results to return
                            resultTitles.clear();  // first clear the result Titles
                            // for each searchResult, set it in the result Titles
                            for (SearchResult searchResult : searchResults) {
                                resultTitles.add(searchResult.getSnippet().getTitle());
                            }
                            // create a String adapter and fill it with the searchResults
                            searchListView.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, resultTitles));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // if an item in the list is clicked, get it's current index in the listView, play the song
        // and set our playingVideoIndex to the correct index
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                player.loadVideo(searchResults.get(i).getId().getVideoId());
                playingVideoIndex = i;
            }
        });

        // button to return to our ClientMainActivity
        // it returns the song ID and the song title through putExtra
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // only do something if the user actually searched for a video
                if (playingVideoIndex >= 0) {
                    // get the intent that SearchActivity was started with
                    Intent intent = getIntent();
                    // this line and the next are ensuring that we were called by ClientMainActivity
                    // by checking the contents of the intent
                    // NOTE: may be removed if we don't care?
                    String msg = intent.getStringExtra("song");
                    if (msg.contentEquals("name")) {
                        // put the song ID and song title into the intent
                        intent.putExtra("Song ID", searchResults.get(playingVideoIndex).getId().getVideoId());
                        intent.putExtra("Song Title", resultTitles.get(playingVideoIndex));
                        // set the result to be returned, use RESULT_OK to ensure that everything went as planned and return
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    // inform user they must search for a video first
                    Toast.makeText(getApplicationContext(), "Search for a video first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // only initialized the player if one was successfully retrieved
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            this.player = youTubePlayer;
        }
    }

    // handle error if player doesn't not initialize successfully
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = "onInitializationFailure of YouTubeFragment";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    // overriding onActivityResult and not changing it...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}

