package com.pathtracker.android.tracker;


import android.util.Log;

import com.pathtracker.android.bluetooth.PathTracker;
import com.pathtracker.android.bluetooth.PathTrackerResultListener;
import com.pathtracker.android.bluetooth.Result;


public class PathStatusChangeListener implements PathTrackerResultListener {

    private Result _targetResult;
    private boolean _done;
    private boolean _status;
    private String _message;

    public PathStatusChangeListener(Result targetResult) {
        _targetResult = targetResult;
    }

    public void startListening(PathTracker tracker) {
        tracker.addListener(this);
        _done = false;
        _message = "";
    }

    public void stopListening(PathTracker tracker) {
        tracker.removeListener(this);
    }

    public boolean isDone(){
        return _done;
    }

    public boolean isOk(){
        return _status;
    }

    public String getMessage(){
        return _message;
    }

    @Override
    public void onResultReady(PathTracker tracker) {
        if (tracker.getLastResult() == _targetResult) {
            _done = true;
            _status = true;
            _message = tracker.getMessage();
        }
        else if (tracker.getLastResult() == Result.Error){
            _done = true;
            _status = false;
            _message = tracker.getMessage();
        }
    }
}
