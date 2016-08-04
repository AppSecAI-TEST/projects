package fi.aalto.itmc.mobilesensingapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import fi.aalto.itmc.mobilesensingapp.sqlite.StatusDB;
import fi.aalto.itmc.mobilesensingcommon.MobileSensingCommon;
import fi.aalto.itmc.mobilesensingcommon.parcelable.ParcelableStringList;

public class MainActivity extends AppCompatActivity {

    // Tag used for log message
    private static final String TAG = "SensingApp";

    private Messenger mService = null;
    private boolean mIsBound = false;
    private boolean mIsSensing = false;

    private TextView mStatusTextView;
    private TextView mStatusDataTextView;
    private TextView mActiveSensorsTextView;
    private EditText mSensorRateEdit;
    private EditText mMsgRateEdit;

    private Button mQueryStatusBtn = null;
    private Button mStartSensingBtn = null;
    private Button mStopSensingBtn = null;
    private Button mSendDataBtn = null;
    private Button mStatusDataBtn = null;

    private Intent mSensingIntent;

    private StatusDB mDatabase;

    private static class IncomingHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        IncomingHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MobileSensingCommon.STATUS_SENSING_MSG:
                        activity.parseStatusUpdate(msg);
                        break;
                    case MobileSensingCommon.STATUS_DATA_MSG:
                        activity.parseDataStatusUpdate(msg);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new IncomingHandler(MainActivity.this));


    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.d(TAG, "Connected to SensingService");
            updateStatus();

            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = StatusDB.getInstance(this);
        mIsSensing = mDatabase.getStatus();

        mStatusTextView = (TextView) findViewById(R.id.status_textView);
        mStatusDataTextView = (TextView) findViewById(R.id.status_data_textView);
        mActiveSensorsTextView = (TextView) findViewById(R.id.active_sensors_textView);
        mSensorRateEdit = (EditText) findViewById(R.id.sensor_rate_number_editText);
        mMsgRateEdit = (EditText) findViewById(R.id.msg_rate_number_editText);

        mQueryStatusBtn = (Button) findViewById(R.id.query_status_button);
        mQueryStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus();
                Log.d(TAG, "Status query message sent");
            }
        });

        mStartSensingBtn = (Button) findViewById(R.id.start_sensing_button);
        mStartSensingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message msg = Message.obtain(null, MobileSensingCommon.START_SENSING_MSG);
                    msg.replyTo = mMessenger;
                    msg.arg1 = getNumberFromEditText(mSensorRateEdit, MobileSensingCommon.SENSOR_SAMPLE_RATE_DEFAULT_S);
                    msg.arg2 = getNumberFromEditText(mMsgRateEdit, MobileSensingCommon.MSG_SENDING_RATE_DEFAULT_S);
                    mService.send(msg);
                    Log.d(TAG, "Start sensing message sent");
                } catch (RemoteException e) {
                    return;
                }
                Toast toast = Toast.makeText(MainActivity.this, "Sensor rate: " + getNumberFromEditText(mSensorRateEdit, MobileSensingCommon.SENSOR_SAMPLE_RATE_DEFAULT_S) +
                        "s message rate: " + getNumberFromEditText(mMsgRateEdit, MobileSensingCommon.MSG_SENDING_RATE_DEFAULT_S) + "s", Toast.LENGTH_LONG);
                toast.show();
                mIsSensing = true;
            }
        });
        mStopSensingBtn = (Button) findViewById(R.id.stop_sensing_button);
        mStopSensingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Message msg = Message.obtain(null, MobileSensingCommon.STOP_SENSING_MSG);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    Log.d(TAG, "Stop sensing message sent");
                } catch (RemoteException e) {
                    return;
                }
                mDatabase.setStatus(false);
                mIsSensing = false;
            }
        });
        mSendDataBtn = (Button) findViewById(R.id.send_data_button);
        mSendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconnectAndSendMessage();
                Log.d(TAG, "Reconnect and Send request to broker message sent");
            }
        });
        mStatusDataBtn = (Button) findViewById(R.id.status_data_button);
        mStatusDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataStatus();
                Log.d(TAG, "Data status query message sent");
            }
        });
        updateViewBySensing();


        mSensingIntent = new Intent();
        mSensingIntent.setClassName("fi.aalto.itmc.mobilesensingservice",
                "fi.aalto.itmc.mobilesensingservice.SensingService");

        startService(mSensingIntent);
        bindService(mSensingIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(mSensingIntent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Resuming app");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mIsSensing) {
            doUnbindService();
            stopService(mSensingIntent);
        }
        Log.d(TAG, "Stopping app");
    }

    private void doUnbindService() {
        if (mIsBound) {
            Log.d(TAG, "Unbinding from sensing service");
            unbindService(mConnection);
            mIsBound = false;
            mService = null;
        }
    }

    private int getNumberFromEditText(EditText editText, int defaultNumber) {
        if (editText == null) {
            return defaultNumber;
        }
        try {
            return Integer.parseInt(editText.getText().toString());

        } catch (NumberFormatException ex) {
            return defaultNumber;
        }
    }

    private void updateViewBySensing() {
        mStartSensingBtn.setEnabled(!mIsSensing);
        mMsgRateEdit.setEnabled(!mIsSensing);
        mSensorRateEdit.setEnabled(!mIsSensing);

        mStopSensingBtn.setEnabled(mIsSensing);
    }

    private void parseStatusUpdate(Message msg) {
        mStatusTextView.setText(msg.arg1 == 1 ? "Actively sensing " + msg.arg2 + " sensors" : "Having a break");
        mIsSensing = msg.arg1 == 1;
        mDatabase.setStatus(mIsSensing);

        Bundle data = msg.getData();
        data.setClassLoader(ParcelableStringList.class.getClassLoader());
        ParcelableStringList activeSensors = data.getParcelable(MobileSensingCommon.ACTIVE_SENSORS_BUNDLE_NAME);
        String text = "";
        for (String sensorName : activeSensors.getData()) {
            text = text.concat(sensorName + " ");
        }
        if (text.isEmpty()) {
            mActiveSensorsTextView.setText(getText(R.string.active_sensors_text_view));
        } else {
            mActiveSensorsTextView.setText(text);
        }

        updateViewBySensing();
        Log.d(TAG, "Updating status");
    }

    private void parseDataStatusUpdate(Message msg) {
        mStatusDataTextView.setText("There are " + Integer.toString(msg.arg1) + " lines in db");

        Log.d(TAG, "Updating data status");
    }

    private void updateStatus() {
        Message msg = Message.obtain(null, MobileSensingCommon.STATUS_SENSING_MSG);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d(TAG, "Updating status failed");
        }
    }

    private void dataStatus() {
        Message msg = Message.obtain(null, MobileSensingCommon.STATUS_DATA_MSG);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d(TAG, "Updating data status failed");
        }
    }

    private void reconnectAndSendMessage() {
        Message msg = Message.obtain(null, MobileSensingCommon.RECONNECT_AND_SEND_MSG);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.d(TAG, "Command to reconnect and send data to broker failed");
        }
    }
}
