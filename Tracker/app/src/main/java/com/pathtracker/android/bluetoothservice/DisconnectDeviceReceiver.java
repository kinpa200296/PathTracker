package com.pathtracker.android.bluetoothservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DisconnectDeviceReceiver extends BroadcastReceiver {

    private BluetoothConnector _connector;

    public DisconnectDeviceReceiver(BluetoothConnector connector) {
        _connector = connector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        _connector.disconnectDevice();
        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
    }
}
