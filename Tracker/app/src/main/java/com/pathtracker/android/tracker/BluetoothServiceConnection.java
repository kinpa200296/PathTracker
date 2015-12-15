package com.pathtracker.android.tracker;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetoothservice.BluetoothBinder;

public class BluetoothServiceConnection implements ServiceConnection {

    private PathTracker _tracker;

    BluetoothBinder binder;
    boolean connected;

    public BluetoothServiceConnection(PathTracker tracker){
        binder = null;
        connected = false;
        _tracker = tracker;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (BluetoothBinder)service;
        connected = true;
        binder.setTracker(_tracker);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder = null;
        connected = false;
    }
}
