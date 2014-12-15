package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
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



public class StartScreenActivity extends Activity implements ClientActivity, SensorEventListener {
    private ServerCommunicator sc;
    private BluetoothHandler blHandler = null;
    private LocationHandler locationhandler = null;

    Player mPlayer;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<DataPoint> dataPoints;
    private List<DataWindow> dataWindows;

    private Boolean isClassifying = false;

    private AssetManager assetMgr;
    private boolean isSlave;


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


    public void sendAccUpdate() {

        // - stop the logging
        List<DataWindow> dataWindowsCopy = new CopyOnWriteArrayList<DataWindow>(dataWindows);
        String classification = WekaDataGenerator.classify(dataWindowsCopy, assetMgr);
        Log.d("class", classification);

        // TODO - send to master unit over bluetooth


    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        locationhandler.onPause();

        super.onPause();
    }

    @Override
    public void onResume() {
        locationhandler.onResume();
        super.onResume();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Right in here is where you put code to read the current sensor values and
        //update any views you might have that are displaying the sensor information
        //You'd get accelerometer values like this:
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        DataPoint dp = new DataPoint(x, y, z);
        dataPoints.add(dp);

        if (dataPoints.size() % 64 == 0 && dataPoints.size() >= 128) {
            Log.d("3", "Start: Add window");
            int counter = dataWindows.size();
            List<DataPoint> windowedDataPoints = dataPoints.subList(counter * 64, counter * 64 + 127);
            dataWindows.add(new DataWindow(windowedDataPoints));
        }

        if (dataWindows.size() == 10) {
            sendAccUpdate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String CLIENT_ID = getString(R.string.CLIENT_ID);
        String REDIRECT_URI = getString(R.string.REDIRECT_URI);
        locationhandler = new LocationHandler(this);
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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        dataPoints = new ArrayList<DataPoint>();
        dataWindows = new ArrayList<DataWindow>();

        assetMgr = this.getAssets();

        isSlave = true;
        if (isSlave) {
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void runLogAcc(View view) {
        Intent intent = new Intent(this, LogAccActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        //blHandler.destroy();
        mSensorManager.unregisterListener(this);
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
