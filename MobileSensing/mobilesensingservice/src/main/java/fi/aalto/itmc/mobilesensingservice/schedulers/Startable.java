package fi.aalto.itmc.mobilesensingservice.schedulers;

import fi.aalto.itmc.mobilesensingservice.messaging.Messaging;

/**
 * Created by laptop on 4/22/16.
 */
public interface Startable {
    void start(Messaging messaging, int sensorRate, int msgRate);
    void stop();
}
