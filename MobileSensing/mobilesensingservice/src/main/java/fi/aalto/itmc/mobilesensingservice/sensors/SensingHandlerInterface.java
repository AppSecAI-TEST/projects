package fi.aalto.itmc.mobilesensingservice.sensors;

import org.json.JSONObject;

/**
 * Created by laptop on 4/27/16.
 */
public interface SensingHandlerInterface {
    void activate();

    void deactivate();

    String getSensorName();

    JSONObject JSONstatusMessage();
}
