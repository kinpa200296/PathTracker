package com.pathtracker.android.tracker;

import android.util.Log;

import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetooth.PathTrackerResultListener;
import com.pathtracker.android.bluetooth.Result;

import java.util.List;

public class PathsListListener implements PathTrackerResultListener {

    private boolean _done;
    private List<String> _container;

    public PathsListListener(List<String> container){
        _done = false;
        _container = container;
    }

    public void startListening(PathTracker tracker){
        tracker.addListener(this);
    }

    public void stopListening(PathTracker tracker){
        tracker.removeListener(this);
    }

    public boolean isDone(){
        return _done;
    }

    @Override
    public void onResultReady(PathTracker tracker) {
        if (tracker.getLastResult() == Result.PathListItem){
            _container.add(tracker.getMessage());
        }
        else if (tracker.getLastResult() == Result.PathsListSent){
            _done = true;
        }
        else if (tracker.getLastResult() == Result.Error){
            _done = true;
        }
    }
}
