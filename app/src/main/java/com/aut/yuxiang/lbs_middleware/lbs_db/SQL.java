package com.aut.yuxiang.lbs_middleware.lbs_db;

import android.provider.BaseColumns;

import static com.aut.yuxiang.lbs_middleware.lbs_db.SQL.AccelerometerReadingsTable.ACCELEROMETER_READINGS_TABLE_NAME;


/**
 * Created by yuxiang on 9/12/16.
 */

public class SQL {
    public static final String REAL_TYPE = "REAL";
    public static final String INT_TYPE = "INTEGER";
    public static final String TEXT_TYPE = "TEXT";
    public static final String CREATE_TABLE_ACCELEROMETER_READINGS = "CREATE TABLE " + ACCELEROMETER_READINGS_TABLE_NAME +
            "(" + AccelerometerReadingsTable._ID + " " + INT_TYPE + ", " +
            AccelerometerReadingsTable.X_AXIS + " " + REAL_TYPE + ", " +
            AccelerometerReadingsTable.Y_AXIS + " " + REAL_TYPE + ", " +
            AccelerometerReadingsTable.Z_AXIS + " " + REAL_TYPE + ", " +
            AccelerometerReadingsTable.TIME_STAMP + " " + INT_TYPE +
            ")";
    public static final String DROP_TABLE_ACCELEROMETER_READINGS = "DROP TABLE " + ACCELEROMETER_READINGS_TABLE_NAME;

    public static final String CREATE_TABLE_GPS_READINGS = "CREATE TABLE " + GPSReadingsTable.GPS_READINGS_TABLE_NAME +
            "(" + GPSReadingsTable._ID + " " + INT_TYPE + ", " +
            GPSReadingsTable.ALTITUDE + " " + REAL_TYPE + ", " +
            GPSReadingsTable.LATITUDE + " " + REAL_TYPE + ", " +
            GPSReadingsTable.LONGITUDE + " " + REAL_TYPE + ", " +
            GPSReadingsTable.ACCURACY + " " + REAL_TYPE + ", " +
            GPSReadingsTable.TIME_STAMP + " " + INT_TYPE +
            ")";
    public static final String DROP_TABLE_GPS_READINGS = "DROP TABLE" + GPSReadingsTable.GPS_READINGS_TABLE_NAME;

    public static final String CREATE_TABLE_CELL_TOWER_READINGS = "CREATE TABLE " + CellTowerReadingsTable.CELL_TOWER_READINGS_TABLE_NAME +
            "(" + CellTowerReadingsTable._ID + " " + INT_TYPE + ", " +
            CellTowerReadingsTable.ALTITUDE + " " + REAL_TYPE + ", " +
            CellTowerReadingsTable.LATITUDE + " " + REAL_TYPE + ", " +
            CellTowerReadingsTable.LONGITUDE + " " + REAL_TYPE + ", " +
            CellTowerReadingsTable.ACCURACY + " " + REAL_TYPE + ", " +
            CellTowerReadingsTable.TIME_STAMP + " " + INT_TYPE +
            ")";
    public static final String DROP_TABLE_CELL_TOWER_READINGS = "DROP TABLE" + CellTowerReadingsTable.CELL_TOWER_READINGS_TABLE_NAME;

    public class AccelerometerReadingsTable implements BaseColumns {
        public static final String ACCELEROMETER_READINGS_TABLE_NAME = "accelerometer_readings";
        public static final String X_AXIS = "x_axis";
        public static final String Y_AXIS = "y_axis";
        public static final String Z_AXIS = "z_axis";
        public static final String TIME_STAMP = "time_stamp";
    }

    public class GPSReadingsTable implements BaseColumns {
        public static final String GPS_READINGS_TABLE_NAME = "gps_readings";
        public static final String ALTITUDE = "altitude";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ACCURACY = "accuracy";
        public static final String TIME_STAMP = "time_stamp";
    }

    public class CellTowerReadingsTable implements BaseColumns
    {
        public static final String CELL_TOWER_READINGS_TABLE_NAME = "cell_tower_readings";
        public static final String ALTITUDE = "altitude";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ACCURACY = "accuracy";
        public static final String TIME_STAMP = "time_stamp";
    }
}
