package http;

/**
 *   Http connect parameter
 */
public interface httpConnectInterface {
    String BASE_URL= "http://163.18.44.131:3000";
    String GET_SENSOR = "/dataRouter/get.Sensor-User";
    String GET_TAG = "/dataRouter/get.Tag-User";
    String GET_USER = "/dataRouter/get.user";
    String SQLITE_INIT = "/dataRouter";
    String SQLITE_SEN_TAG_REL = "/dataRouter/get.SensorTagRelation";
    String SQLITE_HISTORY = "/dataRouter/get.History";
}
