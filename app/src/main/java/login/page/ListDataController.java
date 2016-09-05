package login.page;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;
import http.AuthenticationJSONAsyncTask;
import http.httpConnectInterface;
import sqlite.sqliteDatabase;


/**
 * Created by student.cce on 2016/8/25.
 */
public class ListDataController implements httpConnectInterface {
    private RequestParams req = new RequestParams();
    private Context context;
    private String user;
    private static sqliteDatabase Database ;

    public ListDataController(Context context){
        this.context = context;
    }

    /**
     *     Initial user tag, sensor data after login
     * @param user authentication user name
     */
    public void LoginInitial(String user){

        this.user = user;
        req.put("user",user);

        Database = new sqliteDatabase(context,user);


        AuthenticationJSONAsyncTask.get(SQLITE_INIT, req, new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  super.onSuccess(statusCode, headers, response);
                  Database.DataBaseInit(response);
              }


        });
    }

    public void GetSensorTagRelation(){

        req.put("user",user);
        AuthenticationJSONAsyncTask.get(SQLITE_SEN_TAG_REL, req, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Database.InsertSensorTagrelation(response);
            }
        });
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
}
