package com.mobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;


public class JoystickView extends View
{    
    volatile boolean touched = false;
    volatile float touched_x, touched_y;
    private Joystick mJoystick;
    
    public enum Status {
    	ENABLED,
    	DISABLED
    }
    
	public JoystickView(Context context) {
		super(context);
		mJoystick = new Joystick();
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
	    int sign = 1;//str_mag >= 0 ? 1 : -1;
	    float left = str_mag - sign*rot_mag/2;
	    float right = str_mag + sign*rot_mag/2;
    
	    left = clampOne(left);
	    right = clampOne(right);
	    
	    System.out.println(left + ", " + right);
	    RobotDriver.sRobot.commandDrive(left, right);
    	invalidate();
    }
    
    void resetJoystickHeadPos()
    {
		mJoystick.resetHead();
	    System.out.println(0 + ", " + 0);
		RobotDriver.sRobot.commandDrive(0, 0);
    	invalidate();
    }
    
    void setStatus(Status st) 
    {
    	int col = (st == Status.ENABLED) ? Color.RED : Color.GRAY;
    	mJoystick.setHeadColor(col);
    }
    
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
		int jRadius = (int) (Math.min(xNew, yNew) * 0.35);
        mJoystick.setup(xNew/2, yNew/2, jRadius, jRadius/2);  
   }

}