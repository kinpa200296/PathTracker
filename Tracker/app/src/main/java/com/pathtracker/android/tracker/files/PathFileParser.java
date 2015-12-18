package com.pathtracker.android.tracker.files;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.pathtracker.android.bluetooth.GpsData;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PathFileParser {

    Context context;
    public LinkedList<LatLng> points;
    public int startDate, startTime, finishDate, finishTime;

    public PathFileParser(Context context) {
        this.context = context;
    }

    public void readPathFromFile(String fileName) {
        LinkedList<LatLng> res = new LinkedList<>();
        DataInputStream stream = null;
        try {
            stream = new DataInputStream(context.openFileInput(fileName));
            byte[] buffer = new byte[GpsData.SIZE];
            GpsData data;
            if (stream.available() > GpsData.SIZE) {
                stream.read(buffer, 0, GpsData.SIZE);
                data = GpsData.fromBytes(buffer);
                res.add(data.getLatLng());
                startDate = data.getDate();
                startTime = data.getTime();
            }
            while (stream.available() > 18) {
                stream.read(buffer, 0, GpsData.SIZE);
                data = GpsData.fromBytes(buffer);
                res.add(data.getLatLng());
            }
            int temp = stream.read(buffer, 0, GpsData.SIZE);
            if (temp == GpsData.SIZE) {
                data = GpsData.fromBytes(buffer);
                res.add(data.getLatLng());
                finishDate = data.getDate();
                finishTime = data.getTime();
                if (startDate == 0 && startTime == 0) {
                    startDate = finishDate;
                    startTime = finishTime;
                }
            }
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        points = res;
    }
}
