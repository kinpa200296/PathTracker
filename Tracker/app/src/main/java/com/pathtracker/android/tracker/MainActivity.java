package com.pathtracker.android.tracker;

import android.bluetooth.BluetoothSocket;
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

import com.pathtracker.android.bluetoothservice.BluetoothConnector;
import com.pathtracker.android.tracker.database.PathDataContent;
import com.pathtracker.android.tracker.database.PathDatabase;
import com.pathtracker.android.tracker.dummy.DummyContent;
import com.pathtracker.android.tracker.files.PathFileParser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener,
        PathItemFragment.OnListFragmentInteractionListener, AddPathFragment.OnFragmentInteractionListener,
        DeviceComFragment.OnFragmentInteractionListener, CreatePathFragment.OnFragmentInteractionListener,
        BluetoothConnector.BluetoothConnectorListener{

    private Fragment mapFragment, listFragment, addFragment, deviceFragment;
    private CreatePathFragment createFragment;
    private SettingsFragment settingsFragment;
    private PathDataContent.PathRecord editTemp;
    private FragmentTransaction transaction;
    public PathDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        addFragment = new AddPathFragment();
        deviceFragment = new DeviceComFragment();
        settingsFragment = new SettingsFragment();

        listFragment = new PathItemFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, listFragment);
        transaction.commit();
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
        transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_add_path) {
            transaction.replace(R.id.main_container, addFragment);
        } else if (id == R.id.nav_path_list) {
            transaction.replace(R.id.main_container, listFragment);
        } else if (id == R.id.nav_all_path_map) {
            mapFragment = MapFragment.newInstance(DummyContent.dummyPath);
            transaction.replace(R.id.main_container, mapFragment);
        } else if (id == R.id.nav_settings) {
            transaction.replace(R.id.main_container, settingsFragment);
        } else if (id == R.id.nav_device) {
            transaction.replace(R.id.main_container, deviceFragment);
        }

        transaction.commit();

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
            editTemp = item;
            transaction = getSupportFragmentManager().beginTransaction();
            createFragment = CreatePathFragment.newInstance(item.name, item.description, CreatePathFragment.CALL_FOR_EDIT);
            transaction.replace(R.id.main_container, createFragment);
            transaction.commit();
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
            transaction = getSupportFragmentManager().beginTransaction();
            mapFragment =  MapFragment.newInstance(parser.points);
            transaction.replace(R.id.main_container, mapFragment);
            transaction.commit();
            Toast.makeText(this, "opening item called", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateFragmentInteraction(String name, String description, int result_code) {
        transaction = getSupportFragmentManager().beginTransaction();
        if (result_code == CreatePathFragment.RESULT_OK){
            int id = database.getPathIdByFilename(editTemp.filePath);
            database.updateRowById(id,new PathDataContent.PathRecord(name, editTemp.startDate, description, editTemp.filePath));
            editTemp = null; listFragment = null;
            PathDataContent.getAllPaths(database);
            listFragment = new PathItemFragment();
            transaction.replace(R.id.main_container, listFragment);
            transaction.commit();
        }
        else if (result_code == CreatePathFragment.RESULT_CANCEL){
            transaction.replace(R.id.main_container, listFragment);
            transaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    //region BluetoothConnector interface implemented here
    @Override
    public void onConnect(BluetoothSocket socket) {
        settingsFragment.onConnect(socket);
    }

    @Override
    public void onDisconnect() {
        settingsFragment.onDisconnect();
    }

    @Override
    public void onStateChange() {
        settingsFragment.onStateChange();
    }
    //endregion
}
