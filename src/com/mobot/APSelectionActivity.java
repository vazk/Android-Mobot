package com.mobot;
 
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class APSelectionActivity extends ListActivity {
    
    
    public class APItemAdapter extends ArrayAdapter<ScanResult> {
        private ArrayList<ScanResult> items;
        public APItemAdapter(Context context, int textViewResourceId, ArrayList<ScanResult> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }    
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.aprow, null);
            }
            ScanResult ap = items.get(position);
            if (ap != null) {
                TextView tt = (TextView) v.findViewById(R.id.apname);
                if (tt != null) {
                    tt.setText(ap.SSID);                            
                }
            }
            return v;
        }
        public void add(ScanResult sr) {
        	items.add(sr);
        }
    }	
	
    private APItemAdapter    mAdapter;
    private ProgressDialog   mProgressDialog;
	private WifiManager      mMainWifi;
    private WifiReceiver     mWifiReceiver;
    private NetStatReceiver  mNetStatReceiver;
    private ArrayList<ScanResult> mItemList;

    
    
    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
        	List<ScanResult> wifiList = mMainWifi.getScanResults();
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            for(int i = 0; i < wifiList.size(); i++){
                ScanResult scanResult = wifiList.get(i);                
                mAdapter.add(scanResult);
            }
            mAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
            unregisterReceiver(mWifiReceiver);
        }
    }
    
    private class NetStatReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.dismiss();

            final String action = intent.getAction();
            System.out.println("Action: " + action	.toString());
            
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
           	    
            	NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
           	    System.out.println("nwInfo: " + nwInfo.getState());
           	    
           	    if(NetworkInfo.State.CONNECTING.equals(nwInfo.getState())){
           	    	mProgressDialog = ProgressDialog.show(APSelectionActivity.this, "Hold on...", "Connecting to the network...", true);
           	        System.out.println("CONNECTING...");
           	    } else 
        	    if(NetworkInfo.State.CONNECTED.equals(nwInfo.getState())){
                    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

                	WifiInfo myWifiInfo = wifi.getConnectionInfo();
                    int ip = myWifiInfo.getIpAddress();
                    int ip3 = ip/0x1000000;
                    int ip3mod = ip%0x1000000;
                    int ip2 = ip3mod/0x10000;
                    int ip2mod = ip3mod%0x10000;
                    int ip1 = ip2mod/0x100;
                    int ip0 = ip2mod%0x100;
                    if(ip0 != 0 || ip1 != 0 || ip2 != 0 || ip3 != 0) {                        
                        System.out.println("IP address: " + ip0 + "." + ip1 + "." + ip2 + "." + ip3);
                        unregisterReceiver(mNetStatReceiver);
                    } else {
                    	System.out.println("Received fake IP");
                    }
                    mProgressDialog.dismiss();
                } else {
                    System.out.println("DISCONNECTED!");
                    mProgressDialog.dismiss();
                }
            }
        }
    }; 
    
    
    
    
    
    
    
        
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);       
       setContentView(R.layout.main);  
       mItemList = new ArrayList<ScanResult>();
       mAdapter = new APItemAdapter(this, R.layout.aprow, mItemList);
       setListAdapter(mAdapter);       
       mMainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       mWifiReceiver = new WifiReceiver();
       mNetStatReceiver = new NetStatReceiver();       
       registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
   	   mMainWifi.startScan();       
   	   mProgressDialog = ProgressDialog.show(APSelectionActivity.this, "Hold on...", "Scanning access points...", true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mMainWifi.startScan();
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onPause() {
    	unregisterReceiver(mWifiReceiver);
    	unregisterReceiver(mNetStatReceiver);
        mProgressDialog.dismiss();
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));        
        super.onResume();
    }
    
    public void onListItemClick(ListView parent, View v, int position, long id) {  
    	ScanResult scanResult = (ScanResult)mAdapter.getItem(position); 
    	System.out.println("wifi item is clicked! ");
    	tryWiFiConnect(this, scanResult.SSID);
	}
    
    
       
    
    private void tryWiFiConnect(Context context, String ssid)
    {
        WifiManager wifi = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration(); 
        wc.SSID = "\"" + ssid + "\"";
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;     
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //wc.preSharedKey = "\"" + deviceConfig.wireless_passkey + "\"";// 
        System.out.println("ssid : " + wc.SSID);
        List<WifiConfiguration> netWorkList =  wifi.getConfiguredNetworks();
        
        WifiConfiguration wifiCong = null;
        if (netWorkList != null) {
            for(WifiConfiguration item:netWorkList) {
                if(item.SSID != null && item.SSID.equalsIgnoreCase("\"" + ssid + "\"")) {
                    wifiCong = item;
                }
            }
        }
        wifi.setWifiEnabled(true);
        int res = wifi.addNetwork(wc);
        wifi.enableNetwork(res, true);   
        wifi.saveConfiguration();
        registerReceiver(mNetStatReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        mProgressDialog = ProgressDialog.show(APSelectionActivity.this, "Hold on...", "Connecting to the network...", true);
    }
}
