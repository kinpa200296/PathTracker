package com.pathtracker.android.bluetoothservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements BluetoothConnector.BluetoothConnectorListener{

    BluetoothBinder binder;
    BluetoothServiceConnection btConnection;
    BluetoothConnector connector;
    Intent intent;
    Switch btSwitch, arSwitch;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = manager.getAdapter();
        connector = new BluetoothConnector("20:15:05:22:03:44", this);

        btSwitch = (Switch) findViewById(R.id.btSwitch);
        arSwitch = (Switch) findViewById(R.id.btnConnect);
        btSwitch.setChecked(false);
        arSwitch.setChecked(false);

        btSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.isEnabled()){
                    btSwitch.setChecked(false);
                    adapter.enable();
                }
                else
                    btSwitch.setChecked(true);
                    adapter.disable();
            }
        });
        arSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!arSwitch.isEnabled()) return;
                if (!connected) {
                    sendBroadcast(new Intent(BluetoothConnector.CONNECT_DEVICE));
                    arSwitch.setChecked(false);
                }
                else {
                    sendBroadcast(new Intent(BluetoothConnector.DISCONNECT_DEVICE));
                    arSwitch.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onConnect(BluetoothSocket socket) {
        arSwitch.setChecked(true);
        connected = true;
    }

    @Override
    public void onDisconnect() {
        arSwitch.setChecked(false);
        connected = false;
    }

    @Override
    public void onStateChange() {
        if (btSwitch.isChecked()){
            if (arSwitch.isChecked())
                sendBroadcast(new Intent(BluetoothConnector.DISCONNECT_DEVICE));
            if (arSwitch.isEnabled())
                arSwitch.setEnabled(false);
            btSwitch.setChecked(false);
        }
        else {
            arSwitch.setEnabled(true);
            btSwitch.setChecked(true);
        }
    }

    public class BluetoothServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (BluetoothBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
