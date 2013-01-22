package com.mobot;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class APItemView extends View {
	private ScanResult mScanResult;
	private Joystick   mJoystick;
	private Paint      mPaint;
	
	public APItemView(Context context) {
        super(context);      
        setupPaint();
    }
	
	public APItemView(Context context, ScanResult sr) {
        super(context);
        mScanResult = sr;
        setupPaint();
    }
	
	public APItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }
    
	public void setScanResult(ScanResult sr) {
		mScanResult = sr;
		mJoystick = new Joystick();
		mJoystick.setHeadColor(Color.RED);
	}
	
	public ScanResult getScanResult() {
		return mScanResult;
	}
	
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        final int H = canvas.getHeight();
        
        final float MAX_RAD_FACTOR = 0.35f;
        final float MIN_RAD_FACTOR = 0.04f;
        final float CENTER_X_FACTOR = 0.625f;
        final float CENTER_Y_FACTOR = 0.47f;
        final float BASE_RAD_FACTOR = 0.39f;
        final float TEXT_SIZE_FACTOR = 0.33f;
        final float TEXT_X_FACTOR = 1.328f;
        final float TEXT_Y_FACTOR = 0.59f;
        
		int rad = mapDBtoRadius(MAX_RAD_FACTOR*H, MIN_RAD_FACTOR*H, mScanResult.level);        
		mJoystick.setup((int)(CENTER_X_FACTOR*H), (int)(CENTER_Y_FACTOR*H), 
						(int)(BASE_RAD_FACTOR*H), rad);
		mPaint.setTextSize((int)(TEXT_SIZE_FACTOR*H));
        canvas.drawText(mScanResult.SSID, TEXT_X_FACTOR*H, TEXT_Y_FACTOR*H, mPaint);
        canvas.save();        
        if(mJoystick != null) {
        	mJoystick.render(canvas);
        }
    }
    
	private void setupPaint() {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.MONOSPACE);		
	}

	private int mapDBtoRadius(float max, float min, float level) {
    	// see this link for details...
    	// http://www.anandtech.com/show/3821/iphone-4-redux-analyzing-apples-ios-41-signal-fix
    	final float MAX_BAR_DB = -76;
    	final float MIN_BAR_DB = -107;
    	if(level > MAX_BAR_DB) return (int)max;
    	if(level < MIN_BAR_DB) return (int)min;
    	return (int)(min + (MAX_BAR_DB-level) / (MAX_BAR_DB-MIN_BAR_DB) * (max-min));
    }
}