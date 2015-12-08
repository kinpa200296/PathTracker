package com.kinpa200296.android.labs.arduinocontrol;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    Switch btSwitch, btnConnect;
    TextView tvData;

    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothSocket btSocket;

    public final String BLUETOOTH = "Bluetooth";
    public final String CONNECT_ARDUINO = "com.kinpa200296.arduinocontrol.action.connect";
    public final String DISCONNECT_ARDUINO = "com.kinpa200296.arduinocontrol.action.disconnect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSwitch = (Switch) findViewById(R.id.btSwitch);
        btnConnect = (Switch) findViewById(R.id.btnConnect);
        tvData = (TextView) findViewById(R.id.tvData);

        btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
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
        registerReceiver(br, intentFilter);

        final Activity activity = this;

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
        registerReceiver(br, intentFilter);

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
        registerReceiver(br, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_refresh) {
            if (btSocket != null && btnConnect.isChecked()) {
                if (btSocket.isConnected()) {
                    readData();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readData() {
        InputStream inputStream;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = btSocket.getInputStream();
            for (byte b = '$'; inputStream.available() > 0; b = (byte) inputStream.read()) {
                if (b != '$'){
                    builder.append(String.format("%c", b));
                }
                else{
                    tvData.setText(builder.toString());
                    builder = new StringBuilder();
                    builder.append(String.format("%c", b));
                }
            }
            tvData.setText(builder.toString());
        } catch (Exception e) {
            Log.d(BLUETOOTH, e.getMessage());
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

    private void connectToDevice() {
        if (!btSwitch.isChecked())
            return;
        if (btnConnect.isChecked()) {
            btnConnect.setChecked(false);
            sendBroadcast(new Intent(CONNECT_ARDUINO));
        } else {
            btnConnect.setChecked(true);
            sendBroadcast(new Intent(DISCONNECT_ARDUINO));
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
            btSwitch.setText(R.string.btOn);
            btSwitch.setChecked(true);
            btnConnect.setEnabled(true);
        } else {
            btSwitch.setText(R.string.btOff);
            btSwitch.setChecked(false);
            btnConnect.setEnabled(false);
        }
    }
}
