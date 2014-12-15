package com.cac.camp.camp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PlaylistPlayerActivity extends Activity implements ClientActivity, ConnectionStateCallback, PlayerNotificationCallback {
    Player mPlayer;
    List<String> staticPlaylist = new ArrayList<String>();
    List<Track> playlist = new ArrayList<Track>();
    ListView playlistView;
    Boolean isPlaying = false;
    Button playbutton;
    Track currentTrack;

    private ServerCommunicator sc;

    private String currentPlaylistId;
    private List<String> currentUris;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_player);
        playbutton = (Button) findViewById(R.id.playbutton);

        playlistView = (ListView) findViewById(R.id.playlistView);

        staticPlaylist.add("5QruhkvTtEi6cR8nSr6LwT");
        staticPlaylist.add("4KqQfcx8RM9liPiHEvEJNg");
        staticPlaylist.add("768j11Y0ksbgdncnFuAEZ4");
        staticPlaylist.add("3DqhtDaWayCtfV69d2p5vH");

        SpotifyAPIConnector api = new SpotifyAPIConnector(this);

        for ( String trackURI : staticPlaylist ) {
            api.getTrackFromID(trackURI, this);

        }

        ArrayAdapter<Track> adapter = new ArrayAdapter<Track>(
                getBaseContext(),
                android.R.layout.simple_list_item_1,
                playlist
        );

        playlistView.setAdapter(adapter);

        Spotify spotify = SpotifySingleton.getInstance().getSpotify();

        mPlayer = spotify.getPlayer(this, "CAMP", this, new Player.InitializationObserver() {
            @Override
            public void onInitialized() {
                mPlayer.addConnectionStateCallback(PlaylistPlayerActivity.this);
                mPlayer.addPlayerNotificationCallback(PlaylistPlayerActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("SpotifyIntentService", "Could not initialize player: " + throwable.getMessage());
            }
        });
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button playbutton = (Button) findViewById(R.id.playbutton);
        Button nextbutton = (Button) findViewById(R.id.nextbutton);
        Button previousbutton = (Button) findViewById(R.id.previousbutton);
        playbutton.setTypeface(font);
        nextbutton.setTypeface(font);
        previousbutton.setTypeface(font);


        // TODO - maybe shouldn't create playlist every time...
        createPlaylist();

    }

    //Create playlist
    public void createPlaylist() {

        ArrayList<String> users = new ArrayList();
        users.add("Emil");

        sc.createPlaylist(users, "calmParty", this);
    }

    public void setCurrentPlaylist(String playlistId, List<String> uris) {
        this.currentPlaylistId = playlistId;
        this.currentUris = uris;
    }

    //Update playlist
    public void updatePlaylist() {
        ArrayList<String> users = new ArrayList();
        users.add("Morten");

        // TODO - should come from external users over bluetooth
        List<String> classifications = new ArrayList<String>();
        classifications.add("normalParty");

        String classification = WekaDataGenerator.getMaxClass(classifications);

        sc.updatePlaylist(users, classification, currentPlaylistId, this);

    }

    //get playlist
    public void getPlaylist(View view) {
        sc.getPlaylist(currentPlaylistId, this);
    }

    public void play(View view){
        if (isPlaying) {
            mPlayer.pause();
            playbutton.setText(R.string.fa_pause);

        } else {
            mPlayer.resume();
            playbutton.setText(R.string.fa_play);
        }
        isPlaying = !isPlaying;
    }

    public void next(View view){
        mPlayer.skipToNext();
    }

    public void previous(View view) {
        mPlayer.skipToPrevious();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleTrackResponse(Track t) {
        playlist.add(t);
        if (currentTrack == null) {
            currentTrack = t;
            Log.d("currentTrack", t.toString());
        }
        mPlayer.queue(t.toURI());
    }

    /*
     * Spotify interface methods goes below
     */

    @Override
    public void onLoggedIn() {
        Log.d("StartScrSpotifyIntentServiceeenActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifyIntentService", "User logged out");

    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("SpotifyIntentService", "Login failed");

    }

    @Override
    public void onTemporaryError() {
        Log.d("SpotifyIntentService", "Temporary error occurred");

    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("SpotifyIntentService", "User credentials blob received");

    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SpotifyIntentService", "Received connection message: " + message);

    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.d("SpotifyIntentService", "Playback event received: " + eventType.name());
        if ( eventType.name().equals("TRACK_END") ) {
            int newIndex = playlist.indexOf(currentTrack)+1;
            if (newIndex >= playlist.size()) {
                currentTrack = playlist.get(newIndex);
            } else {
                currentTrack = playlist.get(0);
            }
            Log.d("new currentTrack",currentTrack.toString());
        } else if (eventType.name().equals("PAUSE")) {
            isPlaying = false;
        } else if (eventType.name().equals("PLAY")) {
            isPlaying = true;
        }

    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {
        Log.d("SpotifyIntentService", "Playback error received: " + errorType.name());

    }


    @Override
    public void setCurrentPlaylist(String id, ArrayList<String> playlist) {

    }

    @Override
    public void deriveCommonContext(ArrayList<String> users, ArrayList<String> contexts) {

    }
}
