package fi.aalto.itmc.mobilesensingservice.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingservice.messaging.MessageSender;
import fi.aalto.itmc.mobilesensingservice.messaging.Publisher;

/**
 * Created by laptop on 4/16/16.
 */
public class ConnectListener implements IMqttActionListener {
    private Context mContext = null;
    private Publisher mPublisher = null;

    public ConnectListener(Context mContext, Publisher publisher) {
        this.mContext = mContext;
        this.mPublisher = publisher;
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        try {
            iMqttToken.getClient().publish(MobileSensingCommon.getDeviceID(mContext) + "/" + MobileSensingCommon.TOPIC_STATUS_MQTT,
                    MobileSensingCommon.ONLINE_MESSAGE_MQTT.getBytes(), MobileSensingCommon.QOS_STATUS_LWT_MQTT, MobileSensingCommon.RETAINED_MESSAGE_MQTT);
            MessageSender.sendSensorData(mContext, mPublisher);
        } catch (MqttException e) {
            Log.e("ConnectListener", "Connection hook failed publishing online status");
        }
        Log.d("ConenctListener", "Connected successfully to broker");
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.e("ConnectListener", "Connection hook failed connecting", throwable);
    }
}
