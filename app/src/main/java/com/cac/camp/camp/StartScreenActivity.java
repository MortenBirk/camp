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

import com.google.android.gms.location.LocationListener;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class StartScreenActivity extends Activity implements ClientActivity, LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, SensorEventListener {
    private ServerCommunicator sc;
    private BluetoothHandler blHandler = null;

    Player mPlayer;


    private LocationClient mLocationClient;

    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = 3000;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = 1000;
    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;


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
        if (mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        mLocationClient.connect();
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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        dataPoints = new ArrayList<DataPoint>();
        dataWindows = new ArrayList<DataWindow>();

        assetMgr = this.getAssets();

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        createLocationRequest();


        isSlave = true;
        if (isSlave) {
            // - start the logging again
            mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    private void createLocationRequest() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Set the update interval
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        //Start the bluetooth
        blHandler = new BluetoothHandler(this, getApplicationContext());
    }

    public void runLogAcc(View view) {
        Intent intent = new Intent(this, LogAccActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        blHandler.destroy();
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    public void searchBluetooth(View view) {
        blHandler.discoverDevices();
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
    public void onConnected(Bundle dataBundle) {
        // Display the connection status


        if (mLocationClient.isConnected()) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            Toast.makeText(this, "Connected Loc Client", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected Loc Client",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Display the connection status
        Toast.makeText(this, "Failed conn to Loc Cli",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


        //LatLng currentP = new LatLng(location.getLatitude(), location.getLongitude());


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
