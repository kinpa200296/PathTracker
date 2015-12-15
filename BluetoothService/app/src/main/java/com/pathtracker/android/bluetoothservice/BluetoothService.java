package com.pathtracker.android.bluetoothservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service {

    private BluetoothBinder binder;

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null)
            binder = new BluetoothBinder(this);
        return binder;
    }
}
