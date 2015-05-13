package com.ctxengine;

import com.ctxengine.sensors.onboard.Camera;
import com.ctxengine.sensors.onboard.ICameraCtxUpdated;
import com.ctxengine.sensors.onboard.IIMUCtxUpdated;
import com.ctxengine.sensors.onboard.IMU;

/**
 * @author shinda
 * 
 */
public class ContextEngine implements IIMUCtxUpdated, ICameraCtxUpdated {

	private static IMU imuSensor;
	private static Camera camSensor;

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

	/**
	 * This function instantiates the static camSensor variable and starts the
	 * sensing service.
	 */
	public void startCam() {
		if (camSensor == null) {
			camSensor = new Camera(this);
			camSensor.startSensor();
		}
	}

	/**
	 * This function terminates the camera sensing service.
	 */
	public void stopCamera() {
		if (camSensor != null) {
			camSensor.stopSensor();
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

	/******************************************************************
	 * ICameraCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function handles the face detected event when a face is detected
	 * from camera module.
	 */
	@Override
	public void faceDetected() {
		System.out.println("Face detected");
	}

}
