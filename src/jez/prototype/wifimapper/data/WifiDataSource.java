package jez.prototype.wifimapper.data;

import java.util.ArrayList;
import java.util.List;

import jez.prototype.wifimapper.Constants;
import jez.prototype.wifimapper.helpers.DatabaseHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.util.Log;

// http://www.vogella.com/tutorials/AndroidSQLite/article.html#android_requisites
public class WifiDataSource {
	private static String TAG = "WifiDataSource";
	private static final boolean DEBUG = Constants.DEBUG;
	
	private SQLiteDatabase mDb;
	private DatabaseHelper mDbHelper;
	private String[] allColumns = { 
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_SSID,
			DatabaseHelper.COLUMN_BSSID,
			DatabaseHelper.COLUMN_TIMESTAMP,
			DatabaseHelper.COLUMN_LASTUPDATE,
			DatabaseHelper.COLUMN_ACTIVITY,
			DatabaseHelper.COLUMN_LEVEL,
			DatabaseHelper.COLUMN_FREQ,
			DatabaseHelper.COLUMN_CAPABILITIES,
			DatabaseHelper.COLUMN_LAT,
			DatabaseHelper.COLUMN_LONG,
			DatabaseHelper.COLUMN_ACCURACY};
	
	public WifiDataSource(Context context) {
		mDbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Takes a scan result and stores it in the database, then returns a WifiData object 
	 * constructed from the database entry
	 * @param result
	 * @param loc 
	 * @return
	 */
	public WifiData createWifiDataFromScanResult(ScanResult result, Location loc) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_SSID, result.SSID);
		values.put(DatabaseHelper.COLUMN_BSSID, result.BSSID);
		values.put(DatabaseHelper.COLUMN_TIMESTAMP, result.timestamp);
		values.put(DatabaseHelper.COLUMN_LASTUPDATE, result.timestamp);
		values.put(DatabaseHelper.COLUMN_LEVEL, result.level);
		values.put(DatabaseHelper.COLUMN_FREQ, result.frequency);
		values.put(DatabaseHelper.COLUMN_CAPABILITIES, result.capabilities);
		values.put(DatabaseHelper.COLUMN_ACTIVITY, 0); // currently not used
		if (loc != null) {
			values.put(DatabaseHelper.COLUMN_LAT, loc.getLatitude());
			values.put(DatabaseHelper.COLUMN_LONG, loc.getLongitude());
			values.put(DatabaseHelper.COLUMN_ACCURACY, loc.getAccuracy());
		}
		
		long insertId = mDb.insert(DatabaseHelper.TABLE_WIFI, null, values);
		Cursor c = mDb.query(DatabaseHelper.TABLE_WIFI, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		c.moveToFirst();
		WifiData newData = cursorToWifiData(c);
		return newData;
	}
	
	public void deleteEntry(WifiData entry) {
		long id = entry.getId();
		Log.i(TAG, "Deleting entry with id " + id);
		mDb.delete(DatabaseHelper.TABLE_WIFI, DatabaseHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void clearDatabase() {
		Log.i(TAG, "clearDatabase()");
		mDbHelper.resetDatabase(mDb, DatabaseHelper.TABLE_WIFI);
	}
	
	/**
	 * Converts a database row into a WifiData object
	 * @param c
	 * @return
	 */
	private WifiData cursorToWifiData(Cursor c) {
		WifiData data = new WifiData();
//		if (DEBUG) Log.i(TAG, "column ids: ");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_ID) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_SSID) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_BSSID) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_LASTUPDATE) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_LEVEL) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_FREQ) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_CAPABILITIES) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_LONG) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_LAT) + "");
//		if (DEBUG) Log.i(TAG, c.getColumnIndex(DatabaseHelper.COLUMN_ACCURACY) + "");
		
		if (DEBUG) Log.i(TAG, "data: ");		
		if (DEBUG) Log.i(TAG, c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_ID)) + "");
//		if (DEBUG) Log.i(TAG, c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_SSID)) + "");
//		if (DEBUG) Log.i(TAG, c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_BSSID)) + "");
//		if (DEBUG) Log.i(TAG, c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)) + "");
//		if (DEBUG) Log.i(TAG, c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_LASTUPDATE)) + "");
//		if (DEBUG) Log.i(TAG, c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_LEVEL)) + "");
//		if (DEBUG) Log.i(TAG, c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_FREQ)) + "");
//		if (DEBUG) Log.i(TAG, c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CAPABILITIES)) + "");
//		if (DEBUG) Log.i(TAG, c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY)) + "");
//		if (DEBUG) Log.i(TAG, c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_LONG)) + "");
//		if (DEBUG) Log.i(TAG, c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_LAT)) + "");
		if (DEBUG) Log.i(TAG, c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_ACCURACY)) + "");
		
		data.setId(c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_ID)));
		data.setSsid(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_SSID)));
		data.setBssid(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_BSSID)));
		data.setTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)));
		data.setLastUpdate(c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_LASTUPDATE)));
		data.setLevel(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_LEVEL)));
		data.setFreq(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_FREQ)));
		data.setCapbilities(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CAPABILITIES)));
		data.setActivity(c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY)));
		data.setLongitude(c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_LONG)));
		data.setLatitude(c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_LAT)));
		data.setAccuracy(c.getFloat(c.getColumnIndex(DatabaseHelper.COLUMN_ACCURACY)));
		
		return data;
	}
	
	public List<WifiData> getAll() {
		List<WifiData> dataList = new ArrayList<WifiData>();
		
		Cursor c = mDb.query(DatabaseHelper.TABLE_WIFI, allColumns, null, null, null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			WifiData data = cursorToWifiData(c);
			dataList.add(data);
			c.moveToNext();
		}
		
		// make sure we close the cursor
		c.close();
		return dataList;
	}

	public List<WifiData> processScanResults(List<ScanResult> scanResults, Location loc) {
		List<WifiData> dataList = new ArrayList<WifiData>();
		for (ScanResult result : scanResults) {
			String where = DatabaseHelper.COLUMN_BSSID + " = '" + result.BSSID + "'";
			Cursor c = mDb.query(DatabaseHelper.TABLE_WIFI, allColumns, where, null, null, null, null);
			if (c.getCount() > 0) {
				// if result with same BSSID already exists, don't add duplicate
				if (DEBUG) Log.i(TAG, "Result " + result.SSID + " already exists");
				if (loc != null) {
					c.moveToFirst();
					WifiData log = cursorToWifiData(c);
					ContentValues args = new ContentValues();
					String idFilter = DatabaseHelper.COLUMN_ID + " = " + log.getId();					

					// check timestamp and update
					if (log.getLastUpdate() < result.timestamp) {
						if (DEBUG) Log.i(TAG, "New result is more recent");
						// TODO: do check for if result.timestamp is in the next day, not merely after log.getLastUpdate()
						args.put(DatabaseHelper.COLUMN_LASTUPDATE, result.timestamp);
					} 
					
					// if new location is more accurate than old location, replace location values
					if (log.getAccuracy() < loc.getAccuracy()) {
						if (DEBUG) Log.i(TAG, "New result is more accurate: " + log.getAccuracy() + " vs " + loc.getAccuracy());
						args.put(DatabaseHelper.COLUMN_LAT, loc.getLatitude());
						args.put(DatabaseHelper.COLUMN_LONG, loc.getLongitude());
						args.put(DatabaseHelper.COLUMN_ACCURACY, loc.getAccuracy());
					}
					

					if (args.size() > 0) {
						if (DEBUG) Log.i(TAG, "Passing updates to database " + idFilter + " " + args );
						mDb.update(DatabaseHelper.TABLE_WIFI, args, idFilter, null);
					}
				} else {
					if (DEBUG) Log.i(TAG, "No location data");
				}
			} else {
				if (DEBUG) Log.i(TAG, "Adding new result " + result.SSID);
				dataList.add(createWifiDataFromScanResult(result, loc));
			}
			c.close();
		}
		return dataList;
		
	}
}

