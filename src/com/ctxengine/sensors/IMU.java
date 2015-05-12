/**
 * 
 */
package com.ctxengine.sensors;

/**
 * This class is an IMU sensor class inherited from OnBoardSensor.
 * <p>
 * In particular this sensor class handles the shaken event.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public class IMU extends OnBoardSensor {

	final private String sensorThreadName = "IMU";
	final private String sensorModulePath = "sensorbin/test";
	
	/**
	 * 
	 */
	public IMU() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void handleSensorMsg(String msg) {
		System.out.println(msg);
	}

	@Override
	protected String getSensorModulePath() {
		return this.sensorModulePath;
	}

	@Override
	protected String getSensorThreadName() {
		return this.sensorThreadName;
	}

}

// From src folder, run javah -jni com.contexts.IMU to generate com_contexts_IMU.h file
// Then write the source file and use the following command to compile
// g++ "-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers" 
// -c com_contexts_IMU.cpp
// To generate binary file, use this command: g++ -framework JavaVM -o test imu.o
