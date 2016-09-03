package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import sqlite.sqliteDatabaseContract.*;
/**
 *   SQLite Initial class
 */
public class sqliteDatabase extends SQLiteOpenHelper {

    // Authentication user name init
    public static String USER_NAME = null;

    // Database name
    public final static String DATABASE_NAME = "pundo";

    // DataBase version
    public final static int DATABASE_VERSION = 3;

    // Sql syntax parameter
    private static final String DATE = " DATE";
    private static final String TEXT = " TEXT";
    private static final String INT = " INTEGER";
    private static final String COM = ",";

    // Create sensor table from user
    private static final String SQL_CREATE_USER_SENSOR =
            "CREATE TABLE " + USER_SESNSOR.TABLE + "(" +
                    USER_SESNSOR._ID + " INTEGER PRIMARY KEY," +
                    USER_SESNSOR.SENSOR + TEXT +
                    " )";

    // Create tag table from user
    private static final String SQL_CREATE_USER_TAG =
            "CREATE TABLE " + USER_TAG.TABLE + " (" +
                    USER_TAG._ID + " INTEGER PRIMARY KEY," +
                    USER_TAG.TAG + TEXT +
                    " )";

    // Create relation table with tag and sensor
    private static final String SQL_CREATE_SENSOR_TAG_RELATION =
            "CREATE TABLE " + SENSOR_TAG.TABLE + " (" +
                    SENSOR_TAG._ID + " INTEGER PRIMARY KEY," +
                    SENSOR_TAG.SENSOR + TEXT + COM +
                    SENSOR_TAG.TAG + TEXT +
                    " )";


    /**
     *  Constructor
     * @param context
     * @param user  Authentication name of user
     */
    public sqliteDatabase(Context context,String user){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        USER_NAME = user;
        Log.v("sqliteDataBase:" , user);
    }

    /**
     *  This function will call while app install in mobile
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("DataBase Init","DatabAse init");
        db.execSQL(SQL_CREATE_USER_TAG);
        db.execSQL(SQL_CREATE_USER_SENSOR);
        db.execSQL(SQL_CREATE_SENSOR_TAG_RELATION);
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     *
     * @param json Initial data from server when app create
     */
    public void DataBaseInit(JSONObject json){
        try {

            JSONArray tagArray = json.getJSONArray("tag");
            JSONArray sensorArray = json.getJSONArray("sensor");
            ContentValues tag = new ContentValues();
            ContentValues sensor = new ContentValues();
            for(int i = 0 ; i < tagArray.length() ; i++){
                tag.put("tag",tagArray.getString(i));
                if (getCount(USER_TAG.TABLE,USER_TAG.TAG,tagArray.getString(i)))
                    getWritableDatabase().insert(USER_TAG.TABLE,null,tag);


            }
            for(int i = 0 ; i < sensorArray.length() ; i++){
                sensor.put("sensor",sensorArray.getString(i));
                if (getCount(USER_SESNSOR.TABLE,USER_SESNSOR.SENSOR,sensorArray.getString(i)))
                    getWritableDatabase().insert(USER_SESNSOR.TABLE,null,sensor);


            }
        sensor.clear();
        tag.clear();

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void InsertSensorTagrelation(JSONArray json){
        try {

            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i); // Parse sensor of json
                Iterator iterator = obj.keys(); // get sensor keys access

                if(iterator.hasNext()) {
                    String sensorName  = (String) iterator.next(); // trans access to string
                    JSONArray tags = obj.getJSONArray(sensorName); // tag values
                    for (int j = 0 ; j < tags.length() ; j++){
                        ContentValues values = new ContentValues();
                        values.put("sensor", sensorName);
                        values.put("tag", tags.getString(j));
                        if (getCount(SENSOR_TAG.TABLE, SENSOR_TAG.TAG, tags.getString(j),SENSOR_TAG.SENSOR, sensorName))
                            getWritableDatabase().insert(SENSOR_TAG.TABLE,null,values);
                        Log.v("json keys",sensorName);
                        Log.v("json tag", tags.getString(j));
                        Log.v("json tagname",values.toString());
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public boolean getCount(String tableName, String columnName, String name, String columnName1, String name1){
        Cursor c = null;
        try {
            String query = "select " + columnName + " from " + tableName +
                    " where " + columnName + " = " + "'" + name + "'" + " AND " + columnName1 + " = " + "'" + name1 + "'";
            c = getReadableDatabase().rawQuery(query,null);

            if (c.moveToFirst())
                return false;
            return true;
        }
        finally {
            if (c != null)
                c.close();
        }
    }
    public boolean getCount(String tableName, String columnName, String name ) {
        Cursor c = null;
        try {
            String query = "select " + columnName + " from " + tableName + " where " + columnName + " = " + "'" + name + "'";
            c = getReadableDatabase().rawQuery(query,null);

            if (c.moveToFirst())
                return false;
            return true;
        }
        finally {
            if (c != null)
                c.close();
        }
    }



    /**
     *  Select all data from tableName
     * @param tableName
     * @return
     */
    public Cursor SelectTable(String tableName){
        return  getReadableDatabase().rawQuery("SELECT * FROM " + tableName , null);
    }

    /**
     *  Select columnName data from tableName without condition
     * @param tableName
     * @param columnName
     * @return
     */
    public Cursor SelectTable(String tableName,String columnName){
        return  getReadableDatabase().rawQuery("SELECT " + columnName + " FROM " + tableName , null);
    }

    /**
     *  Select columnName data from tableName with condition
     * @param tableName
     * @param condition
     * @param columnName
     * @return
     */
    public Cursor SelectTable(String tableName,String columnName,String condition){
        return  getReadableDatabase().rawQuery("SELECT " + columnName + " FROM " + tableName + " WHERE " + condition , null );
    }

}
