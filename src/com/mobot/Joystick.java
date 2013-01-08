package com.mobot;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class Joystick {

	public class Head {
		float mRadius;
		float mShiftX;
		float mShiftY;

		public Head(float radius) {
			mShiftX = 0;
			mShiftY = 0;
			mRadius = radius;
		}
		public void render(Canvas canvas) {
			Paint p = new Paint();
			p.setShader(new RadialGradient(mCenterX + mShiftX, mCenterY + mShiftY, 1.6f*mRadius, Color.RED, Color.BLACK, Shader.TileMode.MIRROR));
			canvas.drawCircle(mCenterX + mShiftX, mCenterY + mShiftY, mRadius, p);
			p.setColor(Color.BLACK);
			p.setStyle(Paint.Style.STROKE);		
			p.setStrokeWidth(3.0f); 
			canvas.drawCircle(mCenterX + mShiftX, mCenterY + mShiftY, mRadius, p);
		}
	}

	float mCenterX;
	float mCenterY;
	float mRadius;
	Head mHead; 
	
	
	
	public Joystick(float centerX, float centerY, float baseRadius, float headRadius) {
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = baseRadius;
		mHead = new Head(headRadius);
	}
	
	public void step() {
		
	}
	
	public boolean checkTouch(float x, float y) {
		return Math.pow(x - (mCenterX + mHead.mShiftX), 2) + Math.pow(y - (mCenterY + mHead.mShiftY), 2) <= Math.pow(mHead.mRadius, 2);
	}
	
	public void moveHead(float deltaX, float deltaY) {
		mHead.mShiftX += deltaX;
		mHead.mShiftY += deltaY;
		float dist = (float) Math.sqrt(Math.pow(mHead.mShiftX, 2) + Math.pow(mHead.mShiftY, 2));
		if(dist > maxOffset()) {
	        float factor = dist/maxOffset();
	        mHead.mShiftX /= factor;
			mHead.mShiftY /= factor;
		}
	}
	
	public void resetHead()
	{
		mHead.mShiftX = 0;
		mHead.mShiftY = 0;
	}
	
	public void render(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(130, 130, 130, 130);
		canvas.drawCircle(mCenterX, mCenterY, mRadius, p); 
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.STROKE);		
		p.setStrokeWidth(2.0f); 
		canvas.drawCircle(mCenterX, mCenterY, mRadius, p);
		mHead.render(canvas);
	}
	
	float radius() { return mRadius; }
	float shiftX() { return mHead.mShiftX; }
	float shiftY() { return mHead.mShiftY; }
	float maxOffset() { return mRadius - mHead.mRadius/2; }
}
