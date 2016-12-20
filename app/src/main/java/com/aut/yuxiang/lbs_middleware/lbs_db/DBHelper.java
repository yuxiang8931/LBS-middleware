package com.aut.yuxiang.lbs_middleware.lbs_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuxiang on 9/12/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LBS.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_ACCELEROMETER_READINGS);
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_GPS_READINGS);
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_CELL_TOWER_READINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL.DROP_TABLE_ACCELEROMETER_READINGS);
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_ACCELEROMETER_READINGS);

        sqLiteDatabase.execSQL(SQL.DROP_TABLE_GPS_READINGS);
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_GPS_READINGS);

        sqLiteDatabase.execSQL(SQL.DROP_TABLE_CELL_TOWER_READINGS);
        sqLiteDatabase.execSQL(SQL.CREATE_TABLE_CELL_TOWER_READINGS);
    }
}
