package com.pathtracker.android.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    public static int VIEW_MODE_PATH = 1;
    public static int VIEW_MODE_LOCATION = 2;


    LatLng[] pathOnMap;
    private int _mode;
    private GoogleMap mMap;
    private OnFragmentInteractionListener mListener;
    private static double WORLD_DIM = 256;
    private static float ZOOM_MAX = 18;
    private boolean _notConnected = false;
    private float _locationZoom = 15;

    private static final String ARG_MODE = "mode";
    private static final String ARG_LATITUDE = "lats";
    private static final String ARG_LONGITUDE = "longs";

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(LinkedList<LatLng> dots, int mode) {
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);

        if (dots != null) {
            double[] lat = new double[dots.size()];
            double[] lng = new double[dots.size()];
            for (int i = 0; i < dots.size(); i++) {
                lat[i] = dots.get(i).latitude;
                lng[i] = dots.get(i).longitude;
            }
            args.putDoubleArray(ARG_LATITUDE, lat);
            args.putDoubleArray(ARG_LONGITUDE, lng);
        }

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_mode == VIEW_MODE_LOCATION) {
            mListener.stopBroadcast();
        }
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
        _mode = bundle.getInt(ARG_MODE);
        if (bundle.containsKey(ARG_LATITUDE) && bundle.containsKey(ARG_LONGITUDE)) {
            double[] lat = bundle.getDoubleArray(ARG_LATITUDE);
            double[] lng = bundle.getDoubleArray(ARG_LONGITUDE);
            pathOnMap = new LatLng[lat.length];
            for (int i = 0; i < lat.length; i++) {
                LatLng temp = new LatLng(lat[i], lng[i]);
                pathOnMap[i] = temp;
            }
        } else {
            pathOnMap = new LatLng[]{};
        }
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
        if (_mode == VIEW_MODE_PATH)
            DrawPathOnMap();
        else if (_mode == VIEW_MODE_LOCATION) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(_locationZoom));
            if (pathOnMap.length == 0)
                DrawLocation(null);
            else DrawLocation(pathOnMap[pathOnMap.length - 1]);
        }
    }

    private void DrawPathOnMap() {
        if (pathOnMap.length != 0) {
            mMap.addMarker(new MarkerOptions().position(pathOnMap[0]).title("Path starts here..."));
            mMap.addMarker(new MarkerOptions().position(pathOnMap[pathOnMap.length - 1]).title("And it\'s end is there"));
            PolylineOptions options = new PolylineOptions();
            options.add(pathOnMap);
            options.color(0xFFFF0000);
            mMap.addPolyline(options);
            double maxLat = pathOnMap[0].latitude, minLat = pathOnMap[0].latitude,
                    maxLng = pathOnMap[0].longitude, minLng = pathOnMap[0].longitude;
            if (pathOnMap.length > 1)
                for (int i = 1; i < pathOnMap.length; i++) {
                    if (pathOnMap[i].latitude > maxLat) maxLat = pathOnMap[i].latitude;
                    else if (pathOnMap[i].latitude < minLat) minLat = pathOnMap[i].latitude;
                    if (pathOnMap[i].longitude > maxLng) maxLng = pathOnMap[i].longitude;
                    else if (pathOnMap[i].longitude < minLng) minLng = pathOnMap[i].longitude;
                }
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
            float zoom = getZoomLevel(bounds);
            Toast.makeText(getActivity(), String.valueOf(zoom), Toast.LENGTH_SHORT).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), getZoomLevel(bounds)));
        } else
            Toast.makeText(getActivity(), "Oops! Looks like empty path found here...", Toast.LENGTH_LONG).show();
    }

    public void DrawLocation(LatLng location) {
        if (location != null && mMap != null) {
            if (mMap.getCameraPosition() != null) {
                _locationZoom = mMap.getCameraPosition().zoom;
            }
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(location).title("Your estimated location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, _locationZoom));
            if (_notConnected) {
                _notConnected = false;
            }
        } else if (!_notConnected) {
            Toast.makeText(getActivity(), "Can't find your location yet! Seems like a bad signal...", Toast.LENGTH_LONG).show();
            _notConnected = true;
        }
    }

    public float getZoomLevel(LatLngBounds bounds) {
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        float latZoom = (float) zoom(WORLD_DIM, latFraction);
        float lngZoom = (float) zoom(WORLD_DIM, lngFraction);
        return Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
    }

    private double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private double zoom(double worldPx, double fraction) {
        return Math.floor(Math.log(256 / worldPx / fraction) / Math.log(2));
    }

    public interface OnFragmentInteractionListener {
        void stopBroadcast();
    }
}