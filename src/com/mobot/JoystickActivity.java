package com.mobot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    			boolean status = mComm.commandAck();
    			if(status == false) {
    				attemptReconnect();
    				shouldStop = true;
    			}
    			try {
    				Thread.sleep(150);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}    			
    			System.out.println("Sending ping...");
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
        boolean status = mComm.connect("192.168.2.6", 9999);
        System.out.println("Communication: connecting [" + status + "].");	    
	    mHBThread.start();
	}
     
    public void onStop() {
    	super.onStop();
    	System.out.println("Communication: disconnect.");
    	mHBThread.shutdown();
    	mComm.close();
    }

	public void attemptReconnect() {
		System.out.println("Going back to reconnect...");		
		final Context context = this; 
		Intent intent = new Intent(context, MainActivity.class);
		startActivity(intent);
	}

}