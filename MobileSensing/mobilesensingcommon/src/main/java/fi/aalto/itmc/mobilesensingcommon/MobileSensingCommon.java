package fi.aalto.itmc.mobilesensingcommon;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by laptop on 4/13/16.
 */
public class MobileSensingCommon {

    static public final int START_SENSING_MSG = 1;

    static public final int STOP_SENSING_MSG = 2;

    static public final int STATUS_SENSING_MSG = 3;

    static public final int STATUS_DATA_MSG = 4;

    static public final int RECONNECT_AND_SEND_MSG = 5;

    /*10 seconds */
    static public final int MAX_SAMPLE_INTERVAL_MS = 10000000;

    static public final int SENSOR_SAMPLE_RATE_DEFAULT_S = 5;

    static public final int MSG_SENDING_RATE_DEFAULT_S = 30;

    /*JSON consts*/
    static public final String NAME_FIELD_JSON = "Name";

    static public final String VALUE_FIELD_JSON = "Value";

    static public final String TIMESTAMP_FIELD_JSON = "Timestamp";

    static public final String SENSORS_FIELD_JSON = "Sensors";

    static public final String BUNDLE_FIELD_JSON = "Data";

    static public final String LONGITUDE_FIELD_JSON = "Longitude";

    static public final String LATITUDE_FIELD_JSON = "Latitude";

    static public final String ACTIVE_SENSORS_BUNDLE_NAME = "sensors_bundle";

    static private final String URI_SERVER_MQTT = "10.42.0.1";
//    static private final String URI_SERVER_MQTT = "iot.tecnote.net";

    static private final String PROTOCOL_SERVER_MQTT = "tcp://";

    static private final int PORT_SERVER_MQTT = 1883;

    static public final String FULL_URI_SERVER_MQTT = PROTOCOL_SERVER_MQTT + URI_SERVER_MQTT + ":" + PORT_SERVER_MQTT;

    /* 2 is exactly once and needed in our case*/
    static public final int QOS_MESSAGE_MQTT = 2;

    static public final String MQTT_TOPIC_ROOT = "sensing";

    static public final String MQTT_TOPIC_NAME = "data";

    static public final boolean RETAINED_MESSAGE_MQTT = false;

    static public final boolean CLEAN_SESSION_MQTT = true;

    static public final int CONNECTION_TIMEOUT_MQTT = 30;

    static public final int KEEP_ALIVE_INTERVAL_MQTT = 60;

    /*LWT section*/
    static public final String OFFLINE_MESSAGE_MQTT = "offline";

    static public final String ONLINE_MESSAGE_MQTT = "online";

    static public final String TOPIC_STATUS_MQTT = "status";

    static public final int QOS_STATUS_LWT_MQTT = 2;

    static public final boolean RETAINED_MESSAGE_LWT_MQTT = false;

    /*Location statics*/
    static public final int UPDATE_INTERVAL_MS_LOCATION = 1000;

    static public final int FRESHNESS_MS_LOCATION = 10000;

    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static long getTimeNow() {
        return System.currentTimeMillis();
    }

}
