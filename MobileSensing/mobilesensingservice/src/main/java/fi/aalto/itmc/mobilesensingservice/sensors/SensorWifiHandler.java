package fi.aalto.itmc.mobilesensingservice.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.aalto.itmc.mobilesensingservice.json.JSONFormatter;

/**
 * Created by laptop on 4/27/16.
 */
public class SensorWifiHandler implements SensingHandlerInterface {

    private String mSensorName;

    private Context mContext = null;

    private WifiManager wifiManager = null;

    private boolean mSensing = false;

    private boolean mBroadcastRegistered = false;

    private BroadcastReceiver mScanResultReceiver = null;

    private Map<String, Integer> mWifiMap;

    public SensorWifiHandler(String sensorName, Context context) {
        this.mSensorName = sensorName;
        this.mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiMap = new HashMap<>();

        mScanResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult result : scanResults) {
                    mWifiMap.put(result.SSID, wifiManager.calculateSignalLevel(result.level, 100));
                }
                mSensing = wifiManager.startScan();
            }
        };
    }

    @Override
    public void activate() {
        if (wifiManager != null && mBroadcastRegistered == false) {
            boolean state = wifiManager.setWifiEnabled(true);
            if (state) {
                mContext.registerReceiver(mScanResultReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mBroadcastRegistered = true;
                mSensing = wifiManager.startScan();
            }
        }
    }

    @Override
    public void deactivate() {
        if (mBroadcastRegistered == true) {
            mContext.unregisterReceiver(mScanResultReceiver);
            mBroadcastRegistered = false;
        }
        mSensing = false;
    }

    @Override
    public String getSensorName() {
        return mSensorName;
    }

    @Override
    public JSONObject JSONstatusMessage() {
        return JSONFormatter.sensorWifiMessage(mSensorName, mWifiMap);
    }
}
