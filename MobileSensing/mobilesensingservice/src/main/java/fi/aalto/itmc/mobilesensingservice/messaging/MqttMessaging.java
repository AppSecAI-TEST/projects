package fi.aalto.itmc.mobilesensingservice.messaging;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingservice.mqtt.ConnectListener;
import fi.aalto.itmc.mobilesensingservice.mqtt.MQTTConnectionFactory;

/**
 * Created by laptop on 4/15/16.
 */
public class MqttMessaging implements Messaging {
    private final String TAG = "MqttMessaging";

    private final Context mContext;
    private MQTTConnectionFactory mqttConnectionFactory = null;
    private MqttAndroidClient mqttAndroidClient = null;
    private MqttConnectOptions mqttConnectOptions = null;
    private final String mDeviceID;
    private Publisher mPublisher = null;
    private ConnectListener mCallback = null;

    public MqttMessaging(Context context) {
        this.mContext = context;
        this.mDeviceID = MobileSensingCommon.getDeviceID(context);
        this.mqttConnectionFactory = new MQTTConnectionFactory(context, mDeviceID);
        this.mqttAndroidClient = mqttConnectionFactory.getClientConnectionDefault();
        this.mqttConnectOptions = mqttConnectionFactory.getConnectOptionsDefault();
        this.mCallback = new ConnectListener(context, getPublisher());
    }

    @Override
    public void forceReconnect() {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
            }
        } catch (Exception e) {
            mqttAndroidClient.unregisterResources();
            Log.e(TAG, "Disconnecting failed", e);
        }

        refresh();
        connect();
    }

    @Override
    public void connect() {
        try {
            if (mqttAndroidClient != null && !mqttAndroidClient.isConnected()) {
                Log.d(TAG, "Trying to connect to broker");
                mqttAndroidClient.connect(mqttConnectOptions, null, mCallback);
            } else {
                Log.d(TAG, "Already connected to broker");
            }
        } catch (Exception e) {
            mqttAndroidClient.unregisterResources();
            Log.e(TAG, "Connection to broker failed", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
            }
        } catch (Exception e) {
            mqttAndroidClient.unregisterResources();
            Log.e(TAG, "Disconnecting failed", e);
        }
        refresh();
        Log.d(TAG, "Disconnected successfully from broker");
    }

    @Override
    public Publisher getPublisher() {
        if (mPublisher == null) {
            mPublisher = new MqttPublisher(mqttAndroidClient, mDeviceID);
        }
        return mPublisher;
    }

    private void refresh() {
        this.mqttAndroidClient = mqttConnectionFactory.getClientConnectionDefault();
        this.mqttConnectOptions = mqttConnectionFactory.getConnectOptionsDefault();
        this.mPublisher = new MqttPublisher(mqttAndroidClient, mDeviceID);
        this.mCallback = new ConnectListener(mContext, mPublisher);
    }
}
