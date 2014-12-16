package com.cac.camp.camp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Birk on 15-12-2014.
 */
public class LocationHandler implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private Activity activity = null;
    private LocationClient mLocationClient = null;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = 3000;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = 1000;
    private LocationRequest mLocationRequest;

    private double lat = 0;
    private double lon = 0;
    private Location location = null;

    public LocationHandler(Activity activity) {
        this.activity = activity;
        mLocationClient = new LocationClient(activity, this, this);
        mLocationClient.connect();
        createLocationRequest();
    }

    public void onPause() {
        if (mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
    }

    public void onResume() {
        mLocationClient.connect();
    }

    private void createLocationRequest() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Set the update interval
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLocationClient.isConnected()) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            Toast.makeText(activity, "Connected Loc Client", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(activity, "Disconnected Loc Client",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(activity, "Failed conn to Loc Cli",
                Toast.LENGTH_SHORT).show();
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}
