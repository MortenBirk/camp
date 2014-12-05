package com.cac.camp.camp;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.Player;

/**
 * Created by nikolaiollegaard on 03/12/14.
 */
public class SpotifySingleton {
    private static SpotifySingleton ourInstance = new SpotifySingleton();

    public static SpotifySingleton getInstance() {
        return ourInstance;
    }
    //private Player mPlayer;
    private Spotify spotify;

    private SpotifySingleton() {
    }

    public Spotify getSpotify() {
        return spotify;
    }

    public void setSpotify(Spotify spotify) {
        this.spotify = spotify;
    }
}
