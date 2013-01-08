package com.mobot;


public class CommunicationManager {

    static {
    	System.loadLibrary("RobotComm");
    } 

    public native boolean connect(String ip, int port);
	public native int write(byte[] data, int size);
	public native int read(byte[] da, int size);
	public native void close();


	public native void commandQuit();
	public native void commandStop();
	public native void commandDrive(float left, float right);
	
}