package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;

import java.util.ArrayList;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;


public class StartScreenActivity extends Activity implements ConnectionStateCallback, PlayerNotificationCallback {
    private String CLIENT_ID;
    private String REDIRECT_URI;
    private ServerCommunicator sc;

    Player mPlayer;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());
            mPlayer = spotify.getPlayer(this, "CAMP", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(StartScreenActivity.this);
                    mPlayer.addPlayerNotificationCallback(StartScreenActivity.this);
                    //mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    Button button = (Button) findViewById(R.id.gotoPlaylistBtn);
                    button.setEnabled(true);

                    SpotifySingleton.getInstance().setPlayer(mPlayer);

                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("SpotifyIntentService", "Could not initialize player: " + throwable.getMessage());
                    //debugLog("accessToken " + accessToken);
                }
            });


        }
    }

    public void gotoPlaylist(View view) {
        startActivity(new Intent(this, PlaylistPlayerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CLIENT_ID = getString(R.string.CLIENT_ID);
        REDIRECT_URI = getString(R.string.REDIRECT_URI);
        sc = new ServerCommunicator(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartViewFragment())
                    .commit();
        }
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                new String[]{"user-read-private", "streaming"}, null, this);
    }

    public void runLogAcc(View view) {
        Intent intent = new Intent(this, LogAccActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    //Create a fixed user, no feedback is given
    public void createUser(View view) {
        ArrayList<String> chill = new ArrayList();
        chill.add("God Sang");

        ArrayList<String> calm = new ArrayList();
        calm.add("rolig Sang");

        ArrayList<String> normal = new ArrayList();
        normal.add("normal Sang");

        ArrayList<String> wild = new ArrayList();
        wild.add("Vild Sang");

        sc.createUser("User4", chill, calm, normal, wild);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StartViewFragment extends Fragment {

        public StartViewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
            return rootView;
        }
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

    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {
        Log.d("SpotifyIntentService", "Playback error received: " + errorType.name());

    }
}
