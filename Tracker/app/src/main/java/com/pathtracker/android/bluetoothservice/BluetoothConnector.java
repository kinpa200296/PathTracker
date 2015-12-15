package com.pathtracker.android.bluetoothservice;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnector {

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private String _mac;
    BluetoothConnectorListener mListener;

    public static final String CONNECT_DEVICE = "com.pathtracker.android.pathtracker.connect";
    public static final String DISCONNECT_DEVICE = "com.pathtracker.android.pathtracker.disconnect";

    public final String BLUETOOTH = "Bluetooth";

    public BluetoothConnector(String mac_address, Context context){
        if (context instanceof BluetoothConnectorListener) {
            mListener = (BluetoothConnectorListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        _mac = mac_address;
        BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice btDevice = btAdapter.getRemoteDevice(_mac);
                if (btDevice == null) {
                    Log.d(BLUETOOTH, "Device not found");
                    Toast.makeText(context, "Device not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    btSocket.connect();
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                    mListener.onConnect(btSocket);
                } catch (IOException e) {
                    Log.d(BLUETOOTH, e.getMessage());
                    Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d(BLUETOOTH, e.getClass().getName() + " " + e.getMessage());
                    Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter filter = new IntentFilter(CONNECT_DEVICE);
        context.registerReceiver(br, filter);


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    btSocket.close();
                    btSocket = null;
                    mListener.onDisconnect();
                } catch (IOException e) {
                    Log.d(BLUETOOTH, e.getMessage());
                } catch (Exception e) {
                    Log.d(BLUETOOTH, e.getClass().getName() + " " + e.getMessage());
                }
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        filter = new IntentFilter(DISCONNECT_DEVICE);
        context.registerReceiver(br, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(br, filter);


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                //int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
                if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF )
                    mListener.onStateChange();
            }
        };

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(br, filter);
    }

    public String getMacAddress(){
        return _mac;
    }

    public interface BluetoothConnectorListener{
        void onConnect(BluetoothSocket socket);
        void onDisconnect();
        void onStateChange();
    }
}
