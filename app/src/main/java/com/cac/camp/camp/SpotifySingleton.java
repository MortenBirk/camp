package com.cac.camp.camp;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import java.util.List;

/**
 * Created by nikolaiollegaard on 03/12/14.
 */
public class SpotifySingleton implements ClientActivity, ConnectionStateCallback, PlayerNotificationCallback {
    private static SpotifySingleton ourInstance = new SpotifySingleton();

    public static SpotifySingleton getInstance() {
        return ourInstance;
    }
    private Player mPlayer;
    private Spotify spotify;
    private Boolean isPlaying = false;
    private Track currentTrack = null;
    private StartScreenActivity parent = null;
    private SpotifyAPIConnector api = null;


    public Boolean isPlaying() {
        return this.isPlaying;
    }

    public void init(StartScreenActivity parent, Spotify spotify) {
        this.spotify = spotify;
        this.parent = parent;
        Context context = parent.getBaseContext();
        this.api = new SpotifyAPIConnector(context);
        mPlayer = spotify.getPlayer(context, "CAMP", this, new Player.InitializationObserver() {
            @Override
            public void onInitialized() {
                mPlayer.addConnectionStateCallback(SpotifySingleton.this);
                mPlayer.addPlayerNotificationCallback(SpotifySingleton.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("SpotifyIntentService", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void play(String uri) {
        String apiURI = "spotify:track:"+uri;
        mPlayer.play(apiURI);
        if (api != null) {
            Log.d("SpotifySingleton", "api != null");
            api.getTrackFromID(uri);
        }

    }

    public void stop() {
        mPlayer.pause();
    }


    public void handleTrackResponse(Track t) {
        currentTrack = t;
        parent.nowPlayingHandler(t);
    }

    @Override
    public void setCurrentPlaylist(String id, List<String> playlist) {

    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onNewCredentials(String s) {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        if ( eventType.name().equals("TRACK_END") ) {
                //this.play(parent.getNext());
        } else if (eventType.name().equals("PAUSE")) {
            isPlaying = false;
        } else if (eventType.name().equals("PLAY")) {
            isPlaying = true;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }
}
