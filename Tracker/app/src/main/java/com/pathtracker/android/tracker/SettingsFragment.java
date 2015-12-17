package com.pathtracker.android.tracker;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.pathtracker.android.bluetoothservice.BluetoothConnector;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Switch btSwitch, btnConnect;
    private BluetoothConnector _connector;

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
        changeBtSwitchState();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _connector = ((MainActivity) context).connector;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSwitch:
                _connector.manualDisconnect = true;
                getActivity().sendBroadcast(new Intent(BluetoothConnector.SWITCH_BLUETOOTH));
                if (btSwitch.isChecked()) btSwitch.setChecked(false);
                else btSwitch.setChecked(true);
                break;
            case R.id.btnConnect:
                if (!btSwitch.isChecked())
                    return;
                if (btnConnect.isChecked()) {
                    btnConnect.setChecked(false);
                    getActivity().sendBroadcast(new Intent(BluetoothConnector.CONNECT_DEVICE));
                } else {
                    btnConnect.setChecked(true);
                    _connector.manualDisconnect = true;
                    getActivity().sendBroadcast(new Intent(BluetoothConnector.DISCONNECT_DEVICE));
                }
                break;
            default:
                break;
        }
    }


    private void changeBtSwitchState() {
        int btState = _connector.getBtState();
        if (btState == BluetoothAdapter.STATE_ON || btState == BluetoothAdapter.STATE_TURNING_ON) {
            btSwitch.setText(R.string.BT_on);
            btSwitch.setChecked(true);
            btnConnect.setEnabled(true);
            if (_connector.isDeviceConnected()) {
                onConnect();
            } else {
                onDisconnect();
            }
        } else {
            btSwitch.setText(R.string.BT_off);
            btSwitch.setChecked(false);
            btnConnect.setEnabled(false);
        }
    }

    public void onConnect() {
        //connect socket to service here
        btnConnect.setChecked(true);
        btnConnect.setText(R.string.device_on);
    }

    public void onDisconnect() {
        btnConnect.setChecked(false);
        btnConnect.setText(R.string.device_off);
    }

    public void onStateChange() {
        changeBtSwitchState();
        if (btnConnect.isChecked()) {
            btnConnect.setChecked(false);
        }
    }
}
