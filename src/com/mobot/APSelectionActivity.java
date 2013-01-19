package com.mobot;
 
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import android.widget.Button;
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
	
    private APItemAdapter  mAdapter;
    private ProgressDialog mProgressDialog;
	
    private WifiManager    mMainWifi;
    private WifiReceiver   mReceiverWifi;
    private ArrayList<ScanResult> mItemList;
        
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);       
       setContentView(R.layout.main);  
       mItemList = new ArrayList<ScanResult>();
       mAdapter = new APItemAdapter(this, R.layout.aprow, mItemList);
       setListAdapter(mAdapter);
       
       mMainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       mReceiverWifi = new WifiReceiver();
       registerReceiver(mReceiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

       
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
        unregisterReceiver(mReceiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(mReceiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    
    class WifiReceiver extends BroadcastReceiver {
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
        }
    }
    
    public void onListItemClick(ListView parent, View v, int position, long id) {  
    	ScanResult scanResult = (ScanResult)mAdapter.getItem(position); 
    	System.out.println("wifi item is clicked! ");
    	tryWiFiConnect(this, scanResult.SSID);
	}
    
    private static void tryWiFiConnect(Context context, String ssid)
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
                 if (item.SSID.equalsIgnoreCase("\"" + ssid + "\"")) {
                     wifiCong = item;
                 }
             }
         }

         if (wifiCong == null) {
             boolean res1 = wifi.setWifiEnabled(true);
             int res = wifi.addNetwork(wc);
             System.out.println("WifiPreference: add Network returned " + res );
             boolean b = wifi.enableNetwork(res, true);   
             System.out.println("WifiPreference: enableNetwork returned " + b );  
             boolean es = wifi.saveConfiguration();
             System.out.println("WifiPreference: saveConfiguration returned " + es );
         }

         /// THIS CODE IS NOT COMPELETELY FUNCTIONAL. FOR THAT REASON COMMENTING OUT...
         /*
         WifiManager.WifiLock logk = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL, "RobotLock");
         boolean rs = wifi.reconnect();
         System.out.println("WifiPreference: connection returned " + rs );
         
         int count = 0;
         wifi.setWifiEnabled(true);
         
         ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo networkInfo = null;
         if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
         }
         return networkInfo == null ? false : networkInfo.getState() == NetworkInfo.State.CONNECTED;
         
         while ((wifi.isWifiEnabled() == false) && (networkInfo.getState() != NetworkInfo.State.CONNECTED)) {
             if (count >= 10) {
               System.out.println("Took too long to enable wi-fi, quitting");
               return;
             }
             System.out.println("Still waiting for wi-fi to enable...");
             try {
               Thread.sleep(1000L);
             } catch (InterruptedException ie) {
               // continue
             }
             count++;
         }WifiInfo myWifiInfo = wifi.getConnectionInfo();
         int myIp = myWifiInfo.getIpAddress();

         int intMyIp3 = myIp/0x1000000;
         int intMyIp3mod = myIp%0x1000000;
        
         int intMyIp2 = intMyIp3mod/0x10000;
         int intMyIp2mod = intMyIp3mod%0x10000;
        
         int intMyIp1 = intMyIp2mod/0x100;
         int intMyIp0 = intMyIp2mod%0x100;
        
         String textIp = String.valueOf(intMyIp0)
        		 		+ "." + String.valueOf(intMyIp1)
        		 		+ "." + String.valueOf(intMyIp2)
   		 				+ "." + String.valueOf(intMyIp3);
           
         
         System.out.println("IP address: " + textIp );
         */
     }
}