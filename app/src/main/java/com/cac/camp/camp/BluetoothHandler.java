package com.cac.camp.camp;

import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Birk on 08-12-2014.
 */
public class BluetoothHandler {
    private BluetoothAdapter mBluetoothAdapter = null;
    private IntentFilter filter = null;
    private Intent discoverableIntent = null;
    private ArrayList<BluetoothDevice> deviceList = null; //Maybe we would like to connect to devices after discovery?
    private UUID uuid = UUID.fromString("baeaaee0-8087-11e4-b116-123b93f75cba");
    private String name = "CAMP";
    private BluetoothServer blServer = null;
    private Context context = null;
    private StartScreenActivity activity = null;

    //Used whenever a bluetooth device is found
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent.
                Log.d("Bluetooth Handler", "Found a device");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothSocket socket = null;
                String name = device.getName();
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    Toast.makeText(context, "Connected to device " + name, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d("Bluetooth Handler", "Failed to create insecure connection");
                    return;
                }
                //If we get here we should have a connection
                handleConnection(socket);
            }
        }
    };


    public BluetoothHandler(StartScreenActivity activity, Context context) {
        this.activity = activity;
        this.context = context;
        deviceList = new ArrayList<BluetoothDevice>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        //Enable to be discovered forever
        discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        activity.startActivity(discoverableIntent);
        blServer = new BluetoothServer(uuid, name, mBluetoothAdapter, context, activity);
        blServer.start();
    }

    public void handleConnection(BluetoothSocket socket) {

        Log.d("HandleConnection", "inside handler");
        Toast.makeText(context, "Client handled request", Toast.LENGTH_SHORT).show();
        /*
        InputStream inStream = null;
        try {
            inStream = socket.getInputStream();
        } catch (IOException e) {
            Toast.makeText(context, "Could not create input stream", Toast.LENGTH_SHORT).show();
            return;
        }
        while(true) {
            try {
                // Read from the InputStream
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(inStream));
                String msg = socketReader.readLine();
                Toast.makeText(context, "Received msg: " + msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                break;
            }
        }
        */

    }

    public void discoverDevices() {
        //The allready found devices. This is not required to call
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        //The discovery process usually involves an inquiry scan of about 12 seconds, followed by a page scan of each found device to retrieve its Bluetooth name.
        mBluetoothAdapter.startDiscovery();

    }

    public void destroy() {
        blServer.cancel();
        blServer.interrupt();
        blServer = null;

    }


}
