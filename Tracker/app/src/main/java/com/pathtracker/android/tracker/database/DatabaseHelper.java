package com.pathtracker.android.tracker.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    static final String TABLE_NAME = "PathTable";
    static final String COLUMN_PATH_ID = "_id";
    static final String COLUMN_PATH_FILE = "filePath";
    static final String COLUMN_PATH_NAME = "name";
    static final String COLUMN_PATH_DESCR = "description";
    static final String COLUMN_PATH_DATE = "date";
    static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_PATH_ID + " integer primary key autoincrement,"
            + COLUMN_PATH_NAME + " VARCHAR(50),"
            + COLUMN_PATH_DESCR + " VARCHAR(500),"
            + COLUMN_PATH_FILE + " text,"
            + COLUMN_PATH_DATE + " text"
            + ");";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //method is used for moving DB if it needs to be updated;
    }
}
