package fi.aalto.itmc.mobilesensingservice.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.json.JSONObject;

/**
 * Created by laptop on 4/27/16.
 */
public abstract class AndroidSensorHandler implements SensorEventListener, SensingHandlerInterface{
    protected final String TAG = "Sensor Handler";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mSamplingPeriod;
    private int mCodeID;
    protected String mSensorName;

    public AndroidSensorHandler() {
    }

    public AndroidSensorHandler(SensorManager sensorManager, Sensor sensor, int samplingPeriod, int codeID, String sensorName) {
        this.mSensorManager = sensorManager;
        this.mSensor = sensor;
        this.mSamplingPeriod = samplingPeriod;
        this.mCodeID = codeID;
        this.mSensorName = sensorName;
    }

    @Override
    abstract public void onSensorChanged(SensorEvent event);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void activate() {
        mSensorManager.registerListener(this, mSensor, mSamplingPeriod);
    }

    public void deactivate() {
        mSensorManager.unregisterListener(this);
    }

    public int getCodeID() {
        return mCodeID;
    }

    public String getSensorName() {
        return mSensorName;
    }

    abstract public JSONObject JSONstatusMessage();
}
