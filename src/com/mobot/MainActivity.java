package com.mobot;
 
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
 

import java.util.ArrayList;
import java.util.List;

import com.mobot.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Spinner;

public class MainActivity extends Activity {
    ArrayAdapter adapter;
	ListView mainList;
	
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);       
       setContentView(R.layout.main);
       
       
       final Context context = this;       
		Button button = (Button) findViewById(R.id.btnStart);
		button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, JoystickActivity.class);
			    startActivity(intent);
			}
		});
       
       mainList = (ListView) findViewById(R.id.listView);
       
       ArrayList<Object> test = new ArrayList<Object>();
       adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, test);
       mainList.setAdapter(adapter);
       
       mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       receiverWifi = new WifiReceiver();
       registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       mainWifi.startScan();
       
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();
        return super.onMenuItemSelected(featureId, item);
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){
                adapter.add((wifiList.get(i)).toString());
            }
        }
    }
}
