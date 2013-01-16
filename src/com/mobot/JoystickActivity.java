package com.mobot;

import com.mobot.JoystickView.Status;

import android.app.Activity;
import android.os.Bundle;

public class JoystickActivity extends Activity {
	
    private RobotDriver			 mRobot;
    private JoystickView         mJView;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRobot = new RobotDriver();
	    mJView = new JoystickView(this, mRobot);
	    setContentView(mJView);	    
	    mRobot.start("192.168.2.6", 9999);
	}
	
	public void onStart() {		
        super.onStart();
	}
     
    public void onStop() {
    	super.onStop();
    	
    	System.out.println("Communication: disconnect.");
    	mRobot.stop();
    }
}