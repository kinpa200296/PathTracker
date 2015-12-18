package com.pathtracker.android.tracker;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetooth.Result;
import com.pathtracker.android.bluetoothservice.BluetoothConnector;
import com.pathtracker.android.bluetoothservice.BluetoothService;
import com.pathtracker.android.tracker.database.PathDataContent;
import com.pathtracker.android.tracker.database.PathDatabase;
import com.pathtracker.android.tracker.files.PathFileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener,
        PathItemFragment.OnListFragmentInteractionListener, AddPathFragment.OnAddPathInteractionListener,
        DeviceComFragment.OnDeviceCommunicationListener, CreatePathFragment.OnFragmentInteractionListener,
        BluetoothConnector.BluetoothConnectorListener, NoConnectionFragment.onNoConnection {

    private Fragment _listFragment, _addFragment, _deviceFragment, _noConnetionFragment;
    private CreatePathFragment _createFragment;
    private SettingsFragment _settingsFragment;
    private PathDataContent.PathRecord _editTemp;
    private FragmentTransaction _transaction;

    private int _uploadFileIndex;
    private String _uploadName, _uploadDescription;
    private ProgressDialog _uploadProgressDialog;

    MapFragment mapFragment;

    BluetoothConnector connector;
    PathTracker tracker;
    Intent serviceIntent;
    BluetoothServiceConnection serviceConnection;
    ViewLocationListener locationListener;
    PathLoadingListener pathLoadingListener;

    LocationUpdateReceiver locationUpdateReceiver;
    PathSentReceiver pathSentReceiver;

    public PathDatabase database;
    public static final String LOG_TAG = "PathTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connector = new BluetoothConnector(getString(R.string.mac_address), this);
        connector.registerReceivers();

        tracker = new PathTracker();
        serviceIntent = new Intent(this, BluetoothService.class);
        serviceConnection = new BluetoothServiceConnection(tracker);

        startService(serviceIntent);

        locationListener = new ViewLocationListener(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        database = new PathDatabase(this);
        database.open();
        PathDataContent.getAllPaths(database);

        _addFragment = new AddPathFragment();
        _deviceFragment = new DeviceComFragment();
        _settingsFragment = new SettingsFragment();
        _noConnetionFragment = new NoConnectionFragment();

        _listFragment = new PathItemFragment();
        _transaction = getSupportFragmentManager().beginTransaction();
        _transaction.replace(R.id.main_container, _listFragment);
        _transaction.commit();

        locationUpdateReceiver = new LocationUpdateReceiver();
        IntentFilter filter = new IntentFilter(LocationUpdateReceiver.BROADCAST_ACTION);
        registerReceiver(locationUpdateReceiver, filter);

        pathSentReceiver = new PathSentReceiver();
        filter = new IntentFilter(PathSentReceiver.BROADCAST_ACTION);
        registerReceiver(pathSentReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connector.unregisterReceivers();
        unregisterReceiver(locationUpdateReceiver);
        unregisterReceiver(pathSentReceiver);
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(serviceIntent, serviceConnection, BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        _transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_add_path) {
            if (serviceConnection.binder.isConnectedToDevice()) {
                _transaction.replace(R.id.main_container, _addFragment);
            } else {
                _transaction.replace(R.id.main_container, _noConnetionFragment);
            }
        } else if (id == R.id.nav_path_list) {
            _transaction.replace(R.id.main_container, _listFragment);
        } else if (id == R.id.nav_all_path_map) {
            //check connection and show map with your current location, else show no_device_connection xml
            if (mapFragment != null) {
                _transaction.remove(mapFragment);
            }
            if (serviceConnection.binder.isConnectedToDevice()) {
                locationListener.startListening();
                enableBroadcast();
                if (locationListener.getCurrentLocation() != null) {
                    LinkedList<LatLng> list = new LinkedList<>();
                    list.add(locationListener.getCurrentLocation());
                    mapFragment = MapFragment.newInstance(list, MapFragment.VIEW_MODE_LOCATION);
                } else {
                    mapFragment = MapFragment.newInstance(null, MapFragment.VIEW_MODE_LOCATION);
                }
                _transaction.replace(R.id.main_container, mapFragment);
            } else {
                _transaction.replace(R.id.main_container, _noConnetionFragment);
            }
        } else if (id == R.id.nav_settings) {
            _transaction.replace(R.id.main_container, _settingsFragment);
        } else if (id == R.id.nav_device) {
            if (serviceConnection.binder.isConnectedToDevice()) {
                _transaction.replace(R.id.main_container, _deviceFragment);
            } else {
                _transaction.replace(R.id.main_container, _noConnetionFragment);
            }
        }

        _transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //interaction with PathsItemFragment
    @Override
    public void onListFragmentInteraction(PathDataContent.PathRecord item, int code) {
        if (code == PathItemFragment.CODE_ITEM_DELETE) {
            int id = database.getPathIdByFilename(item.filePath);
            database.removePath(id);
            File file = new File(getFilesDir(), item.filePath);
            if (file.exists()) {
                if (file.delete()) {
                    Toast.makeText(this, "successfully deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "error while deleting", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "empty path removed, fuck yeah", Toast.LENGTH_SHORT).show();
            }
        } else if (code == PathItemFragment.CODE_ITEM_EDIT) {
            _editTemp = item;
            _transaction = getSupportFragmentManager().beginTransaction();
            _createFragment = CreatePathFragment.newInstance(item.name, item.description, CreatePathFragment.CALL_FOR_EDIT);
            _transaction.replace(R.id.main_container, _createFragment);
            _transaction.commit();
            //int id = database.getPathIdByFilename(item.filePath);
            //database.updateRowById(id, item);
            Toast.makeText(this, "editing item called", Toast.LENGTH_SHORT).show();
        } else if (code == PathItemFragment.CODE_ITEM_OPEN) {
            String file = item.filePath;
            PathFileParser parser = new PathFileParser(this);
            parser.readPathFromFile(file);
            if (parser.points.size() == 0) {
                Toast.makeText(this, "Not found or empty path!", Toast.LENGTH_LONG).show();
                return;
            }
            _transaction = getSupportFragmentManager().beginTransaction();
            mapFragment = MapFragment.newInstance(parser.points, MapFragment.VIEW_MODE_PATH);
            _transaction.replace(R.id.main_container, mapFragment);
            _transaction.commit();
            Toast.makeText(this, "opening item called", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateFragmentInteraction(String name, String description, int result_code, int mode) {
        if (result_code == CreatePathFragment.RESULT_OK) {
            if (mode == CreatePathFragment.CALL_FOR_EDIT) {
                _transaction = getSupportFragmentManager().beginTransaction();
                int id = database.getPathIdByFilename(_editTemp.filePath);
                database.updateRowById(id, new PathDataContent.PathRecord(name, _editTemp.startDate, description, _editTemp.filePath));
                _editTemp = null;
                PathDataContent.getAllPaths(database);
                _transaction.replace(R.id.main_container, _listFragment);
                _transaction.commit();
            } else if (mode == CreatePathFragment.CALL_FOR_CREATE) {
                _uploadName = name;
                _uploadDescription = description;
                String filename = UUID.randomUUID().toString() + ".dat";
                pathLoadingListener = new PathLoadingListener(this, filename);
                pathLoadingListener.startListening();
                requestPath();
                _uploadProgressDialog = new ProgressDialog(this);
                _uploadProgressDialog.setIndeterminate(true);
                _uploadProgressDialog.setTitle(R.string.loading);
                _uploadProgressDialog.setMessage(getString(R.string.selection) + _uploadFileIndex);
                _uploadProgressDialog.show();
            }
        } else if (result_code == CreatePathFragment.RESULT_CANCEL) {
            _transaction = getSupportFragmentManager().beginTransaction();
            if (mode == CreatePathFragment.CALL_FOR_EDIT) {
                _transaction.replace(R.id.main_container, _listFragment);
            } else {
                _transaction.replace(R.id.main_container, _addFragment);
            }
            _transaction.commit();
        }
    }

    void requestPath() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandSendPath(buffer, "" + _uploadFileIndex);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    void pathLoaded(Intent intent) {
        pathLoadingListener.stopListening();
        String filename = pathLoadingListener.getFilename();
        String startDate = pathLoadingListener.getStartDate();
        database.addPath(_uploadName, _uploadDescription, startDate, filename);
        _uploadProgressDialog.dismiss();
        Toast.makeText(this, intent.getStringExtra(PathLoadingListener.ARG_RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
        PathDataContent.getAllPaths(database);
        _transaction = getSupportFragmentManager().beginTransaction();
        _transaction.replace(R.id.main_container, _listFragment);
        _transaction.commit();
    }

    //region BluetoothConnector interface implemented here
    @Override
    public void onConnect(BluetoothSocket socket) {
        _settingsFragment.onConnect();
        if (serviceConnection.connected) {
            serviceConnection.binder.setBtSocket(socket);
        }
    }

    @Override
    public void onDisconnect() {
        _settingsFragment.onDisconnect();
        if (serviceConnection.connected) {
            serviceConnection.binder.setBtSocket(null);
        }
    }

    @Override
    public void onStateChange() {
        _settingsFragment.onStateChange();
    }
    //endregion

    //region AddPath interface implemented here
    @Override
    public void onAddPathInteraction(int fileIndex, int interact_code) {
        if (interact_code == AddPathFragment.CODE_FILE_ADD) {
            _transaction = getSupportFragmentManager().beginTransaction();
            _createFragment = CreatePathFragment.newInstance(null, null, (byte) interact_code);
            _uploadFileIndex = fileIndex;
            _transaction.replace(R.id.main_container, _createFragment);
            _transaction.commit();
        } else if (interact_code == AddPathFragment.CODE_FILE_DELETE) {
            DeletePathListener listener = new DeletePathListener(Result.PathDeleted);
            listener.startListening(tracker);
            deletePath(fileIndex);
            for (; !listener.isDone() && connector.isDeviceConnected(); ) ;
            listener.stopListening(tracker);
            if (!listener.isOk()) {
                Toast.makeText(this, listener.getMessage(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "path deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deletePath(int index) {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandDeletePath(buffer, "" + index);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    public void enableBroadcast() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandEnableBroadcast(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    public void disableBroadcast() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandDisableBroadcast(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    @Override
    public void stopBroadcast() {
        disableBroadcast();
        locationListener.stopListening();
    }

    public void requestPathsList() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandListPath(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    @Override
    public List<String> getFiles() {
        ArrayList<String> paths = new ArrayList<>();
        PathsListListener pathsListListener = new PathsListListener(paths);
        pathsListListener.startListening(tracker);
        requestPathsList();
        for (; !pathsListListener.isDone() && connector.isDeviceConnected(); ) ;
        pathsListListener.stopListening(tracker);
        return paths;
    }

    @Override
    public void callSettings() {
        _transaction = getSupportFragmentManager().beginTransaction();
        _transaction.replace(R.id.main_container, _settingsFragment);
        _transaction.commit();
    }

    @Override
    public boolean onPathStatusChange(byte new_status, String tag) {
        if (!connector.isDeviceConnected()) {
            Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show();
            return false;
        }
        PathStatusChangeListener listener;
        switch (new_status) {
            case DeviceComFragment.STATUS_PAUSE:
                listener = new PathStatusChangeListener(Result.PathPaused);
                listener.startListening(tracker);
                pausePath();
                break;
            case DeviceComFragment.STATUS_RESUME:
                listener = new PathStatusChangeListener(Result.PathResumed);
                listener.startListening(tracker);
                resumePath();
                break;
            case DeviceComFragment.STATUS_START:
                listener = new PathStatusChangeListener(Result.PathAdded);
                listener.startListening(tracker);
                startPath(tag);
                break;
            case DeviceComFragment.STATUS_STOP:
                listener = new PathStatusChangeListener(Result.PathStopped);
                listener.startListening(tracker);
                stopPath();
                break;
            default:
                return false;
        }
        for (; !listener.isDone() && connector.isDeviceConnected(); ) ;
        listener.stopListening(tracker);
        if (!listener.isOk()) {
            Toast.makeText(this, listener.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void startPath(String tag) {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandNewPath(buffer, tag);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    public void pausePath() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandPausePath(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    public void resumePath() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandResumePath(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }

    public void stopPath() {
        byte[] buffer = PathTracker.newBuffer();
        int msgSize = PathTracker.commandStopPath(buffer);
        if (serviceConnection.connected) {
            serviceConnection.binder.sendMessage(buffer, msgSize);
        } else {
            Log.w(LOG_TAG, "No connection with device");
        }
    }
}
