package fi.aalto.itmc.mobilesensingservice.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.json.JSONObject;

import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;

/**
 * Created by laptop on 4/14/16.
 */
public class Sensor1DHandler extends AndroidSensorHandler {
    private float mValue;

    public Sensor1DHandler(SensorManager sensorManager, Sensor sensor, int samplingPeriod, int codeID, String sensorName) {
        super(sensorManager, sensor, samplingPeriod, codeID, sensorName);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setValue(event.values[0]);
    }

    @Override
    public JSONObject JSONstatusMessage() {
        return JSONFormatter.sensor1DMessage(mSensorName, getValue());
    }

    private synchronized float getValue() {
        return mValue;
    }

    private synchronized void setValue(float mValue) {
        this.mValue = mValue;
    }
}
