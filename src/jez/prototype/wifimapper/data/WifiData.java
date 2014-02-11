package jez.prototype.wifimapper.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;

public class WifiData {
	@SuppressWarnings("unused")
	private static final String TAG = "WifiData";
	private long id;
	private String ssid;
	private String bssid;
	private long timestamp;
	private long lastUpdate;
	private int level;
	private int freq;
	private String capbilities;
	private float activity;
	private double latitude;
	private double longitude;
	private float accuracy;

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#0.00");
		return getSsid() + " @ " + df.format(getLatitude()) + ", " + df.format(getLongitude()) + "\n" + getLastUpdateAsString();
	}
	@SuppressLint("SimpleDateFormat")
	public String getLastUpdateAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yy");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getLastUpdate());
		return sdf.format(c.getTime());
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public String getCapbilities() {
		return capbilities;
	}
	public void setCapbilities(String capbilities) {
		this.capbilities = capbilities;
	}
	public float getActivity() {
		return activity;
	}
	public void setActivity(float activity) {
		this.activity = activity;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
