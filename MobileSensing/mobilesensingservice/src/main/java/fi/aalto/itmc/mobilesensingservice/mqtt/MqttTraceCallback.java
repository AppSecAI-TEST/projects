package fi.aalto.itmc.mobilesensingservice.mqtt;

import android.util.Log;

import org.eclipse.paho.android.service.MqttTraceHandler;

/**
 * Created by laptop on 4/15/16.
 */
public class MqttTraceCallback implements MqttTraceHandler {

    @Override
    public void traceDebug(String s, String s1) {
        Log.d(s, s1);
    }

    @Override
    public void traceError(String s, String s1) {
        Log.e(s, s1);
    }

    @Override
    public void traceException(String s, String s1, Exception e) {
        Log.e(s, s1, e);
    }
}
