package fi.aalto.itmc.mobilesensingservice.messaging;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;

import java.util.List;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;
import fi.aalto.itmc.mobilesensingservice.sqlite.SensorDataDB;

/**
 * Created by laptop on 4/28/16.
 */
public class MessageSender {
    static public void sendSensorData(Context context, Publisher publisher) {
        long before = MobileSensingCommon.getTimeNow();
        long after = 0;
        SensorDataDB db = SensorDataDB.getInstance(context);
        while (true) {
            List<String> messages = db.messagesBeforeAfter(before, after);
            if (messages.isEmpty()) {
                Log.d("Message sender", "Nothing to send");
                return;
            }
            JSONArray jsonArray = JSONFormatter.parseListToJSONArray(messages);
            String bundle = JSONFormatter.sensingBundle(jsonArray).toString();
            after = JSONFormatter.parseLastTimestampFromString(bundle);
            try {
                publisher.publish(bundle.getBytes());
            } catch (MqttException e) {
                Log.e("Message sender", "Sending failed", e);
            }
        }

    }
}
