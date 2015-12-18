package com.pathtracker.android.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.pathtracker.android.bluetoothservice.BluetoothService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PathTracker {
    static {
        System.loadLibrary("GpsTracker");
    }

    public static final int MESSAGE_BUFFER_LENGTH = 100;

    public static byte[] newBuffer() {
        return new byte[MESSAGE_BUFFER_LENGTH];
    }

    public static boolean sendMessage(BluetoothSocket btSocket, byte[] msg, int msgSize) {
        try {
            OutputStream stream = btSocket.getOutputStream();
            stream.write(msg, 0, msgSize);
            stream.flush();
        } catch (IOException e) {
            Log.e(BluetoothService.LOG_TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public static native int commandListPath(byte[] buffer);

    public static native int commandSendPath(byte[] buffer, String idStr);

    public static native int commandDeletePath(byte[] buffer, String idStr);

    public static native int commandNewPath(byte[] buffer, String name);

    public static native int commandEnableBroadcast(byte[] buffer);

    public static native int commandDisableBroadcast(byte[] buffer);

    public static native int commandPausePath(byte[] buffer);

    public static native int commandResumePath(byte[] buffer);

    public static native int commandStopPath(byte[] buffer);

    public static native int commandGetState(byte[] buffer);

    private native String bytesToString(byte[] bytes);

    private byte[] _buffer;
    private int _bufferPos;
    private Result _lastResult;
    private boolean _readMsgSize;
    private int _currentMsgSize;
    private ArrayList<PathTrackerResultListener> listeners;

    public PathTracker() {
        _buffer = newBuffer();
        _bufferPos = 0;
        _buffer[_bufferPos] = 0;
        _lastResult = Result.NoResult;
        _readMsgSize = false;
        listeners = new ArrayList<>();
    }

    public boolean analyze(byte b) {

        if (_lastResult == Result.NoResult) {
            _bufferPos = 0;
            _buffer[_bufferPos] = 0;
            _readMsgSize = true;
            _parseResult(b);
        } else {
            if (_readMsgSize) {
                _currentMsgSize = b > _buffer.length - 1 ? _buffer.length - 1 : b;
                _readMsgSize = false;
            } else if (_bufferPos < _currentMsgSize) {
                _buffer[_bufferPos] = b;
                _bufferPos++;
                _buffer[_bufferPos] = 0;
            }
            if (_bufferPos == _currentMsgSize) {
                onResultReady();
                resetResult();
                return true;
            }
        }

        return false;
    }

    public void resetResult() {
        _lastResult = Result.NoResult;
    }

    public void addListener(PathTrackerResultListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PathTrackerResultListener listener) {
        listeners.remove(listener);
    }

    public void onResultReady() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onResultReady(this);
        }
    }

    public Result getLastResult() {
        return _lastResult;
    }

    public String getMessage() {
        byte[] temp = new byte[_bufferPos + 1];
        System.arraycopy(_buffer, 0, temp, 0, _bufferPos);
        temp[_bufferPos] = 0;
        return bytesToString(temp);
    }

    public byte[] getMessageBytes() {
        byte[] res = new byte[_bufferPos];
        System.arraycopy(_buffer, 0, res, 0, _bufferPos);
        return res;
    }

    public int getMessageSize() {
        return _bufferPos;
    }

    public State parseState() {
        State res = State.Invalid;
        if (_bufferPos > 0) {
            if (State.Idle.ordinal() == _buffer[0]) {
                res = State.Idle;
            }
            if (State.Recording.ordinal() == _buffer[0]) {
                res = State.Recording;
            }
            if (State.Waiting.ordinal() == _buffer[0]) {
                res = State.Waiting;
            }
        }
        return res;
    }

    private void _parseResult(byte b) {
        if (Result.NoResult.ordinal() == b) {
            _lastResult = Result.NoResult;
        }
        if (Result.Error.ordinal() == b) {
            _lastResult = Result.Error;
        }
        if (Result.UnknownCommand.ordinal() == b) {
            _lastResult = Result.UnknownCommand;
        }
        if (Result.PathListItem.ordinal() == b) {
            _lastResult = Result.PathListItem;
        }
        if (Result.PathsListSent.ordinal() == b) {
            _lastResult = Result.PathsListSent;
        }
        if (Result.PathPart.ordinal() == b) {
            _lastResult = Result.PathPart;
        }
        if (Result.PathSent.ordinal() == b) {
            _lastResult = Result.PathSent;
        }
        if (Result.PathAdded.ordinal() == b) {
            _lastResult = Result.PathAdded;
        }
        if (Result.PathDeleted.ordinal() == b) {
            _lastResult = Result.PathDeleted;
        }
        if (Result.BroadcastEnabled.ordinal() == b) {
            _lastResult = Result.BroadcastEnabled;
        }
        if (Result.BroadcastDisabled.ordinal() == b) {
            _lastResult = Result.BroadcastDisabled;
        }
        if (Result.Broadcast.ordinal() == b) {
            _lastResult = Result.Broadcast;
        }
        if (Result.PathPaused.ordinal() == b) {
            _lastResult = Result.PathPaused;
        }
        if (Result.PathResumed.ordinal() == b) {
            _lastResult = Result.PathResumed;
        }
        if (Result.PathStopped.ordinal() == b) {
            _lastResult = Result.PathStopped;
        }
        if (Result.State.ordinal() == b) {
            _lastResult = Result.State;
        }
    }
}
