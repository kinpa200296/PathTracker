package com.pathtracker.android.bluetoothservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SwitchBluetoothReceiver extends BroadcastReceiver {

    private BluetoothConnector _connector;

    public SwitchBluetoothReceiver(BluetoothConnector connector) {
        _connector = connector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        _connector.switchBluetooth();
    }
}
