package jez.prototype.wifimapper;

import jez.prototype.wifimapper.data.DataManager;
import jez.prototype.wifimapper.listeners.ScanListener;
import jez.prototype.wifimapper.ui.ScanResultsManagerFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WifiTester extends Activity {
	private static final String TAG = "WifiTester";
	private static final boolean DEBUG = Constants.DEBUG;
	
	private Toast mToast;
	private DataManager mDataManager;
	private ScanListener mScanListener;
	
	
	/* Called when the activity is first created. */
	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setActiveScreen(Constants.screenMain);
		
		mDataManager = new DataManager(this);

		// toast for notes - we make a dummy one that we will reuse later, avoiding overlapping toasts
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mToast.cancel();
    }
	
	public DataManager getDataManager() {
		return mDataManager;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		mDataManager.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDataManager.onResume();
	}
	
	@Override
	protected void onStop() {
		mDataManager.onStop();
		super.onStop();
	}	
    
    public void showToastMessage(String message) {
		mToast.setText(message);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
    }
	
	private void setActiveScreen(int screenID) {
		if (screenID == Constants.screenMain) {
			Fragment fragment = new ScanResultsManagerFragment();
			mScanListener = (ScanListener) fragment;
			FragmentTransaction ft =  getFragmentManager().beginTransaction();
			ft.replace(R.id.content, fragment);
			ft.commit();
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
            case DataManager.CONNECTION_FAILURE_RESOLUTION_REQUEST :
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

	public void onScanResult() {
		if (DEBUG) Log.i(TAG, "onScanResults()");
		if (mScanListener != null) {
			if (DEBUG) Log.i(TAG, "onScanResults() --> ScanListener");
			mScanListener.onScanResult();
		}
    	mToast.cancel();		
	}

	public void onDataSetChanged() {
		if (mScanListener != null) {
			if (DEBUG) Log.i(TAG, "onScanResults() --> ScanListener");
			mScanListener.onDataChanged();
		}
		
	}    
    
}