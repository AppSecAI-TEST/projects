package fi.aalto.itmc.mobilesensingservice.mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;

/**
 * Created by laptop on 4/15/16.
 */
public class MQTTConnectionFactory {

    final private String mDeviceID;
    final private Context mContext;

    public MQTTConnectionFactory(Context context, String deviceID) {
        this.mContext = context;
        this.mDeviceID = deviceID;
    }

    public MqttAndroidClient getClientConnectionDefault() {

        MqttAndroidClient client = new MqttAndroidClient(mContext, MobileSensingCommon.FULL_URI_SERVER_MQTT, mDeviceID);
        client.setCallback(new MqttCallbackHandler(mContext));
        client.setTraceCallback(new MqttTraceCallback());

        return client;
    }

    public MqttConnectOptions getConnectOptionsDefault() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(MobileSensingCommon.CLEAN_SESSION_MQTT);
        mqttConnectOptions.setConnectionTimeout(MobileSensingCommon.CONNECTION_TIMEOUT_MQTT);
        mqttConnectOptions.setKeepAliveInterval(MobileSensingCommon.KEEP_ALIVE_INTERVAL_MQTT);

        mqttConnectOptions.setWill(mDeviceID + "/" + MobileSensingCommon.TOPIC_STATUS_MQTT, MobileSensingCommon.OFFLINE_MESSAGE_MQTT.getBytes(),
                MobileSensingCommon.QOS_STATUS_LWT_MQTT, MobileSensingCommon.RETAINED_MESSAGE_LWT_MQTT);

        return mqttConnectOptions;
    }
}
