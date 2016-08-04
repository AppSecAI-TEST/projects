package fi.aalto.itmc.mobilesensingservice.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;

/**
 * Created by laptop on 4/18/16.
 */
public class LocationService extends Service
        implements LocationListener {

    private final static String TAG = "LocationService";

    private Location mLocationAddress = null;

    private static boolean isWorking = false;

    private Messenger mSensorLocationHandler = null;

    private LocationManager locationManager;

    ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mLocationFreshnessCheckerFuture = null;

    private static class IncomingHandler extends Handler {
        private final WeakReference<LocationService> mService;

        IncomingHandler(LocationService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            LocationService service = mService.get();
            if (service != null) {
                switch (msg.what) {
                    case MobileSensingCommon.START_SENSING_MSG:
                        service.startSensing(msg.replyTo);
                        break;
                    case MobileSensingCommon.STOP_SENSING_MSG:
                        service.stopSensing();
                        break;
                    case MobileSensingCommon.STATUS_SENSING_MSG:
                        Log.d(TAG, "Received location update request");

                        if (isWorking) {
                            service.updateCurrentLocation(service.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                        }
                        service.sendLocationUpdate();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public void onLocationChanged(Location location) {
        updateCurrentLocation(location);
        sendLocationUpdate();

        if (location != null) {
            if (mLocationFreshnessCheckerFuture != null) {
                mLocationFreshnessCheckerFuture.cancel(true);
            }
            setLocationFreshnessAlarm();
            Log.d(TAG, "onLocationChanged: " + location.toString());
        } else {
            Log.d(TAG, "onLocationChanged: location is null");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, provider + "changed status to" + Integer.toString(status));
        if (status == LocationProvider.AVAILABLE) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MobileSensingCommon.UPDATE_INTERVAL_MS_LOCATION, 0, this);
        }
        if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            updateCurrentLocation(null);
            Log.d(TAG, "Location provider is out of service, sorry");
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, provider + "enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, provider + "disabled");
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "location manager is null");
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSensing();
        return super.onUnbind(intent);
    }

    private void updateCurrentLocation(Location location) {
        mLocationAddress = location;
    }

    private void sendLocationUpdate() {
        if (isWorking) {
            try {
                Message msg = Message.obtain(null, MobileSensingCommon.STATUS_SENSING_MSG);
                msg.obj = mLocationAddress;
                mSensorLocationHandler.send(msg);
            } catch (RemoteException e) {
                Log.d(TAG, "Sending location update failed", e);
            }
        }
    }

    private void startSensing(Messenger replyTo) {
        if (!isWorking) {
            this.mSensorLocationHandler = replyTo;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MobileSensingCommon.UPDATE_INTERVAL_MS_LOCATION, 0, this);
            isWorking = true;
            updateCurrentLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            Log.d(TAG, "Location sensing started");
        }
    }

    private void stopSensing() {
        if (isWorking) {
            locationManager.removeUpdates(this);
            if (mLocationFreshnessCheckerFuture != null) {
                mLocationFreshnessCheckerFuture.cancel(true);
            }
            isWorking = false;
            Log.d(TAG, "Location sensing stopped");
        }
    }

    private void setLocationFreshnessAlarm() {
        mLocationFreshnessCheckerFuture = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Location is too old, setting null and updating app");
                updateCurrentLocation(null);
                sendLocationUpdate();
            }
        }, MobileSensingCommon.FRESHNESS_MS_LOCATION, TimeUnit.MILLISECONDS);
    }

}
