package com.pathtracker.android.bluetoothservice;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnector {

    private BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    private String _mac;
    private ConnectDeviceReceiver _connectDeviceReceiver;
    private DisconnectDeviceReceiver _disconnectDeviceReceiver;
    private StateChangeReceiver _stateChangeReceiver;
    private SwitchBluetoothReceiver _switchBluetoothReceiver;
    private LowLevelDisconnectReceiver _lowLevelDisconnectReceiver;
    private Context _context;

    BluetoothConnectorListener mListener;

    public boolean manualDisconnect = false;

    public static final String CONNECT_DEVICE = "com.pathtracker.android.pathtracker.action.connectDevice";
    public static final String DISCONNECT_DEVICE = "com.pathtracker.android.pathtracker.action.disconnectDevice";
    public static final String SWITCH_BLUETOOTH = "com.pathtracker.android.pathtracker.action.switchBluetooth";

    public static final String BLUETOOTH = "Bluetooth";

    public BluetoothConnector(String mac_address, Context context) {
        if (context instanceof BluetoothConnectorListener) {
            mListener = (BluetoothConnectorListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        _mac = mac_address;
        _context = context;
        final BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
    }

    public String getMacAddress() {
        return _mac;
    }

    public int getBtState() {
        return btAdapter.getState();
    }

    public boolean isDeviceConnected() {
        return btSocket != null;
    }

    public void registerReceivers() {
        _connectDeviceReceiver = new ConnectDeviceReceiver(this);
        IntentFilter filter = new IntentFilter(CONNECT_DEVICE);
        _context.registerReceiver(_connectDeviceReceiver, filter);

        _disconnectDeviceReceiver = new DisconnectDeviceReceiver(this);
        filter = new IntentFilter(DISCONNECT_DEVICE);
        _context.registerReceiver(_disconnectDeviceReceiver, filter);

        _stateChangeReceiver = new StateChangeReceiver(this);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        _context.registerReceiver(_stateChangeReceiver, filter);

        _switchBluetoothReceiver = new SwitchBluetoothReceiver(this);
        filter = new IntentFilter(SWITCH_BLUETOOTH);
        _context.registerReceiver(_switchBluetoothReceiver, filter);

        _lowLevelDisconnectReceiver = new LowLevelDisconnectReceiver(this);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        _context.registerReceiver(_lowLevelDisconnectReceiver, filter);
    }

    public void unregisterReceivers() {
        _context.unregisterReceiver(_connectDeviceReceiver);
        _context.unregisterReceiver(_disconnectDeviceReceiver);
        _context.unregisterReceiver(_stateChangeReceiver);
        _context.unregisterReceiver(_switchBluetoothReceiver);
        _context.unregisterReceiver(_lowLevelDisconnectReceiver);
    }

    void connectDevice(Context context) {
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

    void disconnectDevice() {
        try {
            BluetoothSocket oldSocket = btSocket;
            btSocket = null;
            mListener.onDisconnect();
            oldSocket.close();
        } catch (IOException e) {
            Log.d(BLUETOOTH, e.getMessage());
        } catch (Exception e) {
            Log.d(BLUETOOTH, e.getClass().getName() + " " + e.getMessage());
        }
    }

    void changeState(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
        //int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
        if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF)
            mListener.onStateChange();

    }

    void switchBluetooth() {
        int state = btAdapter.getState();
        if (state == BluetoothAdapter.STATE_OFF) {
            btAdapter.enable();
        } else if (state == BluetoothAdapter.STATE_ON) {
            if (btSocket != null) {
                disconnectDevice();
            }
            btAdapter.disable();
        }
    }

    void onLowLevelDisconnect() {
        if (!manualDisconnect) {
            disconnectDevice();
        } else {
            manualDisconnect = false;
        }
    }

    public interface BluetoothConnectorListener {
        void onConnect(BluetoothSocket socket);

        void onDisconnect();

        void onStateChange();
    }

}
