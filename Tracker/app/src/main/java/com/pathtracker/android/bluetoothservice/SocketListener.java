package com.pathtracker.android.bluetoothservice;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.pathtracker.android.bluetooth.PathTracker;

import java.io.IOException;
import java.io.InputStream;

public class SocketListener implements Runnable {

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
                if (btSocket.isConnected()) {
                    try {
                        InputStream stream = btSocket.getInputStream();
                        int b = stream.read();
                        while (b != -1) {
                            tracker.analyze((byte) b);
                            b = stream.read();
                        }
                    } catch (IOException e) {
                        Log.w(BluetoothService.LOG_TAG, e.getMessage());
                    }
                }
            }
        }
    }
}
