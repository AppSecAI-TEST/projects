package fi.aalto.itmc.mobilesensingservice.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;
import fi.aalto.itmc.mobilesensingservice.sqlite.SensorDataDB;

/**
 * Created by laptop on 4/15/16.
 */
public class MqttCallbackHandler implements MqttCallback {

    private final SensorDataDB mDB;

    public MqttCallbackHandler(Context context) {
        this.mDB = SensorDataDB.getInstance(context);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d("MqttCallbackHandler", "connectionLost");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            long timestamp = JSONFormatter.parseLastTimestampFromPayload(iMqttDeliveryToken.getMessage().getPayload());
            mDB.deleteMessagesBeforeTimestamp(timestamp);
        } catch (MqttException e) {
            Log.e("MqttCallbackHandler", "On success delivery parsing message failed", e);
        }
    }
}
