package sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import sqlite.sqliteDatabaseContract.USER;
import sqlite.sqliteDatabaseContract.SENSOR;
import sqlite.sqliteDatabaseContract.TAG;

/**
 * Created by student.cce on 2016/8/22.
 */
public class sqliteDatabase extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "user name";
    public final static int DATABASE_VERSION = 1;


    private static final String TEXT = " TEXT";
    private static final String INT = " INTEGER";
    private static final String COM = ",";
    private static final String SQL_CREATE_USER_COLLECTION =
            "CREATE TABLE " + USER.COLLECTION + " (" +
                    USER._ID + " INTEGER PRIMARY KEY," +
                    USER.USER_NAME + TEXT + COM +
                    USER.USER_PASSWORD + TEXT + COM +
                    USER.SENSOR + TEXT + COM +
                    USER.TAG + TEXT + COM +
                    USER.SENSOR_COUNT + INT + COM +
                    USER.TAG_COUNT + INT + COM +
            " )";
    private static final String SQL_CREATE_SENSOR_COLLECTION =
            "CREATE TABLE" + SENSOR.COLLECTION + " (" +
                    SENSOR._ID + " INTEGER PRIMARY KEY," +
                    SENSOR.SENSOR_NAME + TEXT + COM +
                    SENSOR.ORDER + TEXT + COM +
                    SENSOR.DATE + "DATE" + COM +
                    SENSOR.LOCATION + TEXT + COM +
                    SENSOR.TAG + TEXT + COM +
                    SENSOR.TAG_COUNT + INT + COM +
            " )";
    private static final String SQL_CREATE_TAG_COLLECTION =
            "CREATE TABLE" + TAG.COLLECTION + " (" +
                    TAG._ID + " INTEGER PRIMARY KEY," +
                    TAG.TAG_NAME + TEXT + COM +
                    TAG.ORDER_USER + TEXT + COM +
                    TAG.LAST_DATE + TEXT + COM +
                    TAG.LOCATION + TEXT + COM +
            " )";
//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    public sqliteDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
