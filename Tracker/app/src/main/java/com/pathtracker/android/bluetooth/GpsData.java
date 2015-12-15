package com.pathtracker.android.bluetooth;

public class GpsData {
    static {
        System.loadLibrary("GpsData");
    }

    private native boolean parseData(byte[] bytes);

    private byte _latitudeDegrees, _longitudeDegrees;
    private float _latitudeMinutes, _longitudeMinutes;
    private int _time, _date;
    private boolean _active;

    private GpsData(){

    }

    public static GpsData fromBytes(byte[] bytes){
        GpsData res = new GpsData();
        res._active = res.parseData(bytes);
        return res;
    }

    public byte getLatitudeDegrees() {
        return _latitudeDegrees;
    }

    public byte getLongitudeDegrees() {
        return _longitudeDegrees;
    }

    public float getLatitudeMinutes() {
        return _latitudeMinutes;
    }

    public float getLongitudeMinutes() {
        return _longitudeMinutes;
    }

    public int getTime() {
        return _time;
    }

    public int getDate() {
        return _date;
    }

    public boolean isActive() {
        return _active;
    }
}
