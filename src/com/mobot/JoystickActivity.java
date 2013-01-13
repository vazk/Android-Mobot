package com.mobot;

import com.mobot.JoystickView.Status;

import android.app.Activity;
import android.os.Bundle;

public class JoystickActivity extends Activity {
	
    private CommunicationManager mComm;
    private HeartBitThread 		 mHBThread;
    JoystickView 			     mJView;
    
    private class HeartBitThread extends Thread {
    	volatile boolean shouldStop;    	
    	public void run() {
    		shouldStop = false;
    		while(!shouldStop) {
   				if(mComm.isConnected() == false) {
   					connect();
   	    			sleep(300);
   					continue;
   				}    				
   				mComm.commandHeartBeat();   					
    			System.out.println("Sending ping...");
    			sleep(150);
    		}
    	}    	
		public void sleep(int milliseconds)
		{ 
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
    	}
    	public void shutdown() {
    		shouldStop = true;
    	}    	
    }

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComm = new CommunicationManager();
	    mJView = new JoystickView(this, mComm);
	    mHBThread = new HeartBitThread();
        setContentView(mJView);         
	}
	
	public void onStart() {	
        super.onStart();
        mHBThread.start();		
	}
     
    public void onStop() {
    	super.onStop();
    	System.out.println("Communication: disconnect.");
    	mHBThread.shutdown();
    	mComm.close();
    }
    
	private void connect() {
		boolean status = mComm.connect("192.168.2.6", 9999);
		System.out.println("Communication: connecting [" + status + "].");
		Status st = Status.DISABLED;		
		if(status == true) {
			st = Status.ENABLED;
		}
		mJView.setStatus(st);
		//mJView.resetJoystickHeadPos();
		mJView.postInvalidate();
	}
	
}