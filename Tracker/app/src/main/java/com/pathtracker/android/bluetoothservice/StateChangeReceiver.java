package com.pathtracker.android.bluetoothservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StateChangeReceiver extends BroadcastReceiver {

    private BluetoothConnector _connector;

    public StateChangeReceiver(BluetoothConnector connector) {
        _connector = connector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        _connector.changeState(intent);
    }
}
