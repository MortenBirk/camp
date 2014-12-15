package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;



public class StartScreenActivity extends Activity implements ClientActivity {
    private ServerCommunicator sc;
    private BluetoothHandler blHandler = null;
    private LocationHandler locationhandler = null;
    private SensorHandler sensorHandler = null;

    Player mPlayer;

    private Boolean isClassifying = false;

    private AssetManager assetMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String CLIENT_ID = getString(R.string.CLIENT_ID);
        String REDIRECT_URI = getString(R.string.REDIRECT_URI);
        locationhandler = new LocationHandler(this);
        sensorHandler = new SensorHandler(this);
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

        /*
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button playbutton = (Button) findViewById(R.id.playbutton);
        Button nextbutton = (Button) findViewById(R.id.nextbutton);
        Button previousbutton = (Button) findViewById(R.id.previousbutton);
        playbutton.setTypeface(font);
        nextbutton.setTypeface(font);
        previousbutton.setTypeface(font);
        */

        assetMgr = this.getAssets();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());
            SpotifySingleton.getInstance().init(this, spotify);

            Button button = (Button) findViewById(R.id.gotoPlaylistBtn);
            button.setEnabled(true);

        }
    }

    public void gotoPlaylist(View view) {
        //startActivity(new Intent(this, PlaylistPlayerActivity.class));
        SpotifySingleton.getInstance().play("0aO9fmrQXxZi2ZOwtdtggZ");
    }


    public void sendAccUpdate() {

        // - stop the logging
        List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(sensorHandler.getDataWindows());
        String classification = WekaDataGenerator.classify(dataWindowsCopy, assetMgr);
        Log.d("class", classification);
        // TODO - send to master unit over bluetooth

    }

    public String getNext() {
        return "2Lsj1mNTA4032NIu0xZzdZ";
    }

    public String getPrev() {
        return "2EuMREhmnIPQFaqXUH5yPu";
    }

    @Override
    public void onPause() {

        locationhandler.onPause();
        sensorHandler.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        locationhandler.onResume();
        super.onResume();
    }

    public void runLogAcc(View view) {
        Intent intent = new Intent(this, LogAccActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        //blHandler.destroy();
        sensorHandler.onDestroy();
        super.onDestroy();
    }


    public void updateContextAndPosition(View view) {
        sc.updateContextAndPosition("morten");
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

        sc.createUser("User4", chill, calm, normal, wild, this);
    }

    @Override
    public void setCurrentPlaylist(String id, List<String> playlist) {
        // not needed here
    }

    public void play(View view) {
        SpotifySingleton spotify = SpotifySingleton.getInstance();
        if (spotify.isPlaying()) {
            spotify.stop();
        } else {
            spotify.play(this.getNext());
        }

    }

    public void next(View view) {
        SpotifySingleton.getInstance().play(this.getNext());
    }

    public void previous(View view) {
        SpotifySingleton.getInstance().play(this.getPrev());
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

    public void nowPlayingHandler(Track t) {
        TextView song = (TextView) findViewById(R.id.songview);
        TextView artist = (TextView) findViewById(R.id.artistview);

        song.setText(t.getSong());
        artist.setText(t.getArtist());

    }

}
