package com.ctxengine.sensors;

import com.ctxengine.ContextEngine;

/**
 * This class is an IMU sensor class inherited from OnBoardSensor.
 * <p>
 * In particular, this sensor class handles the shaken event.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public class IMU extends OnBoardSensor implements IIMUCtxUpdated {

	/* The name of the sensor thread */
	final private String sensorThreadName = "IMU";

	/* The path where the sensor module executable is stored */
	final private String sensorModulePath = "sensorbin/test";

	/*
	 * The interface to which this sensor module passes events for actual
	 * handling
	 */
	private IIMUCtxUpdated ctxInterface = null;

	/**
	 * This is the constructor.
	 * 
	 * @param IIMUCtxUpdated
	 *            The IIMUCtxUpdated interface that to which this sensor module
	 *            passes events for actual handling.
	 */
	public IMU(IIMUCtxUpdated _ctxInterface) {
		setCtxInterface(_ctxInterface);
	}

	/**
	 * Override abstract function from parent class to update context.
	 * <p>
	 * Shake : the shaken event
	 * 
	 * @param msg
	 *            The sensor message read from stdout.
	 */
	@Override
	protected void handleSensorMsg(String msg) {
		if (msg.compareToIgnoreCase("shake") == 0) {
			this.shakeDetected();
		}
	}

	/**
	 * Override abstract function from parent class to configure the path of the
	 * sensor module executable.
	 * 
	 * @return The path where IMU module executable is stored.
	 */
	@Override
	final protected String getSensorModulePath() {
		return this.sensorModulePath;
	}

	/**
	 * Override abstract function from parent class to configure the name of the
	 * IMU sensor thread.
	 * 
	 * @return The name of the IMU sensor thread.
	 */
	@Override
	final protected String getSensorThreadName() {
		return this.sensorThreadName;
	}

	/******************************************************************
	 * IIMUCtxUpdated interface
	 ******************************************************************/
	
	/**
	 * This function implements the shakeDetected function in the IIMUCtxUpdated
	 * interface by passing the event to the context engine.
	 */
	@Override
	public void shakeDetected() {
		ctxInterface.shakeDetected();
	}

	/**
	 * This is the local setter of the IIMUCtxUpdated.
	 * 
	 * @param _ctxInterface
	 *            The IIMUCtxUpdated that actually does the event handling.
	 */
	public void setCtxInterface(IIMUCtxUpdated _ctxInterface) {
		this.ctxInterface = _ctxInterface;
	}

}

// From src folder, run javah -jni com.contexts.IMU to generate
// com_contexts_IMU.h file
// Then write the source file and use the following command to compile
// g++ "-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"
// -c com_contexts_IMU.cpp
// To generate binary file, use this command: g++ -framework JavaVM -o test
// imu.o
