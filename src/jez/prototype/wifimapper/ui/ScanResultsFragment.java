package jez.prototype.wifimapper.ui;

import jez.prototype.wifimapper.R;
import jez.prototype.wifimapper.WifiTester;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ScanResultsFragment extends Fragment {
	Activity mApp;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
	    View resultsList = inflater.inflate(R.id.resultList, container, false);
	    return resultsList;
	}

}
