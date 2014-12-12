package com.cac.camp.camp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Birk on 10-12-2014.
 */
public class BluetoothServer extends Thread {

    private final BluetoothServerSocket mmServerSocket;
    private Context context = null;
    private StartScreenActivity activity = null;

    public BluetoothServer(UUID uid, String name, BluetoothAdapter mBluetoothAdapter, Context context, StartScreenActivity activity) {
        this.context = context;
        this.activity = activity;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, uid);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                if(mmServerSocket != null) {
                    socket = mmServerSocket.accept();
                }
            } catch (IOException e) {

            }
            // If a connection was accepted
            if (socket != null) {
                // We might like to manage the connection (in a separate thread)
                handleConnection(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleConnection(BluetoothSocket socket) {
        Toast.makeText(context, "Server handled request", Toast.LENGTH_SHORT).show();
        OutputStream outStream = null;
        try {
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(context, "Could not create output stream", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            outStream.write("test".getBytes());
            outStream.write("close".getBytes());
        } catch (IOException e) {
            Toast.makeText(context, "Could not send message", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            Toast.makeText(context, "Could not close socket", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}
