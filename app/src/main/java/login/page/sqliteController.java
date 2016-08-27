package login.page;

import android.app.DownloadManager;
import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import http.AuthenticationJSONAsyncTask;
import http.httpConnectInterface;
import http.mJsonHttpResponseHandler;
import in.srain.cube.request.RequestJsonHandler;
import sqlite.sqliteDatabase;
import sqlite.sqliteDatabaseContract;

/**
 * Created by student.cce on 2016/8/25.
 */
public class sqliteController {
    private RequestParams req = new RequestParams();
    private Context context;
    private String user;
    private static sqliteDatabase Database ;
    public sqliteController(Context context){
        this.context = context;
    }


    public void LoginInitial(String user){

        this.user = user;
        req.put("user",user);
        Database = new sqliteDatabase(context,user);


        AuthenticationJSONAsyncTask.get(httpConnectInterface.SQLITE_INIT, req, new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  super.onSuccess(statusCode, headers, response);

                  Database.DataBaseInit(response);
              }


        });
    }
    public String[] getSqliteSensor(String tableName){
        Cursor c = Database.SelectSensor(tableName);// Call database to select sensor and put the cursor back
        ArrayList<String> sensor = new ArrayList<String>();
        if(c.getCount()!=0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                sensor.add(c.getString(c.getColumnIndex(sqliteDatabaseContract.USER_SESNSOR.SENSOR)));
                c.moveToNext();
            }
        }
        c.close();
        return sensor.toArray(new String[sensor.size()]);
    }

}
