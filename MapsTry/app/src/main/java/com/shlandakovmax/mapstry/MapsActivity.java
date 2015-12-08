package com.shlandakovmax.mapstry;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng dot0 = new LatLng(53 + 1.0/60*44.79739, 28 + 1.0/60*35.15628);
        LatLng dot1 = new LatLng(53 + 1.0/60*44.80324, 28 + 1.0/60*35.18360);
        LatLng dota2 = new LatLng(53 + 1.0/60*44.80900, 28 + 1.0/60*35.21114);
        LatLng dot3 = new LatLng(53 + 1.0/60*44.81460, 28 + 1.0/60*35.23863);
        LatLng dot4 = new LatLng(53 + 1.0/60*44.82018, 28 + 1.0/60*35.26620);

        LatLng dot5 = new LatLng(53 + 1.0/60* 44.82580, 28 + 1.0/60*35.29383);
        LatLng dot6 = new LatLng(53 + 1.0/60* 44.85424, 28 + 1.0/60*35.43357);

        mMap.addMarker(new MarkerOptions().position(dot1).title("Somwhere in Belarus..."));
        mMap.addMarker(new MarkerOptions().position(dot5).title("Somwhere in Belarus..."));
        mMap.addMarker(new MarkerOptions().position(dot6).title("Somwhere in Belarus..."));
        LatLng[] points = new LatLng[]{dot0, dot1, dota2, dot3, dot4, dot5, dot6};
        PolylineOptions options = new PolylineOptions();
        options.add(points);
        options.color(0xFFFF0000);
        mMap.addPolyline(options);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dot4, 14));
    }
}
