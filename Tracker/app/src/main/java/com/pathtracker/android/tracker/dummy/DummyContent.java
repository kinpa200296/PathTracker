package com.pathtracker.android.tracker.dummy;

import com.google.android.gms.maps.model.LatLng;
import com.pathtracker.android.tracker.database.PathDataContent;

import java.util.LinkedList;
import java.util.List;

public class DummyContent {

    public static final List<PathDataContent.PathRecord> ITEMS = new LinkedList<PathDataContent.PathRecord>();

    public static final LinkedList<LatLng> dummyPath = new LinkedList<>();
    static { dummyPath.add(new LatLng(53 + 1.0 / 60 * 44.79739, 28 + 1.0 / 60 * 35.15628));
            dummyPath.add(new LatLng(53 + 1.0/60*45.35991, 28 + 1.0/60*37.9694));
            dummyPath.add(new LatLng(53 + 1.0/60*45.82746, 28 + 1.0/60*40.40885));
            dummyPath.add(new LatLng(53 + 1.0/60*46.36647, 28 + 1.0/60*43.01363));
            dummyPath.add(new LatLng(53 + 1.0/60*46.95141, 28 + 1.0/60*45.98174));
            dummyPath.add(new LatLng(53 + 1.0/60*47.45422, 28 + 1.0/60*48.48637));
            dummyPath.add(new LatLng(53 + 1.0/60*47.92648, 28 + 1.0/60*50.86092));}

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(new PathDataContent.PathRecord(getName(i),getDate(i), getDesc(i), getFile(i)));
        }
    }

    private static void addItem(PathDataContent.PathRecord item) {
        ITEMS.add(item);
    }

    private static String getName(int i){
        return "Patn #" + i;
    }


    private static String getDate(int i){
        return i+ ".12.2015";
    }

    private static String getDesc(int i){
        return "Patn #" + i + " description: that was a good time I've spent biking through a long green valley";
    }

    private static String getFile(int i){
        return "Patn_" + i;
    }
}