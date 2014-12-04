package com.cac.camp.camp;

import com.spotify.sdk.android.playback.Player;

/**
 * Created by nikolaiollegaard on 03/12/14.
 */
public class SpotifySingleton {
    private static SpotifySingleton ourInstance = new SpotifySingleton();

    public static SpotifySingleton getInstance() {
        return ourInstance;
    }
    private Player mPlayer;

    private SpotifySingleton() {
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player mPlayer) {
        this.mPlayer = mPlayer;
    }
}
