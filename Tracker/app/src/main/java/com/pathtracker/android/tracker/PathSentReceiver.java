package com.pathtracker.android.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PathSentReceiver extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "com.pathtracker.android.tracker.action.pathSent";

    @Override
    public void onReceive(Context context, Intent intent) {
        ((MainActivity) context).pathLoaded(intent);
    }
}
