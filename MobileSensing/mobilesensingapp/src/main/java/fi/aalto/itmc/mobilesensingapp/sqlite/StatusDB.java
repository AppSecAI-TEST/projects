package fi.aalto.itmc.mobilesensingapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by laptop on 4/24/16.
 */
public class StatusDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "statusDatabase";

    private static final String TABLE_NAME = "data";


    private static final String KEY_ID = "_id";
    private static final String KEY_STATUS = "status";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STATUS + " INTEGER)";

    private static StatusDB mInstance;

    private StatusDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public static StatusDB getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new StatusDB(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public boolean getStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_STATUS}, null, null, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            boolean res = cursor.getInt(0) == 1;
            cursor.close();
            return res;
        } else {
            return false;
        }
    }

    public void setStatus(boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);
        db.insert(TABLE_NAME, null, values);

        db.close();
    }
}
