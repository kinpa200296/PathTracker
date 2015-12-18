package com.pathtracker.android.tracker;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pathtracker.android.bluetooth.GpsData;
import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetooth.PathTrackerResultListener;
import com.pathtracker.android.bluetooth.Result;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PathLoadingListener implements PathTrackerResultListener {

    private MainActivity _activity;
    private String _filename;
    private FileOutputStream _outputStream;
    private int _startDate;

    public static final String ARG_RESULT_MESSAGE = "resultMessage";

    public PathLoadingListener(MainActivity activity, String filename) {
        _activity = activity;
        _filename = filename;
    }

    public void startListening() {
        _startDate = 0;
        _activity.tracker.addListener(this);
        try {
            _outputStream = _activity.openFileOutput(_filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        _activity.tracker.removeListener(this);
        try {
            _outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return _filename;
    }

    public String getStartDate() {
        return _startDate / 10000 + "." + (_startDate / 100) % 100 + "." + _startDate % 100;
    }

    @Override
    public void onResultReady(PathTracker tracker) {
        if (tracker.getLastResult() == Result.PathPart) {
            try {
                _outputStream.write(tracker.getMessageBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (_startDate == 0) {
                GpsData data = GpsData.fromBytes(tracker.getMessageBytes());
                _startDate = data.getDate();
            }
        } else if (tracker.getLastResult() == Result.PathSent) {
            Intent broadcastIntent = new Intent(PathSentReceiver.BROADCAST_ACTION);
            broadcastIntent.putExtra(ARG_RESULT_MESSAGE, "Loaded path with tag " + tracker.getMessage());
            _activity.sendBroadcast(broadcastIntent);
        } else if (tracker.getLastResult() == Result.Error) {
            Intent broadcastIntent = new Intent(PathSentReceiver.BROADCAST_ACTION);
            broadcastIntent.putExtra(ARG_RESULT_MESSAGE, tracker.getMessage());
            _activity.sendBroadcast(broadcastIntent);
        }
    }
}
