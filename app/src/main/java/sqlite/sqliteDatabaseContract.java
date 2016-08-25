package sqlite;

import android.provider.BaseColumns;

import java.lang.reflect.Array;

/**
 * Created by student.cce on 2016/8/22.
 */
public class sqliteDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.

    public sqliteDatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class USER implements BaseColumns {
        public static final String COLLECTION = "user";

        public static final String USER_NAME = "user_name";
        public static final String USER_PASSWORD = "user_password";
        public static final String SENSOR = "sensor";
        public static final String TAG = "tag";
        public static final String SENSOR_COUNT = "sensor_count";
        public static final String TAG_COUNT = "tag_count";

    }
    public static abstract class SENSOR implements BaseColumns {
        public static final String COLLECTION = "sensor";

        public static final String SENSOR_NAME = "sensor_name";
        public static final String ORDER = "order";
        public static final String DATE = "date";
        public static final String LOCATION = "location";
        public static final String TAG = "tag";
        public static final String TAG_COUNT = "tag_count";

    }
    public static abstract  class TAG implements BaseColumns {
        public static final String COLLECTION = "tag";

        public static final String TAG_NAME = "tag_name";
        public static final String ORDER_USER = "order_user";
        public static final String LAST_DATE = "last_date";
        public static final String LOCATION = "location";


    }
    public static abstract class USER_TAG_RELATION implements BaseColumns {
        public static final String COLLECTION = "user_tag_relation";

        public static final String USER_ID = "user_id";
        public static final String TAG_ID = "tag_id";

    }
    public static abstract class USER_SENSOR_RELATION implements BaseColumns {
        public static final String COLLECTION = "user_sensor_relation";

        public static final String USER_ID = "user_id";
        public static final String SENSOR_ID = "sensor_id";

    }
    public static abstract class SENSOR_TAG_RELATION implements BaseColumns {
        public static final String COLLECTION = "sensor_tag_relation";

        public static final String SENSOR_ID = "sensor_id";
        public static final String TAG_ID = "tag_id";

    }
}
