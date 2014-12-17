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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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

    //This is our playlist
    private ArrayList<String> playlist;
    private String playlistID = "";
    private ArrayList<Position> locations;

    private Boolean isClassifying = false;

    private String USERID = "Birk";
    private String currentContext = "";

    private AssetManager assetMgr;

    private SendUpdated sendUpdated = null;
    private RequestUpdates requestUpdates = null;

    private TextView showContext = null;

    private final String default_song = "2b712q3E27nyW6LGsZxr0y";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locations = new ArrayList<Position>();
        locations.add(new Position(56.171922, 10.188691)); //Babbage
        locations.add(new Position(56.1723, 10.188276));   //Fredagsbar.dk (IMV)
        locations.add(new Position(56.159984,10.198749)); //Sehrøgade
        locationhandler = new LocationHandler(this);
        sensorHandler = new SensorHandler(this);
        sc = new ServerCommunicator(this);
        playlist = new ArrayList<String>();
        sendUpdated = new SendUpdated(this);
        sendUpdated.start();
        requestUpdates = new RequestUpdates(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartViewFragment())
                    .commit();
        }
        showContext = (TextView)findViewById(R.id.showContext);

        SpotifySingleton.getInstance().setPlayerActivity(this);

        //Gets user from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("USER_ID");
            this.USERID = value;
            Log.d("INTENT", this.USERID);
        }

        assetMgr = this.getAssets();
        //new UpdateView().execute(this);


        FileLogger.initFile(this, "wekaoutput.log");
    }

    public void gotoPlaylist(View view) {
        //startActivity(new Intent(this, PlaylistPlayerActivity.class));
        SpotifySingleton.getInstance().play("0aO9fmrQXxZi2ZOwtdtggZ");
    }


    public String getNext() {
        return this.getPrev();
    }

    public String getPrev() {
        Random random = new Random();
        if (playlist.size() == 0) {
            return default_song;
        }
        return playlist.get(random.nextInt(playlist.size()));
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
        sensorHandler.onResume();
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
        sendUpdated.interrupt();
        sendUpdated = null;
        sensorHandler.onDestroy();
        super.onDestroy();
    }


    //This method sends a message to the server, about our context and position.
    public void updateContextAndPosition(View view) {
        //Get position
        String lat = Double.toString(locationhandler.getLat());
        String lon = Double.toString(locationhandler.getLon());
        //Get context
        List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(sensorHandler.getDataWindows());
        String classification = WekaDataGenerator.classify(dataWindowsCopy, assetMgr, this);
        String confidence = "1"; //Currently this is faked
        Log.d("class", classification);
        sc.updateContextAndPosition(USERID, lat, lon, classification, confidence, this);
    }

    public void updateContextAndPosition() {
        //Get position
        Log.d("Log", "Updated context");
        String lat = Double.toString(locationhandler.getLat());
        String lon = Double.toString(locationhandler.getLon());
        //Get context
        List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(sensorHandler.getDataWindows());
        String classification = WekaDataGenerator.classify(dataWindowsCopy, assetMgr, this);
        String confidence = "1"; //Currently this is faked
        Log.d("class", classification);
        sc.updateContextAndPosition(USERID, lat, lon, classification, confidence, this);
    }

    public void updateView() {
        showContext = (TextView)findViewById(R.id.showContext);
        if(showContext == null) {
            Log.d("View update", "it was null");
            return;
        }
        showContext.setText(currentContext);
        Log.d("View update", "called");
    }



    public void startRequestUpdates() {
        if (requestUpdates.isAlive()) {
            Log.d("Log", "Thread is alive already");
            return;
        }
        requestUpdates.start();
        Log.d("Log", "Started Thread");
    }

    public void getContexts() {
        //Get position
        String lat = Double.toString(locationhandler.getLat());
        String lon = Double.toString(locationhandler.getLon());
        boolean atParty = false;
        for(Position p : locations) {
            float[] result = new float[100];
            Location.distanceBetween(locationhandler.getLat(), locationhandler.getLon(), p.getLattitude(), p.getLongitude(), result);
            if(result[0] < 15) {
                atParty = true;
            }
        }
        if(atParty) {
            sc.getContexts(lat, lon, this);
        } else {
            createChillPlaylist();
        }
    }

    @Override
    public void setCurrentPlaylist(String id, ArrayList<String> playlist) {
        playlistID = id;
        this.playlist = playlist;
        Log.d("done", "playlist created");
    }

    //If you are alone, create the chill playlist
    private void createChillPlaylist() {
        ArrayList<String> user = new ArrayList<String>();
        user.add(USERID);
        currentContext = "chill";
        if (playlistID.equals("")) {
            sc.createPlaylist(user, "chill", this);
        } else {
            sc.updatePlaylist(user, "chill", playlistID, this);
        }
    }

    @Override
    public void deriveCommonContext(ArrayList<String> users, ArrayList<String> contexts) {
        //If it is only you there, we have a chill playlist for you
        if(users.size() <= 1) {
            createChillPlaylist();
        }

        //If we have a party for many people instead
        if(contexts.isEmpty() || users.isEmpty()) {
            Log.d("WARNING", "Empty context or no users.");
            return;
        }
        String context = WekaDataGenerator.getMaxClass(contexts);
        currentContext = context;
        if (playlistID.equals("")) {
            sc.createPlaylist(users, context, this);
        } else {
            sc.updatePlaylist(users, context, playlistID, this);
        }
    }

    public void play(View view) {
        startRequestUpdates();
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
