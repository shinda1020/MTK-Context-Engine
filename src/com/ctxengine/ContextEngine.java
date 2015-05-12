/**
 * 
 */
package com.ctxengine;

import com.ctxengine.sensors.IIMUCtxUpdated;
import com.ctxengine.sensors.IMU;

/**
 * @author shinda
 * 
 */
public class ContextEngine implements IIMUCtxUpdated {

	private static IMU imuSensor;

	/**
	 * Naive constructor
	 */
	public ContextEngine() {

	}

	/******************************************************************
	 * Sensor control
	 ******************************************************************/

	/**
	 * This function instantiates the static imuSensor variable and starts the
	 * sensing service.
	 */
	public void startIMU() {
		if (imuSensor == null) {
			imuSensor = new IMU(this);
			imuSensor.startSensor();
		}
	}

	/**
	 * This function terminates the IMU sensing service.
	 */
	public void stopIMU() {
		if (imuSensor != null) {
			imuSensor.stopSensor();
		}
	}

	/******************************************************************
	 * IIMUCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function handles the shaken event when a shake is detected from IMU
	 * module.
	 */
	@Override
	public void shakeDetected() {
		System.out.println("Shake detected");
	}

}
