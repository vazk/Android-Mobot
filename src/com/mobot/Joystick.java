package com.mobot;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;

public class Joystick {

	public class Head {
		int mRadius;
		int mShiftX;
		int mShiftY;
		int mColor;
		Paint mPaint;

		public Head() {
			mShiftX = 0;
			mShiftY = 0;
			mRadius = 0;
			mColor = Color.GRAY;
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
		}
		public void setRadius(int radius) {
			mRadius = radius;
		}
		
		public void render(Canvas canvas) {
			mPaint.setShader(new RadialGradient(mCenterX + mShiftX, mCenterY + mShiftY, 1.6f*mRadius, mColor, Color.BLACK, Shader.TileMode.MIRROR));
			canvas.drawCircle(mCenterX + mShiftX, mCenterY + mShiftY, mRadius, mPaint);
			canvas.drawCircle(mCenterX + mShiftX, mCenterY + mShiftY, mRadius, mPaint);
		}
	}

	int mCenterX;
	int mCenterY;
	int mRadius;	
	Head mHead; 
	Paint mPaint;
	
	public Joystick()
	{
		mHead = new Head();
		mPaint = new Paint();
		setup(0, 0, 0, 0);
	}
	
	public void setup(int centerX, int centerY, int baseRadius, int headRadius) 
	{
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = baseRadius;
		mHead.setRadius(headRadius);
	}
	
	public boolean checkTouch(float x, float y) {
		return Math.pow(x - (mCenterX + mHead.mShiftX), 2) + 
			   Math.pow(y - (mCenterY + mHead.mShiftY), 2) <= Math.pow(mHead.mRadius, 2);
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
	
	public void setHeadColor(int col)
	{
		mHead.mColor = col;
	}
	
	public void resetHead()
	{
		mHead.mShiftX = 0;
		mHead.mShiftY = 0;
	}
	
	public void render(Canvas canvas) {
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(130, 130, 130, 130);
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint); 
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);		
		mPaint.setStrokeWidth(2.0f);
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
		mHead.render(canvas);
	}
	
	float radius() { return mRadius; }
	float shiftX() { return mHead.mShiftX; }
	float shiftY() { return mHead.mShiftY; }
	float maxOffset() { return mRadius - mHead.mRadius/2; }
}
