package fi.aalto.itmc.mobilesensingservice.sensors;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;

/**
 * Created by laptop on 4/18/16.
 */
public class SensorLocationHandler implements SensingHandlerInterface {
    private static final String TAG = "SensorLocationHandler";

    private Messenger mLocationService = null;

    private boolean mIsBound = false;

    private Context mContext = null;

    private Location mLocation = null;

    private String mSensorName;

    private static class IncomingHandler extends Handler {
        private final WeakReference<SensorLocationHandler> mSensorHandler;

        IncomingHandler(SensorLocationHandler sensorHandler) {
            mSensorHandler = new WeakReference<>(sensorHandler);
        }

        @Override
        public void handleMessage(Message msg) {
            SensorLocationHandler sensorHandler = mSensorHandler.get();
            if (sensorHandler != null) {
                switch (msg.what) {
                    case MobileSensingCommon.STATUS_SENSING_MSG:
                        sensorHandler.setLocation((Location) msg.obj);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new IncomingHandler(SensorLocationHandler.this));

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mLocationService = new Messenger(service);
            Log.d(TAG, "Connected to Location Service");

            try {
                Message msg = Message.obtain(null, MobileSensingCommon.START_SENSING_MSG);
                msg.replyTo = mMessenger;
                mLocationService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mLocationService = null;
        }
    };

    public SensorLocationHandler(Context context, String name) {
        super();
        mSensorName = name;
        mContext = context;
    }

    @Override
    public void activate() {
        if (!mIsBound) {
            Intent intent = new Intent();
            intent.setClassName("fi.aalto.itmc.mobilesensingservice",
                    "fi.aalto.itmc.mobilesensingservice.location.LocationService");

            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void deactivate() {
        try {
            if (mIsBound) {
                Message msg = Message.obtain(null, MobileSensingCommon.STOP_SENSING_MSG);
                mLocationService.send(msg);
                mContext.unbindService(mConnection);
                mLocationService = null;
                mIsBound = false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSensorName() {
        return mSensorName;
    }

    @Override
    public JSONObject JSONstatusMessage() {
        return JSONFormatter.sensorLocationMessage(mSensorName, getLocation());
    }

    private synchronized Location getLocation() {
        return mLocation;
    }

    private synchronized void setLocation(Location location) {
        this.mLocation = location;
    }
}
