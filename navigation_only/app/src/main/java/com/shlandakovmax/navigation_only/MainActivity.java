package com.shlandakovmax.navigation_only;

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

import com.shlandakovmax.navigation_only.database.PathDataContent;
import com.shlandakovmax.navigation_only.database.PathDatabase;
import com.shlandakovmax.navigation_only.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapFragment.OnFragmentInteractionListener,
        PathItemFragment.OnListFragmentInteractionListener, AddPathFragment.OnFragmentInteractionListener,
        DeviceComFragment.OnFragmentInteractionListener, CreatePathFragment.OnFragmentInteractionListener{

    private Fragment mapFragment, listFragment, addFragment, deviceFragment, settingsFragment;
    private CreatePathFragment createFragment;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            //code, that will call mapFragment with reading this path information from file
            transaction = getSupportFragmentManager().beginTransaction();
            //change dummy path by path, loaded from file
            mapFragment =  MapFragment.newInstance(DummyContent.dummyPath);
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
}
