package com.mobot;

public class RobotDriver {

    static {
    	System.loadLibrary("RobotComm");
    } 

    RobotDriver() {
    	init();
    }
    /// Initialization
    public native boolean init();
    
    /// State change and query
    public native boolean start(String ip, int port);
	public native void stop();
	public native boolean isRunning();
	
	/// Robot control
	public native boolean commandDrive(float left, float right);
	
}