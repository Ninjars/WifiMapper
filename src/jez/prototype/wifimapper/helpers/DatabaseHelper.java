package jez.prototype.wifimapper.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";	
	
	public static final String TABLE_WIFI = "wifi";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SSID = "SSID";
	public static final String COLUMN_BSSID = "BSSID";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_LASTUPDATE = "lastUpdate";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_ACTIVITY = "activity";
	public static final String COLUMN_FREQ = "frequency";
	public static final String COLUMN_CAPABILITIES = "capabilities";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LONG = "longitude";
	public static final String COLUMN_ACCURACY = "accuracy";
	
	private static final String DATABASE_NAME = "captured_wifi.db";
	private static final int DATABASE_VERSION = 1;
	
	public static enum COLUMNS {
		COLUMN_ID,
		COLUMN_SSID,
		COLUMN_BSSID,
		COLUMN_TIMESTAMP,
		COLUMN_LASTUPDATE,
		COLUMN_ACTIVITY,
		COLUMN_LEVEL,
		COLUMN_FREQ,
		COLUMN_CAPABILITIES,
		COLUMN_LAT,
		COLUMN_LONG,
		COLUMN_ACCURACY
	}
	// make sure the order here matches the COLUMNS enum 
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_WIFI + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SSID + " TEXT NOT NULL, "
			+ COLUMN_BSSID + " TEXT NOT NULL, "
			+ COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
			+ COLUMN_LASTUPDATE + " INTEGER NOT NULL, "
			+ COLUMN_ACTIVITY + " REAL NOT NULL, "
			+ COLUMN_LEVEL + " INTEGER NOT NULL, "
			+ COLUMN_FREQ + " INTEGER NOT NULL, "
			+ COLUMN_CAPABILITIES + " TEXT, "
			+ COLUMN_LAT + " INTEGER NOT NULL, "
			+ COLUMN_LONG + " INTEGER NOT NULL, "
			+ COLUMN_ACCURACY + " REAL NOT NULL);";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
		                + newVersion + ", which will destroy all old data");
		resetDatabase(db, TABLE_WIFI);
	}
	
	public void resetDatabase(SQLiteDatabase db, String table) {
		Log.w(TAG, "reseting database " + db + " table " + table);
		db.execSQL("DROP TABLE IF EXISTS " + table);
		onCreate(db);
	}

}
