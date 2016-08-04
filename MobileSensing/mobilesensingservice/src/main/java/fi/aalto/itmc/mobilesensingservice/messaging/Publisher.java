package fi.aalto.itmc.mobilesensingservice.messaging;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by laptop on 4/15/16.
 */
public interface Publisher {
    void publish(byte[] payload) throws MqttException;
}
