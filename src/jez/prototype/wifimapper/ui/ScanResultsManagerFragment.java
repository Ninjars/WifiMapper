package jez.prototype.wifimapper.ui;

import java.util.List;

import jez.prototype.wifimapper.Constants;
import jez.prototype.wifimapper.R;
import jez.prototype.wifimapper.WifiTester;
import jez.prototype.wifimapper.data.DataManager;
import jez.prototype.wifimapper.data.WifiData;
import jez.prototype.wifimapper.listeners.ScanListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ScanResultsManagerFragment extends Fragment implements OnClickListener, ScanListener {
	private static final String TAG = "ScanResultsManagerFragment";
	private static final boolean DEBUG = Constants.DEBUG;

	private Activity mActivity;
	private ArrayAdapter<WifiData> adapter;
	private ListView mListView;
	
	private DataManager mDataManager;
	private List<WifiData> mWifiDataList;	

	private Button mScanButton;
	private Button mClearButton;
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		super.onCreateView(inflater, container, bundle);
	    View view = inflater.inflate(R.layout.scan_results_fragment, container, false);
	    mListView = (ListView) view.findViewById(R.id.list);
	    mDataManager = ((WifiTester) mActivity).getDataManager();
	    mWifiDataList = mDataManager.getWifiData();
	    
		mScanButton = (Button) view.findViewById(R.id.buttonScan);
		mScanButton.setOnClickListener(this);
		mClearButton = (Button) view.findViewById(R.id.buttonClear);
		mClearButton.setOnClickListener(this);
		
		// view adapter for pushing data to list view
		this.adapter = new ArrayAdapter<WifiData>(mActivity.getApplicationContext(), R.layout.row, mWifiDataList);
		mListView.setAdapter(this.adapter);
		
	    return view;
	}

	@Override
	public void onScanResult() {
		if (DEBUG) Log.i(TAG, "onScanResult()");
		mWifiDataList = mDataManager.getWifiData();
		adapter.notifyDataSetChanged();
	}
	
	 public void onClick(View view) {
	    	if (DEBUG) Log.i(TAG, "onClick()");
	    	
	    	if (view.getId() == R.id.buttonScan) {
	    		if (DEBUG) Log.i(TAG, "Scan clicked");
	    		mDataManager.startScan();
				return;
	    	}
	    	if (view.getId() == R.id.buttonClear) {
	    		if (DEBUG) Log.i(TAG, "Clear clicked");
	    		mDataManager.clearDatabase();
				return;
	    	}
	    }

	@Override
	public void onDataChanged() {
		mWifiDataList = mDataManager.getWifiData();
		adapter.notifyDataSetChanged();
		
	}

}
