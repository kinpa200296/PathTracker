package com.shlandakovmax.navigation_only;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    //possible as a list of paths, set to be displayed
    LatLng[] pathOnMap;
    private GoogleMap mMap;
    private OnFragmentInteractionListener mListener;
    private static double WORLD_DIM = 256;
    private static float ZOOM_MAX = 18;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(LatLng[] dots) {
        Bundle args = new Bundle();
        args.putParcelableArray("dots", dots);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        this.getMapAsync(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        pathOnMap = (LatLng[]) bundle.getParcelableArray("dots");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DrawOnMap();
    }

    private void DrawOnMap(){
        mMap.addMarker(new MarkerOptions().position(pathOnMap[0]).title("Path starts here..."));
        mMap.addMarker(new MarkerOptions().position(pathOnMap[pathOnMap.length - 1]).title("And it\'s end is there"));
        PolylineOptions options = new PolylineOptions();
        options.add(pathOnMap);
        options.color(0xFFFF0000);
        mMap.addPolyline(options);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        double maxLat = pathOnMap[0].latitude, minLat = pathOnMap[0].latitude,
                maxLng = pathOnMap[0].longitude, minLng = pathOnMap[0].longitude;
        if (pathOnMap.length >1)
            for (int i = 1; i<pathOnMap.length; i++){
                if (pathOnMap[i].latitude > maxLat) maxLat = pathOnMap[i].latitude;
                else if (pathOnMap[i].latitude < minLat) minLat = pathOnMap[i].latitude;
                if (pathOnMap[i].longitude > maxLng) maxLng = pathOnMap[i].longitude;
                else if (pathOnMap[i].longitude < minLng) minLng = pathOnMap[i].longitude;
            }
        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat,minLng), new LatLng(maxLat, maxLng));
        float zoom = getZoomLevel(bounds);
        Toast.makeText(getActivity(), String.valueOf(zoom),Toast.LENGTH_SHORT).show();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), getZoomLevel(bounds)));
    }

    public float getZoomLevel(LatLngBounds bounds){
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude)- latRad(sw.latitude))/Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0)? (lngDiff + 360): lngDiff)/360;
        float latZoom = (float)zoom( WORLD_DIM, latFraction);
        float lngZoom = (float)zoom( WORLD_DIM, lngFraction);
        return Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
    }

    private double latRad(double lat){
        double sin = Math.sin(lat*Math.PI/180);
        double radX2 = Math.log((1+sin)/(1-sin))/2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI)/2;
    }

    private double zoom( double worldPx, double fraction){
        return Math.floor(Math.log(256/worldPx/fraction)/Math.log(2));
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}