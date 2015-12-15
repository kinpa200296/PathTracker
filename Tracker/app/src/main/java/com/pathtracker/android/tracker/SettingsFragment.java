package com.pathtracker.android.tracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.pathtracker.android.bluetoothservice.BluetoothBinder;
import com.pathtracker.android.bluetoothservice.BluetoothConnector;


public class SettingsFragment extends Fragment implements View.OnClickListener{

    private Switch btSwitch, btnConnect;
    BluetoothBinder binder;
    BluetoothConnector connector;

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
        connector = new BluetoothConnector(getString(R.string.mac_address), view.getContext());
        //binder connection should be added
        changeBtSwitchState();
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


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSwitch:
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
                    getActivity().sendBroadcast(new Intent(BluetoothConnector.DISCONNECT_DEVICE));
                }
                break;
            default:
                break;
        }
    }


    private void changeBtSwitchState() {
        int btState = connector.getBtState();
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

    public void onConnect(BluetoothSocket socket) {
        //connect socket to service here
        btnConnect.setChecked(true);
    }

    public void onDisconnect() {
        btnConnect.setChecked(false);
    }

    public void onStateChange() {
        if (btSwitch.isChecked()){
            btnConnect.setEnabled(false);
            if (btnConnect.isChecked()) btnConnect.setChecked(false);
            btSwitch.setChecked(false);
        }
        else{
            btnConnect.setEnabled(true);
            btSwitch.setChecked(true);
        }
    }
}
