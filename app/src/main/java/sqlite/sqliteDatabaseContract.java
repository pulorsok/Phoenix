package sqlite;

import android.provider.BaseColumns;

import java.lang.reflect.Array;

/**
 *   Sqlite Data parameter
 */
public class sqliteDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.

    public sqliteDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract  class USER_SESNSOR implements BaseColumns{
        public static final String TABLE = "SENSOR";
        public static final String SENSOR = "sensor";
    }
    public static abstract  class USER_TAG implements BaseColumns{
        public static final String TABLE = "TAG";
        public static final String TAG = "tag";
    }
    public static abstract  class SENSOR_TAG implements BaseColumns{
        public static final String TABLE = "SENSOR_TAG_RELATION";
        public static final String SENSOR = "sensor";
        public static final String TAG = "tag";
    }

    /**
     *  Sql query of select condition
     */
    public static abstract  class SelectConditionQurey{

        /**
         *     ' WHERE sensorColumn = name '
         * @param sensorColumn
         * @param name
         * @return
         */
        public static String tagOrderFromSensor(String sensorColumn, String name){
            String s = sensorColumn + "=" + "'" + name + "'";
            return s;
        }


    }

}
