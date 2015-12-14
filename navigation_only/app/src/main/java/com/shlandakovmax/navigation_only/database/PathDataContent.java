package com.shlandakovmax.navigation_only.database;

import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

public class PathDataContent {

    public static List<PathRecord> records = new LinkedList<PathRecord>();

    public static List<PathRecord> getAllPaths(PathDatabase pDB){
        Cursor dataCursor =  pDB.getAllData();
        dataCursor.moveToFirst();
        List<PathRecord> tempList = new LinkedList<PathRecord>();
        String pathName = "", pathDescription = "", pathFile = "", pathDate = "14.12.2015";
        do{
            int index = 0;
            index = dataCursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_NAME);
            pathName = dataCursor.getString(index);
            index = dataCursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_DESCR);
            pathDescription = dataCursor.getString(index);
            index = dataCursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_DATE);
            pathDate = dataCursor.getString(index);
            index = dataCursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_FILE);
            pathFile = dataCursor.getString(index);
            tempList.add(new PathRecord(pathName, pathDate, pathDescription, pathFile));
        }while (dataCursor.moveToNext());
        records = tempList;
        return records;
    }

    public static void addPath(String name, String descr, String date, String file){
        records.add(new PathRecord(name, date, descr, file));
    }

    public static void removePath(int listID){
        records.remove(listID);
    }

    public static class PathRecord{
        public final String name;
        public final String startDate;
        public final String description;
        public final String filePath;

        public PathRecord(String name, String startDate, String description, String filePath){
            this.name = name;
            this.description = description;
            this.filePath = filePath;
            this.startDate = startDate;
        }

        public String toString(){ return name; }
    }

}
