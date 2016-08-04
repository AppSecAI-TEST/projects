package fi.aalto.itmc.mobilesensingservice.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by laptop on 4/14/16.
 */
public class SensorFactory {

    private final SensorManager mSensorManager;

    public SensorFactory(SensorManager sensorManager) {
        this.mSensorManager = sensorManager;
    }

    /*
    Sampling rate is in microseconds
     */
    public AndroidSensorHandler createTemperatureService(int samplingRate) {
        return create1DAndroidService(Sensor.TYPE_AMBIENT_TEMPERATURE, samplingRate, "temperature");
    }

    /*
    Sampling rate is in microseconds
     */
    public AndroidSensorHandler createPressureService(int samplingRate) {
        return create1DAndroidService(Sensor.TYPE_PRESSURE, samplingRate, "pressure");
    }

    /*
    Sampling rate is in microseconds
     */
    public AndroidSensorHandler createLightService(int samplingRate) {
        return create1DAndroidService(Sensor.TYPE_LIGHT, samplingRate, "light");
    }

    public SensorLocationHandler createLocationService(Context context){
        return new SensorLocationHandler(context, "location");
    }

    public SensorWifiHandler createWifiService(Context context){
        return new SensorWifiHandler("wifi", context);
    }

    private AndroidSensorHandler create1DAndroidService(int type, int samplingRate, String topic) {
        Sensor sensor = mSensorManager.getDefaultSensor(type);
        if (sensor == null)
            return null;
        return new Sensor1DHandler(mSensorManager, sensor, samplingRate, type, topic);
    }
}
