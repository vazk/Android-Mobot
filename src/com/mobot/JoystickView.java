package com.mobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class JoystickView extends View
{
    
    volatile boolean touched = false;
    volatile float touched_x, touched_y;
    private Joystick mJoystick;
    private CommunicationManager mComm;
    
	public JoystickView(Context context) {
		super(context);
        mJoystick = new Joystick(600, 300, 170, 80);
        mComm = new CommunicationManager();
        boolean status = mComm.connect("192.168.2.9", 9999);
        System.out.println("status = " + status);
	}

    public boolean onTouchEvent(MotionEvent event) {
    	int action = event.getAction();
    	switch(action){
    	case MotionEvent.ACTION_DOWN:    		
        	touched_x = event.getX();
        	touched_y = event.getY();
        	touched = mJoystick.checkTouch(touched_x, touched_y);
    		break;
    	case MotionEvent.ACTION_MOVE:
    		if(touched) {
            	float tx = event.getX();
            	float ty = event.getY();
            	updateJoystickHeadPos(tx, ty);
        		touched_x = tx;
        		touched_y = ty;
    		}
    		break;
    	case MotionEvent.ACTION_UP:
    		resetJoystickHeadPos();
    		touched = false;
    		break;
    	case MotionEvent.ACTION_CANCEL:
    		touched = false;
    		break;
    	case MotionEvent.ACTION_OUTSIDE:
    		touched = false;
    		break;
    	default:
    	}
    	return true;
    }
    
    protected void onDraw(Canvas canvas) {
    	mJoystick.render(canvas);
    }
    
    float clampOne(float x) 
    {
        if(x > 1) x = 1;
	    else 
	   	if(x < -1) x = -1;
        return x;        	
    }
    
    void updateJoystickHeadPos(float tx, float ty)
    {
		mJoystick.moveHead(tx-touched_x, ty-touched_y);
	    float str_mag = (-mJoystick.shiftY())/mJoystick.maxOffset();
	    float rot_mag = (mJoystick.shiftX())/mJoystick.maxOffset();
	    float left = str_mag - rot_mag/3;
	    float right = str_mag + rot_mag/3;
    
	    left = clampOne(left);
	    right = clampOne(right);
	    
	    System.out.println(left + ", " + right);
	    mComm.commandDrive(left, right);
    	invalidate();
    }
    
    void resetJoystickHeadPos()
    {
		mJoystick.resetHead();
	    mComm.commandDrive(0, 0);
    	invalidate();
    }
}