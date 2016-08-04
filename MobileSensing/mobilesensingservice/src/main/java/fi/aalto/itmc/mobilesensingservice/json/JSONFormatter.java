package fi.aalto.itmc.mobilesensingservice.json;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;

/**
 * Created by laptop on 4/23/16.
 */
public class JSONFormatter {

    private static final String TAG = "JSONFormatter";

    public static JSONArray parseListToJSONArray(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (String s : list) {
            try {
                jsonArray.put(new JSONObject(s));
            } catch (JSONException e) {
                return new JSONArray();
            }
        }
        return jsonArray;
    }

    public static JSONObject sensingMessage(List<JSONObject> jsonObjectList, long timestamp) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (JSONObject o : jsonObjectList) {
                if (o.length() == 0) {
                    continue;
                }
                String name = o.names().getString(0);
                Object value = o.get(name);
                jsonObject.put(name, value);
            }
            jsonObject.put(MobileSensingCommon.TIMESTAMP_FIELD_JSON, timestamp);
            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "sensingMessage creation failed", e);
        }
        return defaultValue();
    }

    public static JSONObject sensor1DMessage(String sensorName, float value) {
        try {
            return new JSONObject()
                    .put(sensorName, value);
        } catch (JSONException e) {
            Log.e(TAG, "sensor message creation failed", e);
        }
        return defaultValue();
    }

    public static JSONObject sensorLocationMessage(String sensorName, Location location) {
        if (location == null) {
            return defaultValue();
        }
        try {
            return new JSONObject()
                    .put(sensorName,
                            new JSONObject()
                                    .put(MobileSensingCommon.LONGITUDE_FIELD_JSON, location.getLongitude())
                                    .put(MobileSensingCommon.LATITUDE_FIELD_JSON, location.getLatitude())
                    );
        } catch (JSONException e) {
            Log.e(TAG, "location message creation failed", e);
        }
        return defaultValue();
    }

    public static JSONObject sensorWifiMessage(String sensorName, Map<String, Integer> wifiMap) {
        if (wifiMap == null) {
            return defaultValue();
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, Integer> entry : wifiMap.entrySet()) {
                jsonArray.put(new JSONObject().put(MobileSensingCommon.NAME_FIELD_JSON, entry.getKey()).put(MobileSensingCommon.VALUE_FIELD_JSON, entry.getValue()));
            }
            return new JSONObject()
                    .put(sensorName, jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "wifi message creation failed", e);
        }
        return defaultValue();
    }

    public static JSONObject sensingBundle(JSONArray jsonArray) {
        try {
            return new JSONObject()
                    .put(MobileSensingCommon.BUNDLE_FIELD_JSON, jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "sensingBundle creation failed", e);
        }
        return defaultValue();
    }

    public static long parseLastTimestampFromPayload(byte[] payload) {
        try {
            JSONObject jsonObject = new JSONObject(new String(payload));
            JSONArray jsonArray = jsonObject.getJSONArray(MobileSensingCommon.BUNDLE_FIELD_JSON);
            return ((JSONObject) jsonArray.get(jsonArray.length() - 1)).getLong(MobileSensingCommon.TIMESTAMP_FIELD_JSON);
        } catch (JSONException e) {
            //if message is not parsable in my format there is nothing to do
            return 0;
        }
    }

    private static JSONObject defaultValue() {
        return new JSONObject();
    }
}
