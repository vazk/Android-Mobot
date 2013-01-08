package com.mobot;

import android.app.Activity;
import android.os.Bundle;

public class JoystickActivity extends Activity {
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    JoystickView jv = new JoystickView(this);
        setContentView(jv);   
	}

}