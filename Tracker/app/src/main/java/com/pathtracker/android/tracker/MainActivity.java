package com.pathtracker.android.tracker;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetoothservice.BluetoothConnector;
import com.pathtracker.android.tracker.database.PathDataContent;
import com.pathtracker.android.tracker.database.PathDatabase;
import com.pathtracker.android.tracker.dummy.DummyContent;
import com.pathtracker.android.tracker.files.PathFileParser;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener,
        PathItemFragment.OnListFragmentInteractionListener, AddPathFragment.OnAddPathInteractionListener,
        DeviceComFragment.OnFragmentInteractionListener, CreatePathFragment.OnFragmentInteractionListener,
        BluetoothConnector.BluetoothConnectorListener{

    private Fragment _mapFragment, _listFragment, _addFragment, _deviceFragment;
    private CreatePathFragment _createFragment;
    private SettingsFragment _settingsFragment;
    private PathDataContent.PathRecord _editTemp;
    private FragmentTransaction _transaction;

    PathTracker tracker;
    Intent serviceIntent;
    BluetoothServiceConnection serviceConnection;

    public PathDatabase database;

    public static final String serviceClassName = "com.pathtracker.android.bluetoothservice.BluetoothService";
    public static final String servicePackage = "com.pathtracker.android.bluetoothservice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tracker = new PathTracker();
        serviceIntent = new Intent(serviceClassName);
        serviceIntent.setPackage(servicePackage);
        serviceConnection = new BluetoothServiceConnection(tracker);

        startService(serviceIntent);

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
        if (database.isEmptyDatabase()){
            for (int i = 0; i < DummyContent.ITEMS.size(); i++){
                database.addPath(DummyContent.ITEMS.get(i).name, DummyContent.ITEMS.get(i).description,
                        DummyContent.ITEMS.get(i).startDate, DummyContent.ITEMS.get(i).filePath);
            }
        }
        PathDataContent.getAllPaths(database);

        _addFragment = new AddPathFragment();
        _deviceFragment = new DeviceComFragment();
        _settingsFragment = new SettingsFragment();

        _listFragment = new PathItemFragment();
        _transaction = getSupportFragmentManager().beginTransaction();
        _transaction.replace(R.id.main_container, _listFragment);
        _transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            _transaction.replace(R.id.main_container, _addFragment);
        } else if (id == R.id.nav_path_list) {
            _transaction.replace(R.id.main_container, _listFragment);
        } else if (id == R.id.nav_all_path_map) {
            //check connection and show map with your current location, else show no_device_connection xml
            _mapFragment = MapFragment.newInstance(DummyContent.dummyPath, MapFragment.VIEW_MODE_LOCATION);
            _transaction.replace(R.id.main_container, _mapFragment);
        } else if (id == R.id.nav_settings) {
            _transaction.replace(R.id.main_container, _settingsFragment);
        } else if (id == R.id.nav_device) {
            _transaction.replace(R.id.main_container, _deviceFragment);
        }

        _transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //interaction with PathsItemFragment
    @Override
    public void onListFragmentInteraction(PathDataContent.PathRecord item, int code) {
        if (code == PathItemFragment.CODE_ITEM_DELETE){
            int id = database.getPathIdByFilename(item.filePath);
            database.removePath(id);
            Toast.makeText(this, "deleting item called", Toast.LENGTH_SHORT).show();
        }
        else if (code == PathItemFragment.CODE_ITEM_EDIT){
            _editTemp = item;
            _transaction = getSupportFragmentManager().beginTransaction();
            _createFragment = CreatePathFragment.newInstance(item.name, item.description, CreatePathFragment.CALL_FOR_EDIT);
            _transaction.replace(R.id.main_container, _createFragment);
            _transaction.commit();
            //int id = database.getPathIdByFilename(item.filePath);
            //database.updateRowById(id, item);
            Toast.makeText(this, "editing item called", Toast.LENGTH_SHORT).show();
        }
        else if (code == PathItemFragment.CODE_ITEM_OPEN){
            String file = item.filePath;
            PathFileParser parser = new PathFileParser(this);
            parser.readPathFromFile(file);
            if (parser.points.size() == 0){
                Toast.makeText(this, "Not found or empty path!", Toast.LENGTH_LONG).show();
                return;
            }
            _transaction = getSupportFragmentManager().beginTransaction();
            _mapFragment =  MapFragment.newInstance(parser.points, MapFragment.VIEW_MODE_PATH);
            _transaction.replace(R.id.main_container, _mapFragment);
            _transaction.commit();
            Toast.makeText(this, "opening item called", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateFragmentInteraction(String name, String description, int result_code) {
        _transaction = getSupportFragmentManager().beginTransaction();
        if (result_code == CreatePathFragment.RESULT_OK){
            int id = database.getPathIdByFilename(_editTemp.filePath);
            database.updateRowById(id,new PathDataContent.PathRecord(name, _editTemp.startDate, description, _editTemp.filePath));
            _editTemp = null; _listFragment = null;
            PathDataContent.getAllPaths(database);
            _listFragment = new PathItemFragment();
            _transaction.replace(R.id.main_container, _listFragment);
            _transaction.commit();
        }
        else if (result_code == CreatePathFragment.RESULT_CANCEL){
            _transaction.replace(R.id.main_container, _listFragment);
            _transaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    //region BluetoothConnector interface implemented here
    @Override
    public void onConnect(BluetoothSocket socket) {
        _settingsFragment.onConnect(socket);
        if (serviceConnection.connected){
            serviceConnection.binder.setBtSocket(socket);
        }
    }

    @Override
    public void onDisconnect() {
        _settingsFragment.onDisconnect();
        if (serviceConnection.connected){
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
        _transaction = getSupportFragmentManager().beginTransaction();
        //createpathfragment is called here and interaction continues
    }

    @Override
    public void onGotoSettingsInteraction() {
        _transaction = getSupportFragmentManager().beginTransaction();
        _transaction.replace(R.id.main_container, _settingsFragment);
        _transaction.commit();
    }

    @Override
    public List<String> getFiles() {
        //here we should take files' name strings from the device
        return null;
    }

}
