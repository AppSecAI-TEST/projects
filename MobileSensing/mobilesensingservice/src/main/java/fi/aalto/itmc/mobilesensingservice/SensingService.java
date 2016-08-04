package fi.aalto.itmc.mobilesensingservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingcommon.parcelable.ParcelableStringList;
import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;
import fi.aalto.itmc.mobilesensingservice.messaging.MessageSender;
import fi.aalto.itmc.mobilesensingservice.messaging.Messaging;
import fi.aalto.itmc.mobilesensingservice.messaging.MqttMessaging;
import fi.aalto.itmc.mobilesensingservice.schedulers.SensingScheduledWorkers;
import fi.aalto.itmc.mobilesensingservice.schedulers.Startable;
import fi.aalto.itmc.mobilesensingservice.sensors.AndroidSensorHandler;
import fi.aalto.itmc.mobilesensingservice.sensors.SensingHandlerInterface;
import fi.aalto.itmc.mobilesensingservice.sensors.SensorFactory;
import fi.aalto.itmc.mobilesensingservice.sensors.SensorLocationHandler;
import fi.aalto.itmc.mobilesensingservice.sensors.SensorWifiHandler;
import fi.aalto.itmc.mobilesensingservice.sqlite.SensorDataDB;

/**
 * Created by laptop on 4/12/16.
 */
public class SensingService extends Service {

    // Tag used for log message
    private static final String TAG = "SensingService";

    private boolean mSensingActive = false;

    private Map<Integer, SensingHandlerInterface> mSensorMap = null;
    private SensorFactory mSensorFactory = null;

    private Messaging mMessaging = null;

    private int mSensorRate;
    private int mMsgRate;

    private Startable sensingScheduledWorkers = null;


    private static class IncomingHandler extends Handler {
        private final WeakReference<SensingService> mService;

        IncomingHandler(SensingService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SensingService service = mService.get();
            if (service != null) {
                switch (msg.what) {
                    case MobileSensingCommon.START_SENSING_MSG:
                        if (!service.mSensingActive) {
                            service.mSensorRate = msg.arg1;
                            service.mMsgRate = msg.arg2;
                            service.startSensing();
                            service.sendStatusToMainClient(msg.replyTo);
                        }
                        Log.d(TAG, "SensingService received start sensing command");
                        break;
                    case MobileSensingCommon.STOP_SENSING_MSG:
                        if (service.mSensingActive) {
                            service.stopSensing();
                            service.sendStatusToMainClient(msg.replyTo);
                        }
                        Log.d(TAG, "SensingService received stop sensing command");
                        break;
                    case MobileSensingCommon.STATUS_SENSING_MSG:
                        service.sendStatusToMainClient(msg.replyTo);
                        Log.d(TAG, "SensingService received status query command");
                        break;
                    case MobileSensingCommon.STATUS_DATA_MSG:
                        SensorDataDB db = SensorDataDB.getInstance(service);
                        long numberOfMessages = db.getNumberOfMessages();
                        service.sendDataStatusToMainClient(msg.replyTo, numberOfMessages);
                        Log.d(TAG, "SensingService received status data query command");
                        break;
                    case MobileSensingCommon.RECONNECT_AND_SEND_MSG:
                        service.mMessaging.forceReconnect();
                        Log.d(TAG, "SensingService received send to broker request command");
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private final Messenger mMessenger = new Messenger(new IncomingHandler(SensingService.this));

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Client bound to SensingService");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Client unbinding to SensingService");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SensingService started");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mSensorFactory = new SensorFactory((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        mSensorMap = new HashMap<>();
        mMessaging = new MqttMessaging(this);
        sensingScheduledWorkers = new SensingScheduledWorkers(this);
        initSensors();

        BroadcastReceiver broadcastReceiver = wifiStateChangedReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Log.d(TAG, "SensingService created");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        deactivateSensors();
        mSensorMap.clear();

        mSensorFactory = null;
        mSensorMap = null;

        Log.d(TAG, "SensingService destroyed");
        super.onDestroy();
    }

    private void initSensors() {
        AndroidSensorHandler temperatureService = mSensorFactory.createTemperatureService(MobileSensingCommon.MAX_SAMPLE_INTERVAL_MS);
        if (temperatureService != null) {
            Log.d(TAG, "Temperature sensor created");
            mSensorMap.put(temperatureService.getCodeID(), temperatureService);
        }
        AndroidSensorHandler pressureService = mSensorFactory.createPressureService(MobileSensingCommon.MAX_SAMPLE_INTERVAL_MS);
        if (pressureService != null) {
            Log.d(TAG, "Pressure sensor created");
            mSensorMap.put(pressureService.getCodeID(), pressureService);
        }
        AndroidSensorHandler lightService = mSensorFactory.createLightService(MobileSensingCommon.MAX_SAMPLE_INTERVAL_MS);
        if (lightService != null) {
            Log.d(TAG, "Light sensor created");
            mSensorMap.put(lightService.getCodeID(), lightService);
        }
        SensorLocationHandler locationService = mSensorFactory.createLocationService(this);
        if (locationService != null) {
            Log.d(TAG, "Location 'sensor' created");
            mSensorMap.put(-1, locationService);
        }
        SensorWifiHandler wifiService = mSensorFactory.createWifiService(this);
        if (wifiService != null) {
            Log.d(TAG, "Wifi 'sensor' created");
            mSensorMap.put(-2, wifiService);
        }
    }

    private void activateSensors() {
        for (Map.Entry<Integer, SensingHandlerInterface> entry : mSensorMap.entrySet()) {
            entry.getValue().activate();
        }
    }

    private void deactivateSensors() {
        for (Map.Entry<Integer, SensingHandlerInterface> entry : mSensorMap.entrySet()) {
            entry.getValue().deactivate();
        }
    }

    private void startSensing() {
        if (!mSensingActive) {
            activateSensors();
            mMessaging.connect();
            sensingScheduledWorkers.start(mMessaging, mSensorRate, mMsgRate);
            mSensingActive = true;
        }
    }

    private void stopSensing() {
        if (mSensingActive) {
            sensingScheduledWorkers.stop();
            deactivateSensors();
            mMessaging.disconnect();
            mSensingActive = false;
        }
    }

    public String JSONsensorMessage(long timestamp) {
        boolean locationSensor = false;
        List<JSONObject> sensorVals = new LinkedList<>();
        for (Map.Entry<Integer, SensingHandlerInterface> entry : mSensorMap.entrySet()) {
            JSONObject jsonObject = entry.getValue().JSONstatusMessage();
            try {
                if (jsonObject.length() > 0 && jsonObject.names().get(0).equals("location")) {
                    locationSensor = true;
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONsensorMessage failed", e);
            }
            sensorVals.add(jsonObject);
        }
        if (locationSensor == false) {
            Log.d(TAG, "JSONsensorMessage: location was not in sensing data");
            return "";
        }
        return JSONFormatter.sensingMessage(sensorVals, timestamp).toString();
    }

    private void sendStatusToMainClient(Messenger replyTo) {
        ParcelableStringList parcelableStringList = new ParcelableStringList(readySensorNames());
        Bundle bundle = new Bundle();
        bundle.putParcelable(MobileSensingCommon.ACTIVE_SENSORS_BUNDLE_NAME, parcelableStringList);
        try {
            Message msg = Message
                    .obtain(null, MobileSensingCommon.STATUS_SENSING_MSG);
            msg.arg1 = mSensingActive ? 1 : 0;
            msg.arg2 = mSensorMap.size();
            msg.setData(bundle);
            replyTo.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Sending status message");
    }

    private void sendDataStatusToMainClient(Messenger replyTo, long result) {
        try {
            Message msg = Message
                    .obtain(null, MobileSensingCommon.STATUS_DATA_MSG);
            msg.arg1 = (int) result;
            replyTo.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Sending data status message");
    }

    private List<String> readySensorNames() {
        List<String> sensorNames = new LinkedList<>();
        for (Map.Entry<Integer, SensingHandlerInterface> entry : mSensorMap.entrySet()) {
            sensorNames.add(entry.getValue().getSensorName());
        }
        return sensorNames;
    }

    private BroadcastReceiver wifiStateChangedReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    mMessaging.forceReconnect();
                    Log.d(TAG, "Connected to network:" + netInfo.getExtraInfo());

                } else {
                    mMessaging.disconnect();
                }
            }
        };
    }
}
