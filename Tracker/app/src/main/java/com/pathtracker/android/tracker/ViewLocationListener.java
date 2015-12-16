package com.pathtracker.android.tracker;

import com.google.android.gms.maps.model.LatLng;
import com.pathtracker.android.bluetooth.GpsData;
import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetooth.PathTrackerResultListener;
import com.pathtracker.android.bluetooth.Result;

public class ViewLocationListener implements PathTrackerResultListener {

    private MainActivity _activity;
    private LatLng _currentLocation;
    private boolean _signalAvailable;
    private boolean _listening;

    public ViewLocationListener(MainActivity activity) {
        _activity = activity;
        _currentLocation = new LatLng(999, 999);
        _signalAvailable = false;
        _listening = false;
    }

    public void startListening() {
        _activity.tracker.addListener(this);
        _listening = true;
    }

    public void stopListening() {
        _activity.tracker.removeListener(this);
        _listening = false;
        _signalAvailable = false;
    }

    public LatLng getCurrentLocation() {
        return _currentLocation;
    }

    public boolean isSignalAvailable() {
        return _signalAvailable;
    }

    public boolean isListening() {
        return _listening;
    }

    @Override
    public void onResultReady(PathTracker tracker) {
        if (tracker.getLastResult() == Result.Broadcast) {
            if (tracker.getMessageSize() == 6 && tracker.getMessage().equals("Active")) {
                _signalAvailable = true;
            } else if (tracker.getMessageSize() == 4 && tracker.getMessage().equals("Void")) {
                _signalAvailable = false;
            } else {
                GpsData data = GpsData.fromBytes(tracker.getMessageBytes());
                double latitude = data.getLatitudeDegrees() + 1.0 / 60.0 * data.getLatitudeMinutes();
                double longitude = data.getLongitudeDegrees() + 1.0 / 60.0 * data.getLongitudeMinutes();
                _currentLocation = new LatLng(latitude, longitude);
            }
        }
    }
}
