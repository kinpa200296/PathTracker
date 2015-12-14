package com.shlandakovmax.navigation_only.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PathDatabase {
    private static final String DB_NAME = "PathDB";
    private static final int DB_VER = 12;

    private Context _context;
    private DatabaseHelper _DBHelper;
    private SQLiteDatabase _DB;

    public PathDatabase (Context context){_context = context;}

    public void open(){
        _DBHelper = new DatabaseHelper(_context, DB_NAME, null, DB_VER);
        _DB = _DBHelper.getWritableDatabase();
    }

    private void close() {
        if (_DBHelper !=null) _DBHelper.close();
    }

    public Cursor getAllData(){
        return _DB.query(DatabaseHelper.TABLE_NAME, null, null,null,null,null, null);
    }

    public void addPath(String name, String description, String startDate, String filePath){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_PATH_NAME, name);
        cv.put(DatabaseHelper.COLUMN_PATH_DESCR, description);
        cv.put(DatabaseHelper.COLUMN_PATH_DATE, startDate);
        cv.put(DatabaseHelper.COLUMN_PATH_FILE, filePath);
        _DB.insert(DatabaseHelper.TABLE_NAME, null, cv);
    }

    public void removePath(int _id){
        _DB.delete(DatabaseHelper.TABLE_NAME, String.format( DatabaseHelper.COLUMN_PATH_ID + " = " + _id), null);
    }

    public int getPathIdByFilename(String filename){
        String query = "select " + DatabaseHelper.COLUMN_PATH_ID + " from " + DatabaseHelper.TABLE_NAME
                + " where " + DatabaseHelper.COLUMN_PATH_FILE + " = \"" + filename + "\"";
        Cursor c = _DB.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() == 0) return 0;
        else return c.getInt(0);
    }

    public boolean isFileInDatabase(String fileName){
        String query = "select * from " + DatabaseHelper.TABLE_NAME + " where " + DatabaseHelper.COLUMN_PATH_FILE
                 + " = \"" + fileName + "\"";
        Cursor cursor = _DB.rawQuery(query, null);
        if (cursor.getCount() == 0) return false;
        else return true;
    }

    public void updateRowById(int updateId, PathDataContent.PathRecord pathRecord){
        String query = "select * from " + DatabaseHelper.TABLE_NAME + " where " + DatabaseHelper.COLUMN_PATH_ID
                 + " = " + updateId;
        Cursor cursor = _DB.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) return;
        checkAndUpdateIfNeeded(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_NAME)),
                pathRecord.name, DatabaseHelper.COLUMN_PATH_NAME, updateId);
        checkAndUpdateIfNeeded(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH_DESCR)),
                pathRecord.description, DatabaseHelper.COLUMN_PATH_DESCR, updateId);
        //think if it's possible to change start path date
    }

    private void checkAndUpdateIfNeeded(String oldString, String newString, String colName, int id){
        if (!oldString.equals(newString)) _DB.execSQL("update " + DatabaseHelper.TABLE_NAME + " set "
                + colName + " = \"" + newString + "\" " + " where " + DatabaseHelper.COLUMN_PATH_ID + " = " + id);

    }

    public boolean isEmptyDatabase(){
        Cursor c = getAllData();
        if (c.getCount() == 0) return true;
        else return false;
    }
}
