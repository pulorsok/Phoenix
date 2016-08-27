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

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import sqlite.sqliteDatabaseContract.*;
/**
 *   SQLite Initial class
 */
public class sqliteDatabase extends SQLiteOpenHelper {
    public static String USER_NAME = null;
    public final static String DATABASE_NAME = "pundo";
    public final static int DATABASE_VERSION = 3;
    private static final String DATE = " DATE";
    private static final String TEXT = " TEXT";
    private static final String INT = " INTEGER";
    private static final String COM = ",";
    private static final String SQL_CREATE_USER_SENSOR =
            "CREATE TABLE " + USER_SESNSOR.TABLE + "(" +
                    USER_SESNSOR._ID + " INTEGER PRIMARY KEY," +
                    USER_SESNSOR.SENSOR + TEXT +
                    " )";
    private static final String SQL_CREATE_USER_TAG =
            "CREATE TABLE " + USER_TAG.TABLE + " (" +
                    USER_TAG._ID + " INTEGER PRIMARY KEY," +
                    USER_TAG.TAG + TEXT +
                    " )";

//    private static final String SQL_CREATE_USER_COLLECTION =
//            "CREATE TABLE " + USER.COLLECTION + " (" +
//                    USER._ID + " INTEGER PRIMARY KEY," +
//                    USER.USER_NAME + TEXT + COM +
//                    USER.USER_PASSWORD + TEXT + COM +
//                    USER.SENSOR + TEXT + COM +
//                    USER.TAG + TEXT + COM +
//                    USER.SENSOR_COUNT + INT + COM +
//                    USER.TAG_COUNT + INT +
//            " )";
//    private static final String SQL_CREATE_SENSOR_COLLECTION =
//            "CREATE TABLE " + SENSOR.COLLECTION + " (" +
//                    SENSOR._ID + " INTEGER PRIMARY KEY," +
//                    SENSOR.SENSOR_NAME + TEXT + COM +
//                    SENSOR.ORDER + TEXT + COM +
//                    SENSOR.DATE + DATE + COM +
//                    SENSOR.LOCATION + TEXT + COM +
//                    SENSOR.TAG + TEXT + COM +
//                    SENSOR.TAG_COUNT + INT +
//            " )";
//    private static final String SQL_CREATE_TAG_COLLECTION =
//            "CREATE TABLE " + TAG.COLLECTION + " (" +
//                    TAG._ID + " INTEGER PRIMARY KEY," +
//                    TAG.TAG_NAME + DATE + COM +
//                    TAG.ORDER_USER + TEXT + COM +
//                    TAG.LAST_DATE + TEXT + COM +
//                    TAG.LOCATION + TEXT +
//            " )";
//
//    private static final String SQL_CREATE_USER_TAG_RELATION =
//            "CREATE TABLE " + USER_TAG_RELATION.COLLECTION + " (" +
//                    USER_TAG_RELATION._ID +  " INTEGER PRIMARY KEY," +
//                    USER_TAG_RELATION.TAG_NAME + TEXT +
//
//            " )";
//    private static final String SQL_CREATE_USER_SENSOR_RELATION =
//            "CREATE TABLE " + USER_SENSOR_RELATION.COLLECTION + " (" +
//                    USER_SENSOR_RELATION._ID +  " INTEGER PRIMARY KEY," +
//                    USER_SENSOR_RELATION.SENSOR_NAME + TEXT +
//            " )" ;
//    private static final String SQL_CREATE_SENSOR_TAG_RELATION =
//            "CREATE TABLE " + SENSOR_TAG_RELATION.COLLECTION + " (" +
//                    SENSOR_TAG_RELATION._ID +  " INTEGER PRIMARY KEY," +
//                    SENSOR_TAG_RELATION.SENSOR_NAME + TEXT +
//                    SENSOR_TAG_RELATION.TAG_NAME + TEXT +
//            " )" ;
    public sqliteDatabase(Context context,String user){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        USER_NAME = user;
        Log.v("sqliteDataBase:" , user);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("DataBase Init","DatabAse init");
        db.execSQL(SQL_CREATE_USER_TAG);
        db.execSQL(SQL_CREATE_USER_SENSOR);
//        db.execSQL(SQL_CREATE_USER_COLLECTION);
//        db.execSQL(SQL_CREATE_SENSOR_COLLECTION);
//        db.execSQL(SQL_CREATE_TAG_COLLECTION);
//        db.execSQL(SQL_CREATE_SENSOR_TAG_RELATION);
//        db.execSQL(SQL_CREATE_USER_SENSOR_RELATION);
//        db.execSQL(SQL_CREATE_USER_TAG_RELATION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void DataBaseInit(JSONObject useraData){
        try {

            JSONArray tagArray = useraData.getJSONArray("tag");
            JSONArray sensorArray = useraData.getJSONArray("sensor");
            ContentValues tag = new ContentValues();
            ContentValues sensor = new ContentValues();
            for(int i = 0 ; i < tagArray.length() ; i++){
                tag.put("tag",tagArray.getString(i));
                getWritableDatabase().insert(USER_TAG.TABLE,null,tag);
            }
            for(int i = 0 ; i < sensorArray.length() ; i++){
                sensor.put("sensor",sensorArray.getString(i));
                getWritableDatabase().insert(USER_SESNSOR.TABLE,null,sensor);
            }



        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Cursor SelectSensor(String tableName){
       return  getReadableDatabase().rawQuery("SELECT * FROM " + tableName , null);
    }

//
//
//
//
//
//
//
//
//
//
//
//    /**
//     * Content value of SENSOR
//     * @param count
//     * @param tag
//     * @param date
//     * @param value 0: ORDER, 1: LOCATION, 2: SENSOR_NAME
//     * @return
//     */
//    private ContentValues sensorcreateContentValue(int count, JSONArray tag, Date date, String ... value){
//        ContentValues values = new ContentValues();
//
//        // date format
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        values.put(SENSOR.TAG_COUNT, count);
//        values.put(SENSOR.TAG, SENSOR_TAG_RELATION.COLLECTION);
//        values.put(SENSOR.DATE, sdf.format(date));
//        values.put(SENSOR.ORDER, value[0]);
//        values.put(SENSOR.LOCATION, value[1]);
//        values.put(SENSOR.SENSOR_NAME, value[2]);
//
//        ContentValues sensorTag = new ContentValues();
//        try {
//            for (int i = 0 ; i < tag.length() ; i++) {
//                sensorTag.put(SENSOR_TAG_RELATION.SENSOR_NAME, value[2]);
//                sensorTag.put(SENSOR_TAG_RELATION.TAG_NAME, tag.getString(i));
//                getWritableDatabase().insert(SENSOR_TAG_RELATION.COLLECTION,null,sensorTag);
//            }
//        }catch (JSONException e){
//            Log.d("data err","Sensor relation err");
//            e.printStackTrace();
//        }
//
//
//        return values;
//    }
//
//    /**
//     * Content value of USER
//     * @param tagCount
//     * @param sensorCount
//     * @param sensor
//     * @param tag
//     * @param value 0: USER_NAME, 1: USER_PASSWORD
//     * @return
//     */
//    private ContentValues usercreateContentValue(int tagCount, int sensorCount,JSONArray sensor,JSONArray tag,String ... value){
//        ContentValues values = new ContentValues();
//
//        values.put(USER.TAG_COUNT, tagCount);
//        values.put(USER.SENSOR_COUNT, sensorCount);
//        values.put(USER.SENSOR, USER_SENSOR_RELATION.COLLECTION);
//        values.put(USER.TAG, USER_TAG_RELATION.COLLECTION);
//        values.put(USER.USER_NAME, value[0]);
//        values.put(USER.USER_PASSWORD, value[1]);
//
//        ContentValues userSensor = new ContentValues();
//        ContentValues userTag = new ContentValues();
//        try {
//            for (int i = 0 ; i < sensor.length() ; i++) {
//                userSensor.put(USER_SENSOR_RELATION.SENSOR_NAME, sensor.getString(i));
//                getWritableDatabase().insert(USER_SENSOR_RELATION.COLLECTION, null, userSensor);
//            }
//            for (int i = 0 ; i < tag.length() ; i++) {
//                userTag.put(USER_TAG_RELATION.TAG_NAME, tag.getString(i));
//                getWritableDatabase().insert(USER_TAG_RELATION.COLLECTION,null,userTag);
//            }
//        }catch (JSONException e){
//            Log.d("data err","user relation err");
//            e.printStackTrace();
//        }
//
//
//
//
//        return values;
//    }
//
//    /**
//     * Content value of TAG
//     * @param date
//     * @param value 0: TAG_NAME, 1: ORDER_USER, 2: LOCATION
//     * @return
//     */
//    private ContentValues tagcreateContentValue(Date date, String ... value){
//        ContentValues values = new ContentValues();
//
//        // date format
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        values.put(TAG.LAST_DATE, sdf.format(date));
//        values.put(TAG.TAG_NAME, value[0]);
//        values.put(TAG.ORDER_USER, value[1]);
//        values.put(TAG.LOCATION, value[2]);
//
//        return values;
//    }

//
//    public void DataBaseInit(JSONArray user, JSONArray sensor,JSONArray tag){
//
//
//        /**
//         *  Parse sensor json
//         */
//
//        for (int i=0 ; i < sensor.length(); i++){
//            try{
//
//                // Parse JSON in JSON Array
//                JSONObject value = sensor.getJSONObject(i);
//
//                String date = value.getString("date");
//                // date format
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                // put json in content value that has been parsed
//                ContentValues values = sensorcreateContentValue(
//                        value.getInt("tag_count"),
//                        value.getJSONArray("tag"),
//                        sdf.parse(date),
//                        value.getString("order"),
//                        value.getString("location"),
//                        value.getString("sensor_name")
//                        );
//
//
//                getWritableDatabase().insert(SENSOR.COLLECTION,null,values);
//            }catch (JSONException e){
//                e.printStackTrace();
//            }catch (ParseException e){
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         *  Parse user json
//         */
//
//        for (int i=0 ; i < user.length(); i++){
//            try{
//
//                // Parse JSON in JSON Array
//                JSONObject value = user.getJSONObject(i);
//
//                // put json in content value that has been parsed
//                ContentValues values = usercreateContentValue(
//                        value.getInt("tag_count"),
//                        value.getInt("sensor_count"),
//                        value.getJSONArray("sensor"),
//                        value.getJSONArray("tag"),
//                        value.getString("user_name"),
//                        value.getString("user_password")
//                );
//
//
//                getWritableDatabase().insert(USER.COLLECTION,null,values);
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         *  Parse tag json
//         */
//
//        for (int i=0 ; i < tag.length(); i++){
//            try{
//
//                // Parse JSON in JSON Array
//                JSONObject value = tag.getJSONObject(i);
//
//                String date = value.getString("date");
//                // date format
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                // put json in content value that has been parsed
//                ContentValues values = tagcreateContentValue(
//                        sdf.parse(date),
//                        value.getString("tag_name"),
//                        value.getString("order_user"),
//                        value.getString("location")
//                );
//
//
//                getWritableDatabase().insert(SENSOR.COLLECTION,null,values);
//            }catch (JSONException e){
//                e.printStackTrace();
//            }catch (ParseException e){
//                e.printStackTrace();
//            }
//        }
//
//    }
}
