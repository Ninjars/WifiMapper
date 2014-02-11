package jez.prototype.wifimapper;

import java.util.List;

import jez.prototype.wifimapper.data.WifiData;
import jez.prototype.wifimapper.data.WifiDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class WifiTester extends Activity implements OnClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private static final String TAG = "WifiTester";
	private static final boolean DEBUG = Constants.DEBUG;
	
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private WifiManager wifi;       
	private ListView mListView;
	private Button mScanButton;
	private Button mClearButton;
	private List<WifiData> mWifiDataList;
	private Toast mToast;
	
	private LocationClient mLocationClient;
	
	// database
	WifiDataSource mWifiDataSource;
	
	private ArrayAdapter<WifiData> adapter;
	private BroadcastReceiver mBroadCastReceiver;
	
	/* Called when the activity is first created. */
	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// layout elements
		mScanButton = (Button) findViewById(R.id.buttonScan);
		mScanButton.setOnClickListener(this);
		mClearButton = (Button) findViewById(R.id.buttonClear);
		mClearButton.setOnClickListener(this);
		mListView = (ListView)findViewById(R.id.list);

		// toast for notes
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mToast.cancel();
		
        // data linkup
		mWifiDataSource = new WifiDataSource(this);
		mWifiDataSource.open();		
		mWifiDataList = mWifiDataSource.getAll();
		
		// view adapter for pushing data to list view
		this.adapter = new ArrayAdapter<WifiData>(this, android.R.layout.simple_list_item_1, mWifiDataList);
		mListView.setAdapter(this.adapter);
		
		// Wifi linkup
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		// turn on wifi if it is off
		enableWifi();
		
		// register to receive broadcast events from wifiManager system
		
		mBroadCastReceiver = new BroadcastReceiver() {			
        	@Override
            public void onReceive(Context c, Intent intent) {
        		if (DEBUG) Log.i(TAG, "onReceive() broadcast intent");
        		((WifiTester) c).onScanResults(wifi.getScanResults());
            }
        };
		
		// create location client
		mLocationClient = new LocationClient(this, this, this);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mBroadCastReceiver);
		mLocationClient.disconnect();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop() {
		// disconnect from location services
		mLocationClient.disconnect();
		super.onStop();
	}
	
	private void enableWifi() {
		if (wifi.isWifiEnabled() == false) {
			if (DEBUG) Log.i(TAG, "Enabling WiFi");
			mToast.setText("Enabling WiFi");
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.show();
			
		    wifi.setWifiEnabled(true);
		}
	}

    protected void onScanResults(List<ScanResult> scanResults) {
    	if (DEBUG) Log.i(TAG, "onScanResults()");
    	Location loc = null;
    	if (servicesConnected()) {
    		loc = mLocationClient.getLastLocation();
    	}
    	mWifiDataList.addAll(mWifiDataSource.processScanResults(scanResults, loc));
    	adapter.notifyDataSetChanged();
    	mToast.cancel();
		
	}

    public void onClick(View view) {
    	if (DEBUG) Log.i(TAG, "onClick()");
    	
    	if (view.getId() == R.id.buttonScan) {
    		if (DEBUG) Log.i(TAG, "Scan clicked");
    		enableWifi();
			wifi.startScan();
			
			mToast.setText("Scanning...");
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.show();
			return;
    	}
    	if (view.getId() == R.id.buttonClear) {
    		if (DEBUG) Log.i(TAG, "Clear clicked");			
			mToast.setText("Clearing Database");
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.show();
			mWifiDataSource.clearDatabase();
			mWifiDataList.clear();
			adapter.notifyDataSetChanged();
			return;
    	}
    }
    
    
    /**
     * Google play services management functions
     *
     */
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
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
                this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST);

    	if (errorDialog != null) {
    		 // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), "Error Dialog");
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
        Toast.makeText(this, "Google Services Connected", Toast.LENGTH_SHORT).show();

        if (DEBUG) Log.i(TAG, "onConnected(), registering Receiver");
		registerReceiver(mBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Google Services Disconnected.",
                Toast.LENGTH_SHORT).show();
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
                        this,
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
}