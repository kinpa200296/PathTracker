package com.pathtracker.android.tracker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class SettingsFragment extends Fragment implements View.OnClickListener{

    private Switch btSwitch, btnConnect, mapSwitch;
    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private BluetoothSocket btSocket;

    public final String BLUETOOTH = "Bluetooth";
    public final String CONNECT_ARDUINO = "com.shlandakovmax.action.connect";
    public final String DISCONNECT_ARDUINO = "com.shlandakovmax.action.disconnect";

    public boolean isDeviceConnected;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        btSwitch = (Switch) view.findViewById(R.id.btSwitch);
        btSwitch.setOnClickListener(this);
        btnConnect = (Switch) view.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btSocket = null;

        btSocket = null;

        changeBtSwitchState();

        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                //int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
                if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF)
                    changeBtSwitchState();
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(br, intentFilter);

        final Activity activity = getActivity();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice btDevice = null;
                Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    if (bondedDevice.getName().equals(getString(R.string.deviceName))) {
                        btDevice = bondedDevice;
                    }
                    //Log.d(BLUETOOTH, bondedDevice.getName());
                }
                if (btDevice == null) {
                    Log.d(BLUETOOTH, "Device not found");
                    Toast.makeText(activity, "Device not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    btSocket.connect();
                    btnConnect.setChecked(true);
                    Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d(BLUETOOTH, e.getMessage());
                    Toast.makeText(activity, "Connection failed", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d(BLUETOOTH, e.getClass().getName() + " " + e.getMessage());
                    Toast.makeText(activity, "Connection failed", Toast.LENGTH_SHORT).show();
                }

            }
        };

        intentFilter = new IntentFilter(CONNECT_ARDUINO);
        getActivity().registerReceiver(br, intentFilter);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    btSocket.close();
                    btSocket = null;
                } catch (IOException e) {
                    Log.d(BLUETOOTH, e.getMessage());
                } catch (Exception e) {
                    Log.d(BLUETOOTH, e.getClass().getName() + " " + e.getMessage());
                }
                btnConnect.setChecked(false);
                Toast.makeText(activity, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        intentFilter = new IntentFilter(DISCONNECT_ARDUINO);
        getActivity().registerReceiver(br, intentFilter);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void connectToDevice() {
        if (!btSwitch.isChecked())
            return;
        if (btnConnect.isChecked()) {
            btnConnect.setChecked(false);
            getActivity().sendBroadcast(new Intent(CONNECT_ARDUINO));
        } else {
            btnConnect.setChecked(true);
            getActivity().sendBroadcast(new Intent(DISCONNECT_ARDUINO));
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSwitch:
                changeBtState();
                break;
            case R.id.btnConnect:
                connectToDevice();
                break;
            default:
                break;
        }
    }

    private void changeBtState() {
        if (btSwitch.isChecked()) {
            btAdapter.enable();
            btSwitch.setChecked(false);
        } else {
            btnConnect.setChecked(false);
            btSwitch.setChecked(true);
            connectToDevice();
            btAdapter.disable();
        }
    }

    private void changeBtSwitchState() {
        int btState = btAdapter.getState();
        if (btState == BluetoothAdapter.STATE_ON || btState == BluetoothAdapter.STATE_TURNING_ON) {
            btSwitch.setText(R.string.BT_on);
            btSwitch.setChecked(true);
            btnConnect.setEnabled(true);
        } else {
            btSwitch.setText(R.string.BT_off);
            btSwitch.setChecked(false);
            btnConnect.setEnabled(false);
        }
    }
}
