package fi.aalto.itmc.mobilesensingservice.messaging;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;

/**
 * Created by laptop on 4/15/16.
 */
public class MqttPublisher implements Publisher {
    private MqttAndroidClient mqttAndroidClient = null;
    private final String mDeviceID;

    public MqttPublisher(MqttAndroidClient mqttAndroidClient, String deviceID) {
        this.mqttAndroidClient = mqttAndroidClient;
        this.mDeviceID = deviceID;
    }

    @Override
    public void publish(byte[] payload) throws MqttException {
        if (mqttAndroidClient.isConnected()) {
            String targetTopic = MobileSensingCommon.MQTT_TOPIC_ROOT + "/" + mDeviceID + "/" + MobileSensingCommon.MQTT_TOPIC_NAME;
            mqttAndroidClient.publish(targetTopic, payload, MobileSensingCommon.QOS_MESSAGE_MQTT, MobileSensingCommon.RETAINED_MESSAGE_MQTT);
            Log.d("Publisher", "Publishing to topic: " + targetTopic + " value: " + new String(payload));
        } else {
            Log.d("Publisher", "MQTTclient is not connected, can not publish anything");
        }
    }
}
