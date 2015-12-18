package com.pathtracker.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationUpdateReceiver extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "com.pathtracker.android.tracker.action.update.map";

    public LocationUpdateReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = (MainActivity) context;
        activity.mapFragment.DrawLocation(activity.locationListener.getCurrentLocation());
    }
}
