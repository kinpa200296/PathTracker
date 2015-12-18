package com.pathtracker.android.bluetoothservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LowLevelDisconnectReceiver extends BroadcastReceiver{

    private BluetoothConnector _connector;

    public LowLevelDisconnectReceiver(BluetoothConnector connector){
        _connector = connector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        _connector.onLowLevelDisconnect();
    }
}
