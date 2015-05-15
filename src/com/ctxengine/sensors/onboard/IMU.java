package com.ctxengine.sensors.onboard;

import com.ctxengine.ContextEngine;
import com.ctxengine.sensors.OnBoardSensor;
import com.ctxengine.sensors.interfaces.IIMUCtxUpdated;

/**
 * This class is an IMU sensor class inherited from OnBoardSensor.
 * <p>
 * In particular, this sensor class handles the shaken event.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public final class IMU extends OnBoardSensor implements IIMUCtxUpdated {

	/* The name of the sensor thread */
	final private String sensorThreadName = "IMU";

	/* The path where the sensor module executable is stored */
	final private String sensorModulePath = "sensorbin/IMU";

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
		this.ctxInterface = _ctxInterface;
	}

	/******************************************************************
	 * Implementation of abstract methods
	 ******************************************************************/

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
			this.OnBoardIMUShakeDetected();
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
	 * This function implements the OnBoardIMUShakeDetected function in the
	 * IIMUCtxUpdated interface by passing the event to the context engine.
	 */
	@Override
	public void OnBoardIMUShakeDetected() {
		ctxInterface.OnBoardIMUShakeDetected();
	}

}
