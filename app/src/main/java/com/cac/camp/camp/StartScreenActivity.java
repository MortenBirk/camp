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


public class StartScreenActivity extends Activity {
    private ServerCommunicator sc;
    private BluetoothHandler blHandler = null;

    Player mPlayer;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {

            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());
            SpotifySingleton.getInstance().setSpotify(spotify);
            Button button = (Button) findViewById(R.id.gotoPlaylistBtn);
            button.setEnabled(true);



        }
    }

    public void gotoPlaylist(View view) {
        startActivity(new Intent(this, PlaylistPlayerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String CLIENT_ID = getString(R.string.CLIENT_ID);
        String REDIRECT_URI = getString(R.string.REDIRECT_URI);
        sc = new ServerCommunicator(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartViewFragment())
                    .commit();
        }
        //SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
        //        new String[]{"user-read-private", "streaming"}, null, this);

        //Start the bluetooth
        blHandler = new BluetoothHandler(this);
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

    //Create playlist
    public void createPlaylist(View view) {

        ArrayList<String> users = new ArrayList();
        users.add("User4");

        sc.createPlaylist(users, "normalParty");
    }

    //Update playlist
    public void updatePlaylist(View view) {
        ArrayList<String> users = new ArrayList();
        users.add("User4");

        sc.updatePlaylist(users, "normalParty", "5482dcf2c9284");
    }

    //get playlist
    public void getPlaylist(View view) {
        sc.getPlaylist("5482dcf2c9284");
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


}
