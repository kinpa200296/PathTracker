package com.pathtracker.android.bluetoothservice;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Binder;


public class BluetoothBinder extends Binder {
    private BluetoothSocket btSocket;
    private Context context;

    public BluetoothBinder(Context context){
        this.context = context;
    }

    public void setBtSocket(BluetoothSocket socket){
        btSocket = socket;
    }


}
