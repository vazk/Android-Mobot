package com.mobot;

import com.mobot.JoystickView.Status;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class JoystickActivity extends Activity {
	
    private JoystickView    mJView;
    private Handler 	    mHandler;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    mJView = new JoystickView(this);
	    setContentView(mJView);	    
	    mHandler = new Handler();
	    autoRefresh();
	}
	
	public void onStart() {		
        super.onStart();
	}

    public void onPause() {
    	super.onPause();
    	stopAutoRefresh();
    }

    public void onResume() {
    	super.onResume();
    	autoRefresh();
    }
    
    public void onStop() {
    	super.onStop();    	
    	System.out.println("Communication: disconnect.");
    }
    
    private void autoRefresh() {
        mHandler.postDelayed(new Runnable() {
                 public void run() {
                	 JoystickView.Status status = (RobotDriver.sRobot.isRunning() ? Status.ENABLED : Status.DISABLED);
                	 mJView.setStatus(status);
                	 mJView.invalidate();
                     autoRefresh();                
                 }
             }, 1000);
    }
    
    private void stopAutoRefresh() {
    	mHandler.removeCallbacksAndMessages(null);
    }
}