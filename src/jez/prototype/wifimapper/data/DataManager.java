package jez.prototype.wifimapper.data;

import java.util.List;

import jez.prototype.wifimapper.Constants;
import jez.prototype.wifimapper.WifiTester;
import jez.prototype.wifimapper.WifiTester.ErrorDialogFragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class DataManager implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private static final String TAG = "DataManager";
	private static final boolean DEBUG = Constants.DEBUG;
	
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    
	private Context mContext;

	// database
	private WifiDataSource mWifiDataSource;
	private BroadcastReceiver mBroadCastReceiver;

	private List<WifiData> mWifiDataList;

	private WifiManager mWifiManager;
	private LocationClient mLocationClient;
	
	public DataManager(Context context) {
		mContext = (WifiTester) context;
        // data linkup
		mWifiDataSource = new WifiDataSource(context);
		mWifiDataSource.open();		
		mWifiDataList = mWifiDataSource.getAll();
		
		// create location client
		mLocationClient = new LocationClient(context, this, this);
		
		// Wifi linkup
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		// turn on wifi if it is off
		enableWifi();
		
		// register to receive broadcast events from wifiManager system
		
		mBroadCastReceiver = new BroadcastReceiver() {			
        	@Override
            public void onReceive(Context c, Intent intent) {
        		if (DEBUG) Log.i(TAG, "onReceive() broadcast intent");
        		((WifiTester) c).getDataManager().onScanResults(mWifiManager.getScanResults());
            }
        };
	}
	
	private void enableWifi() {
		if (mWifiManager.isWifiEnabled() == false) {
			if (DEBUG) Log.i(TAG, "Enabling WiFi");
			((WifiTester) mContext).showToastMessage("Enabling WiFi");
			
		    mWifiManager.setWifiEnabled(true);
		}
	}
	
	public void startScan() {
		enableWifi();
		((WifiTester) mContext).showToastMessage("Scanning");
		mWifiManager.startScan();
	}
	
	public void clearDatabase() {		
		((WifiTester) mContext).showToastMessage("Clearing Database");
		mWifiDataSource.clearDatabase();
		mWifiDataList.clear();
		((WifiTester) mContext).onDataSetChanged();
	}
	
	

	private void onScanResults(List<ScanResult> scanResults) {
		if (DEBUG) Log.i(TAG, "onScanResults()");
    	Location loc = null;
    	if (servicesConnected()) {
    		loc = mLocationClient.getLastLocation();
    	}
    	mWifiDataList.addAll(mWifiDataSource.processScanResults(scanResults, loc));
    	((WifiTester) mContext).onScanResult();
	}
	
	public List<WifiData> getWifiData() {
		return mWifiDataList;
	}
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {            
            // If Google Play services can provide an error dialog
            showGoogleServicesErrorDialog(resultCode);
        } return false;
    }
    
    public void showGoogleServicesErrorDialog(int result) {
    	// Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                result,
                (Activity) mContext,
                CONNECTION_FAILURE_RESOLUTION_REQUEST);

    	if (errorDialog != null) {
    		 // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(((Activity) mContext).getFragmentManager(), "Error Dialog");
    	}
    }
	
	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
       ((WifiTester) mContext).showToastMessage("Google Services Connected");

        if (DEBUG) Log.i(TAG, "onConnected(), registering Receiver");
		mContext.registerReceiver(mBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
    	 ((WifiTester) mContext).showToastMessage("Google Services Disconnected.");
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        (Activity) mContext,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services cancelled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showGoogleServicesErrorDialog(connectionResult.getErrorCode());
        }
    }

	public void onPause() {
		mContext.unregisterReceiver(mBroadCastReceiver);
		mLocationClient.disconnect();		
	}

	public void onResume() {
		mLocationClient.connect();		
	}

	public void onStop() {
		// disconnect from location services
		mLocationClient.disconnect();		
	}
}
