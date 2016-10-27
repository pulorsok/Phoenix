package dataController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import gcm.MyGcmListenerService;
import http.AuthenticationJSONAsyncTask;
import http.httpConnectInterface;
import listPage.ListPage;
import login.page.LoginActivity;
import sqlite.sqliteDatabase;
import sqlite.sqliteDatabaseContract;


/**
 * Created by student.cce on 2016/8/25.
 */
public class ListDataController implements httpConnectInterface {
    private RequestParams req = new RequestParams();
    private Context context;
    private String user;
    private static sqliteDatabase Database ;
    private SharedPreferences settings;
    public static String[] array ;
    public ListDataController(Context context){
        this.context = context;
    }




    public void setRemind(String item,String action){
        req.put("remindItem",item);
        req.put("action",action);
        AuthenticationJSONAsyncTask.get("/dataRouter/setRemind", req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getInt("result") == 1)
                        Log.v("setRemind", "succeed");
                }catch (Exception e){
                    e.printStackTrace();
                }
                getRemind();
            }
        });

    }
    public void getRemind(){
        req.put("user",user);

        AuthenticationJSONAsyncTask.get("/dataRouter/GetRemind", req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String a[] = new String[response.length()];
                    for (int i = 0; i < response.length(); i++)
                        a[i] = response.getString(i);
                    array = a;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
                Intent in = new Intent("gg");
                in.putExtra("remind", "ok");
                broadcaster.sendBroadcast(in);
            }
        });

    }
    public void CheckRain(){
        AuthenticationJSONAsyncTask.get("/dataRouter/getWeather", req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("rain") == 1){
                        Log.d("Check","succeed");
                        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
                        Intent in = new Intent("gg");
                        in.putExtra(MyGcmListenerService.COPA_MESSAGE, "rain");
                        broadcaster.sendBroadcast(in);
                    }else{
                        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
                        Intent in = new Intent("gg");
                        in.putExtra(MyGcmListenerService.COPA_MESSAGE, "noRain");
                        broadcaster.sendBroadcast(in);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }
        });
    }
    public void CheckPM(){
        AuthenticationJSONAsyncTask.get("/dataRouter/getPM", req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("rain") == 1){
                        Log.d("Check","succeed");
                        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
                        Intent in = new Intent("gg");
                        in.putExtra(MyGcmListenerService.COPA_MESSAGE, "pm");
                        broadcaster.sendBroadcast(in);
                    }else{
                        Log.d("Check","succeed");
                        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);
                        Intent in = new Intent("gg");
                        in.putExtra(MyGcmListenerService.COPA_MESSAGE, "noPm");
                        broadcaster.sendBroadcast(in);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }
        });
    }



    /**
     *     Initial user tag, sensor data after login
     * @param user authentication user name
     */
    public void LoginInitial(String user){

        this.user = user;
        req.put("user",user);

        Database = new sqliteDatabase(context,user);
        requestHistory();

        AuthenticationJSONAsyncTask.get(SQLITE_INIT, req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //new Thread(runnable).interrupt();
                Database.DataBaseInit(response);
                super.onSuccess(statusCode, headers, response);
            }
        });
    }
    public void sendToken(String token){
        req.put("token",token);
        AuthenticationJSONAsyncTask.get("/dataRouter/test", req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //Database.DataBaseInit(response);
            }


        });
    }
    public void Update(){

        req.put("user",user);
        AuthenticationJSONAsyncTask.get(SQLITE_INIT, req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Database.DataBaseInit(response);
                super.onSuccess(statusCode, headers, response);
            }


        });
    }
    public void requestSensorTagRelation(){

        req.put("user",user);
        AuthenticationJSONAsyncTask.get(SQLITE_SEN_TAG_REL, req, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Database.InsertSensorTagRelation(response);
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    public void requestHistory(){
        req.put("user", user);
        AuthenticationJSONAsyncTask.get(SQLITE_HISTORY, req, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Database.InsertHistory(response);
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    public Cursor getHistory(){
        return Database.SelectTable(sqliteDatabaseContract.HISTORY.TABLE);
    }
    /**
     *    Select sqlite data
     * @param tableName select table name
     * @param columnName select column name
     * @return  String array
     */

    public String[] getSqliteSelect(String tableName,String columnName){

        // Call database to select sensor and put the cursor back
        Cursor c = Database.SelectTable(tableName);

        ArrayList<String> sensor = new ArrayList<String>();

        if(c.getCount()!=0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                sensor.add(c.getString(c.getColumnIndex(columnName)));
                c.moveToNext();
            }
        }

        c.close();
        return sensor.toArray(new String[sensor.size()]);
    }

    public String[] getSqliteSelect(String tableName,String columnName,String condition){
        // Call database to select sensor and put the cursor back
        Cursor c = Database.SelectTable(tableName,columnName,condition);

        ArrayList<String> sensor = new ArrayList<String>();

        if(c.getCount()!=0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                sensor.add(c.getString(c.getColumnIndex(columnName)));
                c.moveToNext();
            }
        }

        c.close();
        return sensor.toArray(new String[sensor.size()]);
    }
    /**
     * DataBase Close
     */

    public void cleanDB(){
        Database.getWritableDatabase().execSQL("delete from "+ sqliteDatabaseContract.HISTORY.TABLE);
        Database.getWritableDatabase().execSQL("delete from "+ sqliteDatabaseContract.SENSOR_TAG.TABLE);
        Database.getWritableDatabase().execSQL("delete from "+ sqliteDatabaseContract.USER_SESNSOR.TABLE);
        Database.getWritableDatabase().execSQL("delete from "+ sqliteDatabaseContract.USER_TAG.TABLE);
    }
}
