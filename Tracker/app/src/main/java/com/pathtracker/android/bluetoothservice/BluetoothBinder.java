package com.pathtracker.android.bluetoothservice;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Binder;

import com.pathtracker.android.bluetooth.PathTracker;


public class BluetoothBinder extends Binder {
    private Context _context;
    private SocketListener _listener;

    public BluetoothBinder(Context context, SocketListener listener){
        _context = context;
        _listener = listener;
    }

    public void setBtSocket(BluetoothSocket socket){
        if (_listener != null){
            _listener.btSocket = socket;
        }
    }

    public void setTracker(PathTracker tracker){
        if (_listener != null){
            _listener.tracker = tracker;
        }
    }
}
