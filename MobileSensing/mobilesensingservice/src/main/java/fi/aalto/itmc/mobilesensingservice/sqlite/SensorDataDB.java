package fi.aalto.itmc.mobilesensingservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by laptop on 4/23/16.
 */
public class SensorDataDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "sensorDatabase";

    private static final String SENSOR_TABLE_NAME = "data";


    private static final String KEY_ID = "_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";

    private static final String CREATE_ADDRESS_TABLE = "CREATE TABLE " + SENSOR_TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT, "
            + KEY_TIMESTAMP + " INTEGER)";


    private static SensorDataDB mInstance;

    private SensorDataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADDRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SENSOR_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public static SensorDataDB getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SensorDataDB(ctx.getApplicationContext());
        }
        return mInstance;
    }


    public List<String> messagesBefore(long timestamp) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SENSOR_TABLE_NAME, new String[]{KEY_MESSAGE}, KEY_TIMESTAMP + " <= ?", new String[]{String.valueOf(timestamp)}, null, null, null);

        if (cursor != null) {
            LinkedList<String> res = new LinkedList<>();
            while (cursor.moveToNext()) {
                res.add(cursor.getString(0));
            }
            db.close();
            cursor.close();
            return res;
        } else {
            db.close();
            return new LinkedList<>();
        }
    }

    public void putMessage(String message, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message);
        values.put(KEY_TIMESTAMP, timestamp);

        db.insert(SENSOR_TABLE_NAME, null, values);
        db.close();
    }

    public void deleteMessagesBeforeTimestamp(long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SENSOR_TABLE_NAME, KEY_TIMESTAMP + " <= ?",
                new String[]{String.valueOf(timestamp)});
        db.close();
    }

    public long getNumberOfMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, SENSOR_TABLE_NAME);
        return count;
    }
}
