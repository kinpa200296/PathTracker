package com.pathtracker.android.bluetoothservice;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.pathtracker.android.bluetooth.PathTracker;

import java.io.IOException;
import java.io.InputStream;

public class SocketListener implements Runnable {

    public static final String LOG_TAG = "SocketListener";

    BluetoothSocket btSocket;
    PathTracker tracker;
    boolean stop;

    public SocketListener() {
        stop = false;
    }

    @Override
    public void run() {
        while (!stop) {
            if (btSocket != null && tracker != null) {
                try {
                    InputStream stream = btSocket.getInputStream();
                    int b = stream.read();
                    while (b != -1) {
                        tracker.analyze((byte) b);
                        b = stream.read();
                    }
                } catch (IOException e) {
                    Log.w(LOG_TAG, e.getMessage());
                }
            }
        }
    }
}
