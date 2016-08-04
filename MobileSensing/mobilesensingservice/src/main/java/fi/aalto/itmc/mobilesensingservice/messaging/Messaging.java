package fi.aalto.itmc.mobilesensingservice.messaging;

/**
 * Created by laptop on 4/15/16.
 */
public interface Messaging {
    void forceReconnect();

    void connect();

    void disconnect();

    Publisher getPublisher();
}
