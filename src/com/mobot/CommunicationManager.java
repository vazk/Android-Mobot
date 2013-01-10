package com.mobot;


public class CommunicationManager {

    static {
    	System.loadLibrary("RobotComm");
    } 

    public native boolean connect(String ip, int port);
	public native int write(byte[] data, int size);
	public native int read(byte[] da, int size);
	public native void close();


	public native boolean commandAck();
	public native boolean commandQuit();
	public native boolean commandStop();
	public native boolean commandDrive(float left, float right);
	
}