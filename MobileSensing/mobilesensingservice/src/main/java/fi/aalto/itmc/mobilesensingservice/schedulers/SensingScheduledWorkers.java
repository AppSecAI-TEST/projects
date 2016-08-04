package fi.aalto.itmc.mobilesensingservice.schedulers;


import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingservice.SensingService;
import fi.aalto.itmc.mobilesensingservice.messaging.MessageSender;
import fi.aalto.itmc.mobilesensingservice.messaging.Messaging;
import fi.aalto.itmc.mobilesensingservice.sqlite.SensorDataDB;

/**
 * Created by laptop on 4/22/16.
 */
public class SensingScheduledWorkers implements Startable {
    private final SensingService mSensingService;
    private boolean enabled = false;

    private ScheduledFuture<?> mSensorCollectorFuture;
    private ScheduledFuture<?> mMessageSenderFuture;

    public SensingScheduledWorkers(SensingService mSensingService) {
        this.mSensingService = mSensingService;
    }

    @Override
    public void start(final Messaging messaging, int sensorRate, int msgRate) {
        enabled = true;
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        //sensor collector
        mSensorCollectorFuture = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (enabled == false) {
                    return;
                }
                long timestamp = MobileSensingCommon.getTimeNow();
                String message = mSensingService.JSONsensorMessage(timestamp);
                if(message.isEmpty()){
                    return;
                }
                SensorDataDB db = SensorDataDB.getInstance(mSensingService);
                db.putMessage(message, timestamp);
            }
        }, sensorRate, sensorRate, TimeUnit.SECONDS);

        //message sender
        mMessageSenderFuture = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (enabled == false) {
                    return;
                }
                MessageSender.sendSensorData(mSensingService, messaging.getPublisher());
            }
        }, msgRate, msgRate, TimeUnit.SECONDS);
        Log.d("SensingScheduledWorkers", "Workers created with sensor rate: " + sensorRate + "s and message rate: " + msgRate + "s");
    }

    @Override
    public void stop() {
        mSensorCollectorFuture.cancel(true);
        mMessageSenderFuture.cancel(true);
        enabled = false;
    }


}
