package com.pathtracker.android.tracker.files;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class PathFileParser {

    Context context;
    public LinkedList<LatLng> points;
    public int startDate, startTime, finishDate, finishTime;

    public PathFileParser(Context context){
        this.context = context;
    }

    public void readPathFromFile(String fileName){
        LinkedList<LatLng> res = new LinkedList<>();
        DataInputStream stream = null;
        try{
            stream = new DataInputStream(new FileInputStream(fileName));
            LatLng buffer;
            if (stream.available()> 18) {
                res.add(parseLatLng(stream));
                startDate = stream.readInt();
                startTime = stream.readInt();
            }
            while (stream.available() > 36){
                buffer = parseLatLng(stream);
                if (buffer !=null) res.add(buffer);
                stream.skipBytes(8);
            }
            res.add(parseLatLng(stream));
            finishDate = stream.readInt();
            finishTime = stream.readInt();
            stream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        points = res;
        return;
    }

    private LatLng parseLatLng(DataInputStream stream){
        try{
            return new LatLng(((double)stream.readByte() + 1.0/60*stream.readFloat()), ((double)stream.readByte() + 1.0/60*stream.readFloat()));
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
