package com.pathtracker.android.bluetoothservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service {

    public static final String LOG_TAG = "BluetoothService";

    private BluetoothBinder _binder;
    private SocketListener _socketListener;
    private Thread _listenerThread;

    public BluetoothService() {
        _binder = null;
        _socketListener = null;
        _listenerThread = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (_binder == null)
            _binder = new BluetoothBinder(this, _socketListener);
        return _binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (_socketListener == null){
            _socketListener = new SocketListener();
        }

        if (_listenerThread == null){
            _listenerThread = new Thread(_socketListener);
        }
        _listenerThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _socketListener.stop = true;
        if (_listenerThread.isAlive()){
            _listenerThread.interrupt();
        }
    }
}
