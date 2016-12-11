package com.aut.yuxiang.lbs_middleware.db;

import android.provider.BaseColumns;

import static com.aut.yuxiang.lbs_middleware.db.SQL.AccelerometerReadingsTable.ACCELEROMETER_READINGS_TABLE_NAME;
import static com.aut.yuxiang.lbs_middleware.db.SQL.AccelerometerReadingsTable.TIME_STAMP;
import static com.aut.yuxiang.lbs_middleware.db.SQL.AccelerometerReadingsTable.X_AXIS;
import static com.aut.yuxiang.lbs_middleware.db.SQL.AccelerometerReadingsTable.Y_AXIS;
import static com.aut.yuxiang.lbs_middleware.db.SQL.AccelerometerReadingsTable.Z_AXIS;

/**
 * Created by yuxiang on 9/12/16.
 */

public class SQL {
    public static final String REAL_TYPE = "REAL";
    public static final String INT_TYPE = "INTEGER";
    public static final String TEXT_TYPE = "TEXT";
    public static final String CREATE_TABLE_ACCELEROMETER_READINGS = "CREATE TABLE "+ACCELEROMETER_READINGS_TABLE_NAME+"("+AccelerometerReadingsTable._ID+" "+ INT_TYPE+", "+
            X_AXIS+" "+ REAL_TYPE+", "+
            Y_AXIS+" "+ REAL_TYPE+", "+
            Z_AXIS+" "+ REAL_TYPE+", "+
            TIME_STAMP+" "+ INT_TYPE+
            ")";
    public static final String DROP_TABLE_ACCELEROMETER_READINGS = "DROP TABLE "+ AccelerometerReadingsTable.ACCELEROMETER_READINGS_TABLE_NAME;

    class AccelerometerReadingsTable implements BaseColumns
    {
        public static final String ACCELEROMETER_READINGS_TABLE_NAME = "accelerometer_readings";
        public static final String X_AXIS = "x_axis";
        public static final String Y_AXIS = "y_axis";
        public static final String Z_AXIS = "z_axis";
        public static final String TIME_STAMP = "time_stamp";
    }
}
